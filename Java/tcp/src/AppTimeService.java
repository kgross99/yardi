
public class AppTimeService {
	
	CS425Network network;
	CS425Transport transport;
	CS425Endpoint ep;
	
	public static void main(String[] args) {
		
		AppTimeService ats = new AppTimeService();
		ats.run();
	}
	
	public AppTimeService() {
		network = new CS425Network(8080);
		transport = new CS425Transport(network);
	}
	
	public void run(){
		while(true) {
			ep = transport.accept("TimeService");
			if(ep != null) {
				(new Thread(new AppTimeServiceThread(ep))).start();
			} else {}
		}
	}

}
