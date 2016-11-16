
public class TestAppTimeService {

	AppTimeService atp;
	
	public TestAppTimeService() {
		atp = new AppTimeService();
	}
	
	public static void main(String[] args) {
		
		TestAppTimeService test = new TestAppTimeService();
		test.atp.run();
		
	}

}
