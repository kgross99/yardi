import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.lang.InterruptedException;
import java.io.UnsupportedEncodingException;
import java.lang.NumberFormatException;
import java.util.ArrayList;
import java.util.Iterator;
import java.net.UnknownHostException;
import java.net.MulticastSocket;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This is the primary class for the proxy of things.The main
 * method creates a thread for the browser proxy and threads for
 * each aft to be serviced. The number of afts to be serviced must be
 * passed to the proxy as a command-line argument. If no argument is
 * given, one aft is assumed.
 * 
 * The browser proxy spawns a thread for each browser that attempts a
 * connection.
 * 
 * WE HAVE TWO CACHE MANAGEMENT OPTIONS, TO BE DECIDED BY THE INSTRUCTOR:
 * 
 * OPTION = UPDATE DOCUMENT WHEN CACHE LIFE EXPIRES:
 * When a resource is cached, an expiration time is established.
 * If a browser requests the resource before its expiration time, the
 * resource is served from the cache. If the resource is requested
 * after the expiration time, the proxy thread queues an update
 * request with the appropriate aft. The next time the aft awakes, it
 * responds to update requests. The browser connection thread does not
 * respond to the browser until the cache receives an updated document.
 * The average time a browser is kept waiting is half the aft sleep
 * time. The longest wait time is the sleep time of the aft.
 * 
 * OPTION = UPDATE DOCUMENT WHEN TIMER EXPIRES:
 * A browser's request for a resource is served from a cache,
 * without referring to an expiration time. The proxy thread is
 * tasked with making sure the cache documents do not expire. Each
 * resource is associated with a timer. When a resource's timer expires,
 * the proxy thread queues an update request with the appropriate aft.
 * The next time the aft awakes, it responds to update requests and the
 * cache is updated.
 * 
 * COMMENTS:
 * Serving a resource, such as the time-of-day, that expires the
 * instant it is created does not seem to be appropriate for a
 * sleeping/waking aft. It only makes sense that the data served by an
 * aft would have a longer life, if only by a little, than the sleep
 * time of the aft. Much needless complexity would be removed from the
 * project by serving data that has a lifetime that exceeds the sleep
 * time of the aft. Good candidates would be temperature and pressure.
 * END COMMENTS
 * 
 * The thread that services an aft waits to receive a packet from an aft
 * saying it is ready to respond to requests for document updates. The
 * aft servicing thread then dequeues a request and sends a packet to the
 * aft that requests some specific resource. If the aft responds within a
 * timeout interval, the response is taken as an acknowledgement of the
 * request. Otherwise, the request is repeated a couple of time, after
 * which it is assumed that the aft has gone back to sleep. The request is
 * returned to the queue. The aft servicing thread repeats this activity
 * until the queue is exhausted, or the aft stops responding. The aft
 * responds to duplicate requests by resending the requested packet,
 * under the assumption that the previous response was lost. This is the
 * limit of attempts to make the data transfers reliable.
 */
public class pft {
	
	/**
	 * Per discussions 2015-04-28, we are using an ConcurrentLinkedDeque of
	 * AftServicer objects as the resource cache. Each aft will have an
	 * object in the array. This will be used to pass data from aft-servicer to
	 * browser-servicer. Aft-servicer threads will put data from afts in the cache.
	 * The ConcurrentLinkedDeque will be passed to browser-servicer threads via the
	 * constructor.
	 */
	private volatile static ConcurrentLinkedDeque<AftServicer> aftCache;
	
	/**
	 * We require a list to store all of the AftServicer threads that have
	 * been created to deal with aft's.
	 */
	private volatile static ConcurrentLinkedDeque<Thread> aftThreads;
	
	/**
	 * This is a simplistic model of a resource cache, but it is close to
	 * what is required. A similar array would be required for each
	 * document to be served.
	 */
	private static boolean DEBUG;
	
	/**
	 * A constant string that is the prefix used by a valid aft in its broadcast
	 * packet as it attempts to find an appropriate pft.
	 */
	private static final String AFT_GREETING = "kcna";
	
	/**
	 * Port that browser proxy listens on for incoming browser connection attempts.
	 */
	private static int browserProxyListenerPort;
	private static int aftBroadcastListenerPort;
	
	/**
	 * The main method creates a thread for the browser proxy and threads for
	 * each aft to be serviced. The number of afts to be service must be
	 * passed to this method as a command-line argument. If no argument is
	 * given, one aft is assumed.
	 * @param args
	 */
	public static void main(String[] args) {
        /* usage message: give the use the requirements for
         * command-line arguments
         */
        String usage = new String("Usage: java pft\n" +
                "<browser proxy listner port number>\n" +
                "<aft broadcast listner port number>\n");
        if (args.length != 2) {
            System.err.println(usage);
            System.exit(1);
        }
        
        /* get the command-line arguments */
        try {
        	browserProxyListenerPort = Integer.parseInt(args[0]);
        	aftBroadcastListenerPort = Integer.parseInt(args[1]);
        } catch(NumberFormatException ex) {
            System.err.println(usage);
            System.exit(1);
        }
        
		DEBUG = false;
		
		/* create and start the thread that listens for aft udp
		 * broadcasts with which an aft attempts to connect to a suitable pft */
		Thread abl = new Thread(new AftBroadcastListener(aftBroadcastListenerPort));
		abl.start();
		
		/* create and start the thread that listens for tcp/http
		 * browser requests for data from an aft */
		Thread bp = new Thread(new BrowserProxy(browserProxyListenerPort));
		bp.start();
		
		/* the main procedure that we are in now will sleep until a
		 * timeout has expired. the timeout can be long (or forever) for
		 * production use of the program, or it can be short for
		 * debugging */
		while(bp.isAlive() && areAllAftsAlive()) {
			try {
				if(DEBUG) {
					Thread.sleep(1*60*1000); // wait a minute
				} else {
					Thread.sleep(24*60*60*1000); // wait a day
				}
			} catch(InterruptedException ex) {
				System.exit(1);
			}
			
			/* when the timeout expires, it's time to stop all threads by
			 * calling their interrupt() method. interrupt() will cause an
			 * InterruptedException in the called thread. all threads
			 * have handlers for it that allow for graceful shutdown. */
			bp.interrupt();
			Iterator<Thread> iter = aftThreads.iterator();
			while(iter.hasNext()) {
				Thread t = iter.next();
				t.interrupt();
			}
			
			/* after the threads have been told to stop, we need to
			 * give them a little time to close gracefully */
			try {
				Thread.sleep(2*1000);
			} catch(InterruptedException ex) {
				System.exit(1);
			}
			
			/* wait until each thread is stopped. join will return when the
			 * called thread is stopped. */
			try {
				bp.join();
				iter = aftThreads.iterator();
				while(iter.hasNext()) {
					Thread t = iter.next();
					t.join();
				}
			} catch(InterruptedException ex) {
				System.exit(1);
			}
		}
		/* done at last */
	}
	
	/**
	 * Determine whether any of the aft servicing threads is no
	 * longer active.
	 * @param afts Array of aft servicing threads.
	 * @return true if all threads are alive, otherwise false.
	 */
	private static boolean areAllAftsAlive() {
		Iterator<Thread> iter = aftThreads.iterator();
		while(iter.hasNext()) {
			Thread t = iter.next();
			if(!t.isAlive()) {
				return false;
			} else {}
		}
		return true;
	}
	
	/**
	 * Private class that creates the thread that services browser
	 * requests for resources provided by the afts.
	 * For each connection attempt, spawns a thread to serve the
	 * requested resources.
	 */
	private static class BrowserProxy implements Runnable {
		
		private ServerSocket bpListener;
		
		/**
		 * Constructs a tcp server socket that listens for
		 * connection requests.
		 * @param port number of port on which to listen.
		 */
		public BrowserProxy(int port) {
			try {
				bpListener = new ServerSocket(port);
				if(DEBUG) bpListener.setSoTimeout(3*60*1000);
			} catch (IOException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
	
		/**
		 * Thread run method listens for a connection attempt. When
		 * connection is accepted, creates a thread to serve resources
		 * from a document cache.
		 */
		@Override
		public void run() {
			try {
				while(true) {
					if(DEBUG) System.out.println("Browser Proxy Listening");
					Socket connection = bpListener.accept();
					if(DEBUG) System.out.println("Browser Proxy Serving");
					(new Thread(new BrowserProxyService(connection, DEBUG, aftCache))).start();
					if(Thread.interrupted()) {
						if(DEBUG) System.out.println("Browser Proxy Interrupted");
						bpListener.close();
						break;
					} else {}
					}
			} catch(SocketTimeoutException ex1) {
				if(DEBUG) System.out.println("Browser Proxy timed out");
				try {
					bpListener.close();
				} catch (IOException ex2) {
					ex2.printStackTrace();
					System.exit(1);
				}
			} catch (IOException ex3) {
				ex3.printStackTrace();
				System.exit(1);
			}
		}
	}
		
	/**
	 * Private class that creates the thread that services browser
	 * requests for resources provided by the afts.
	 * For each connection attempt, spawns a thread to serve the
	 * requested resources.
	 */
	private static class AftBroadcastListener implements Runnable {
		
		private MulticastSocket aftBcastListener;
		private final int MAX_BUFFER = 1024;
		private byte[] bytes;
		private DatagramPacket packet;
		private InetAddress group;
		
		/**
		 * Constructs a tcp server socket that listens for
		 * connection requests.
		 * @param port number of port on which to listen.
		 */
		public AftBroadcastListener(int udpPort) { // throws InterruptedException {
			bytes = new byte[MAX_BUFFER];
			try {
				aftBcastListener = new MulticastSocket(udpPort);
				group = InetAddress.getByName("127.0.0.0");
				aftBcastListener.joinGroup(group);
				if(DEBUG) aftBcastListener.setSoTimeout(3*60*1000);
			} catch (UnknownHostException ex) {
				//throw(new InterruptedException());
				ex.printStackTrace();
				System.exit(1);
			} catch (SocketException ex) {
				//throw(new InterruptedException());
				ex.printStackTrace();
				System.exit(1);
			} catch (IOException ex) {
				//throw(new InterruptedException());
				ex.printStackTrace();
				System.exit(1);
			}
		}
	
		/**
		 * Thread run method listens for a connection attempt. When
		 * connection is accepted, creates a thread to serve resources
		 * from a document cache.
		 */
		@Override
		public void run() {
			try {
				while(true) {
					packet = new DatagramPacket(bytes, bytes.length);
					if(DEBUG) System.out.println("AftBroadcastListener Listening");
					aftBcastListener.receive(packet);
					if(DEBUG) System.out.println("AftBroadcastListener handling aft");
					(new Thread(new AftAcceptanceService(packet, aftCache, aftThreads,
							AFT_GREETING, DEBUG))).start();
					if(Thread.interrupted()) {
						if(DEBUG) System.out.println("AftBroadcastListener Interrupted");
						aftBcastListener.leaveGroup(group);
						aftBcastListener.close();
						break;
					} else {}
					}
			} catch(SocketTimeoutException ex) {
				if(DEBUG) System.out.println("AftBroadcastListener timed out");
				try {
					aftBcastListener.leaveGroup(group);
					aftBcastListener.close();
				} catch (IOException ex1) {
					ex1.printStackTrace();
					System.exit(1);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
	}
	
//	static synchronized void addAft(AftServicer aft) {
//		aftCache.add(aft);
//		if(DEBUG) System.out.println("aft: " + aft.getAftId() +"added.");
//	}
	
	/* these are examples of synchronized methods in case we need some
	static synchronized void setIndexDoc(String doc) {
		timeDoc = doc;
		System.out.println("timeDoc: " + timeDoc);
	}
	static synchronized String getIndexDoc() {
		return timeDoc;
	}

	
	}*/

}
