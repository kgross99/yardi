import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.net.InetAddress;
import java.nio.charset.Charset;



//IDEA: an AftServicer will be running on each thread, one for each aft we have.  OR have an arraylist of AftServicers - 
//  when a request comes in, spin that off on a thread and handle it

//MUCH LEFT TO DO - basics, AftServicer will be the connection point for everything aft related.

//Need to add an aft?
// - will add the aft & a copy of all it's pages

//Need to find an aft's page?
// - will search it's array, find the aft, find the page, check the timeout (if timed out 
//  request new page from aft on next wake, otherwise return the page and move that
//  page to front of cache)

//TODO have method for updating the index page (aft connects, aftServicer created, 
//  now pft should call AftServicer.updateIndex() - to return what should display
//  on the main index page)



public class AftServicer implements Runnable {
	private String aftName, aftIndexPage;
	private InetAddress ipAddr;
	private volatile static int id = 0;
	private int aftId, aftPort, localPort;
	private ConcurrentLinkedDeque<String> workQueue; // List of all pages requested, no duplicates

	ConcurrentLinkedDeque<PageWrapper> allAftPages;
	PageWrapper tempPageWrapper;
	DatagramPacket packet;
	private final int BUFFER_SIZE = 1024;
	byte[] bytes = new byte[BUFFER_SIZE];
	
	private DatagramSocket socket;
	private AftPacket aftReq;
	private AftPacket pftResp;

	private boolean DEBUG;
	
	/**
	 * Constructor, initializes variables. Reads and stores the aft's
	 * information
	 * @param aftName Name of Aft (used on pft's main index page)
	 * @param aftIpAddress Aft's IP address
	 * @param aftPort Aft's Port Number
	 * @param aftIndexPage
	 */
	public AftServicer(DatagramPacket packet) {
		workQueue = new ConcurrentLinkedDeque<String>();
		aftId = getNextAftIDNum();
		allAftPages = new ConcurrentLinkedDeque<PageWrapper>();
		this.packet = packet;
		
		try {
			socket = new DatagramSocket();
			if(DEBUG) socket.setSoTimeout(1*60*1000);
		} catch (SocketException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		
    	/* respond to the aft */
		aftReq = new AftPacket(packet.getData());
		pftResp = new AftPacket(false, false, false, "", "kcna",
				aftReq.getAftName(), aftReq.getAftGreeting(), aftId,
				aftReq.getTimeoutLength(), "");
		bytes = pftResp.encode();
    	packet = new DatagramPacket(bytes, bytes.length, packet.getAddress(), packet.getPort());
		try {
			socket.send(packet);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		/* aft now has its id number, the name of the pft and in the datagram packet, the
		 * pft's ip address and port number */
	}
	
	/**
	 * Thread run method listens for a datagram from the aft. It updates
	 * data from the aft that the browser servicer thread can access.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				/** wait to receive the first packet from the aft notifying
				 * this aft servicing thread that the aft is awake and ready to
				* respond to document update requests */
				bytes = new byte[BUFFER_SIZE];
				packet = new DatagramPacket(bytes, bytes.length);
				socket.receive(packet);
			} catch(SocketTimeoutException ex) {
				if(DEBUG) System.out.println("aftID: " + aftId + " timed out");
				break;
			} catch (IOException ex) {
				if(DEBUG) System.out.println("Err blocking for aft datagram");
			}
			
			//TODO: packet just received asks: do you have work?
			
			Iterator<String> iter = workQueue.iterator();
			while(iter.hasNext()) {
				//TODO: put work requests into packet and send.
				try {
					socket.send(packet);
				} catch(IOException ex) {
					;
				}
				
				//TODO: wait to receive processed work
				try {
					/** wait to receive the first packet from the aft notifying
					 * this aft servicing thread that the aft is awake and ready to
					* respond to document update requests */
					bytes = new byte[BUFFER_SIZE];
					packet = new DatagramPacket(bytes, bytes.length);
					socket.receive(packet);
				} catch(SocketTimeoutException ex) {
					if(DEBUG) System.out.println("aftID: " + aftId + " timed out");
					break;
				} catch (IOException ex) {
					if(DEBUG) System.out.println("Err blocking for aft datagram");
				}
				
				//TODO: distribute aft resource information to appropriate data structures.
			}
		}
	}	
	
	/**
	 * Used to obtain the aftName.
	 * 
	 * @return aftName
	 */
	public int getAftId() {
		return this.aftId;
	}

	/**
	 * Whenever a browser requests a page
	 * 
	 * @param pageName
	 * @param pageContents
	 * @param timeoutLength
	 */
	public void addPage(String pageName, String pageContents, long timeoutLength) {
		tempPageWrapper = new PageWrapper(pageName, pageContents, timeoutLength);
		allAftPages.add(tempPageWrapper);
	}

	/**
	 * Method that returns the requested page as a String. If the page has timed
	 * out (or not cached yet) it will queue the request for the next Aft Poll -
	 * then update it's local copy of the page and timeout time. Finally it
	 * returns the page as a String. If the page does not exist, it will return
	 * a 404 page.
	 * 
	 * @param pageName
	 *            Name of page to be retrieved
	 * @return page contents
	 */
	public String getPage(String pageName) {
		int size = allAftPages.size();
		for (int i = 0; i < size; i++) {// loop through all pages
			if (pageName == allAftPages.get(i).pageName) {
				if (allAftPages.get(i).checkTimeout()) {
					return waitForPage(allAftPages.get(i));
				} else {
					// Return page, move to front of list
					allAftPages.remove(i);
					allAftPages.add(0, tempPageWrapper);
					return tempPageWrapper.pageContents;
				}
			}
		}
		if (!workQueue.contains(pageName)) {// TODO queue the page request
			workQueue.add(pageName);
		}

		// TODO *** is the aft returning 404 if page doesn't exist? Aft should
		// send back a correct index page so we don't have this issue
		return "";// TODO return correct value
	}

	/**
	 * If we are waiting for a page to be updated, loop and sleep until the page
	 * has been updated, then return the page contents.
	 * 
	 * @param aftPage
	 *            PageWrapper of current page
	 * @return Page Contents
	 */
	private String waitForPage(PageWrapper aftPage) {
		while (aftPage.checkTimeout()) {
			// loop and sleep until page has been updated
			try {
				wait(100);
			} catch (InterruptedException e) {
				System.out.println("waitForPage in AftServicer failed");// TODO
																		// debug
				e.printStackTrace();
			}
		}
		return aftPage.pageContents;
	}

	/**
	 * Method used when an aft poll's for work
	 */
	private void pollWork() {
		if (workQueue.size() > 0) {
			// TODO Work to be done
		} else {
			// TODO no work
		}
	}

	/**
	 * Page wrapper holds the aft's page names, contents, and timeoutlength
	 * Allows for easy storage and retrieval by AftServicer
	 * 
	 * @author Cordell
	 * 
	 */
	private class PageWrapper {
		private String pageName, pageContents;
		private long timeoutLength, whenTimeout;

		/**
		 * Constructor, stores information about the page
		 * 
		 * @param pageName
		 *            name of the page
		 * @param pageContents
		 *            contents of the page
		 * @param timeoutLength
		 *            length page is valid for (set by Aft)
		 */
		private PageWrapper(String pageName, String pageContents,
				long timeoutLength) {
			this.pageName = pageName;
			this.pageContents = pageContents;
			this.timeoutLength = timeoutLength;
			whenTimeout = System.currentTimeMillis() + timeoutLength;
		}

		/**
		 * Simple method to check if a page has timed out
		 * 
		 * @return true if page timed out, false otherwise
		 */
		private boolean checkTimeout() {
			if (whenTimeout < System.currentTimeMillis()) {
				return true;// Timed out
			} else {
				return false;
			}
		}

		private void updatePage(String contents) {
			pageContents = contents;
			whenTimeout = System.currentTimeMillis() + timeoutLength;
		}
	}
	
	/** Increment the aft id count.  This method is synchronized
	 * because multiple threads may increment the static id variable
	 * concurrently.
	 */
	static synchronized int getNextAftIDNum() {return ++id;}

}

// TODO aft when connected to pft will need to send all of it's pages so
// AftServicer knows what is available.

// TODO make sure pft when creating the AftServicer increments the port's to the
// correct number -- also tell the aft the correct port to use

// TODO ensure the shortest length of an aft page timeout is 5 seconds -- else
// we can be in infintie loop during waitForPage()