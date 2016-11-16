import java.net.DatagramPacket;

public class DatagramReceiver {

    DatagramReceiverThread drThread;
    
    public DatagramReceiver(int localReceiverPortNumber) {
        drThread=new DatagramReceiverThread(localReceiverPortNumber);
        drThread.start();
    }
    
    /* method to return a packet from the receiver server */
    public DatagramPacket recv() {
        return drThread.recv();
    }
}
