import java.net.DatagramPacket;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This thread class services connections from aft's attempting to
 * connect to a suitable pft. If the aft is suited to this pft, it
 * is added to the aft data sets.
 */
public class AftAcceptanceService implements Runnable {
	
	private boolean DEBUG;
	private DatagramPacket packet;
	ConcurrentLinkedDeque<AftServicer> aftCache;
	ConcurrentLinkedDeque<Thread> aftThreads;
	private String requiredAftGreeting;
	
	/* Constructor builds ConnectionService for a specified
	 * connection */
	public AftAcceptanceService(
			DatagramPacket packet,
			ConcurrentLinkedDeque<AftServicer> aftCache,
			ConcurrentLinkedDeque<Thread> aftThreads,
			String aftGreeting,
			boolean debug) {
		
		this.DEBUG = debug;
		this.packet = packet;
		this.requiredAftGreeting = aftGreeting;
		this.aftCache = aftCache;
		this.aftThreads = aftThreads;
	}
	/**
	 * This method examines the broadcast packet from an aft and determines
	 * whether the aft may be serviced by our pft.
	 */
	@Override
	public void run() {
		if(Thread.interrupted()) {
			if(DEBUG) System.out.println(
					"Aft Acceptance Service Interrupted. " +
					"Will finish and exit");
			AftPacket aftPacket = new AftPacket(packet.getData());
			String aftGreeting = aftPacket.getAftGreeting();
		    if(!aftGreeting.equalsIgnoreCase(requiredAftGreeting)) return;
	    	
	    	/* create aft servicer object */
		    AftServicer aftServicer = new AftServicer(packet);
			aftCache.add(aftServicer);
	    	
	    	/* start aft servicer thread to handle the new aft */
			Thread t = new Thread(aftServicer);
	    	aftThreads.add(t);
	    	t.start();
	    	
		} else {}
	}
}
