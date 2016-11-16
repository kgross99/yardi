import java.util.Date;

public class AppTimeServiceThread implements Runnable {
	
	private String dateString;
	private CS425Endpoint ep;
	//private static int numConnections = 0;

	public AppTimeServiceThread(CS425Endpoint ep) {
		this.ep = ep;
		//incNumConnections();
	}
	
    /* thread does what ???
     */
    public void run() {
    	dateString = new Date().toString();
    	ep.send(dateString);
    	ep.close();
    }
    
	//Increment count of connections.  This method is synchronized because multiple threads
	//will increment/decrement the static nConnections variable concurrently.
	//static synchronized void incNumConnections() {
	//	numConnections++;
	//}
	//static synchronized void decNumConnections() {
	//	numConnections--;
	//}
}
