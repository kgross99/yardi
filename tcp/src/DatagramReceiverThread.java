import java.net.DatagramSocket;
import java.net.DatagramPacket;
//import java.net.InetAddress;
import java.net.SocketException;
import java.io.IOException;
//import java.lang.InterruptedException;
//import java.net.UnknownHostException;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;

public class DatagramReceiverThread extends Thread {

    private DatagramSocket socket;
    private final int BUFFER_SIZE = 65536;
    //private final String LOCAL_HOST_NAME = "127.0.0.1";
    private ArrayDeque<DatagramPacket> packets;
    //private boolean running;

    public DatagramReceiverThread(int localReceiverPortNumber) {
         super("DatagramReceiverThread");
        try {
            //socket=new DatagramSocket(localReceiverPortNumber,
            //        InetAddress.getByName(LOCAL_HOST_NAME));
            socket=new DatagramSocket(localReceiverPortNumber);
        //} catch(UnknownHostException ex) {
        //    ex.printStackTrace();
        //    System.exit(1);
        } catch(SocketException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        packets=new ArrayDeque<DatagramPacket>();
        //running = true;
    }

    /* thread makes receiver socket, which blocks waiting for
     * incoming packet
     */
    public void run() {
        try {
            while(true) {
                byte[] buf = new byte[BUFFER_SIZE];
                DatagramPacket packet=new DatagramPacket(buf, buf.length);
                /* socket blocks waiting for a packet.
                 * when program ends, this will still be waiting.
                 * use eclipse terminate button or
                 * ctrl+c from terminal to close the socket */
                socket.receive(packet);
                /* enqueue the packet */
                packets.addLast(packet);
                String message = new String(packet.getData(), "UTF-8").trim();
                if(message.equalsIgnoreCase("quit")) {
                    socket.setSoTimeout(100);
                } else {}
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (SocketTimeoutException ex) {
            //ex.printStackTrace();
            socket.close();
            //running = false;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public DatagramPacket recv() {
        /* request for a packet blocks until the queue
         * contains a packet or socket has been closed
         */
        //try {
        //    while(packets.isEmpty() && running) {
        //        Thread.sleep(10);
        //    }
        //} catch(InterruptedException ex) {
        //    ex.printStackTrace();
        //    System.exit(1);
        //}
        /* dequeue and return a packet */
        return packets.isEmpty() ? null : packets.removeFirst();
    }
}
