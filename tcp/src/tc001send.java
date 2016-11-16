import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.io.IOException;

public class tc001send {

    private int localReceiverPortNumber;
    private String remoteReceiverHostName;
    private int remoteReceiverPortNumber;
    private int bufferSize;
    private String role;

    public static void main(String[] args) {
        /* usage message: give the use the requirements for
         * command-line arguments
         */
        String usage = new String("Usage: java tc001send\n" +
                "<local receiver port number>\n" +
                "<remote receiver host name>\n" +
                "<remote receiver port number>\n" +
                "<buffer size>\n" +
                "<\"s\" to be the sender or \"r\" to be the receiver>");
        if (args.length != 5) {
            System.err.println(usage);
            System.exit(1);
        }
        
        /* create an instance of this test class */
        tc001send test = new tc001send();
        
        /* get the command-line arguments */
        try {
            test.localReceiverPortNumber = Integer.parseInt(args[0]);
            test.remoteReceiverHostName = args[1];
            test.remoteReceiverPortNumber = Integer.parseInt(args[2]);
            test.bufferSize = Integer.parseInt(args[3]);
            test.role = args[4];
            if(!test.role.equalsIgnoreCase("r") &&
                    !test.role.equalsIgnoreCase("s")) {
                System.err.println(usage);
                System.exit(1);
            } else {}
        } catch(NumberFormatException ex) {
            System.err.println(usage);
            System.exit(1);
        } catch(NullPointerException ex) {
            System.err.println(usage);
            System.exit(1);
        }
        
        try {
            /* create the test network */
            CS425Network network =
                    new CS425Network(test.localReceiverPortNumber);
            
            String message = null;
            if(test.role.equalsIgnoreCase("s")) {
                /* act as sender */
                /* packet data buffers */
                byte[] sendBytes = new byte[test.bufferSize];
                /* send some packets */
                int count = 10;
                for(int i = 0; i <= count; i++) {
                    if(i >= count) {
                        message = "quit";
                    } else {
                        message = String.format("Number: %2d", i);
                    }
                    sendBytes = message.getBytes(Charset.forName("UTF-8"));
                    DatagramPacket sendPacket = new DatagramPacket(
                            sendBytes, sendBytes.length,
                            InetAddress.getByName(
                                    test.remoteReceiverHostName),
                            test.remoteReceiverPortNumber);
                    network.send(sendPacket);
                }
                message = "quit";
                sendBytes = message.getBytes(Charset.forName("UTF-8"));
                DatagramPacket sendPacket = new DatagramPacket(
                        sendBytes, sendBytes.length,
                        InetAddress.getByName("127.0.0.1"),
                        test.localReceiverPortNumber);
                network.send(sendPacket);
            } else {
                /* act as receiver */
                /* packet data buffers */
                byte[] recvBytes = new byte[test.bufferSize];
                /* receive some packets */
                boolean running = true;
                while(running) {
                    DatagramPacket recvPacket =
                            new DatagramPacket(recvBytes, recvBytes.length);
                    network.recv(recvPacket);
                    System.out.print("packet received: ");
                    message = new String(recvPacket.getData(), "UTF-8").trim();
                    System.out.println(message);
                    if(message.equalsIgnoreCase("quit")) {
                        running = false;
                    } else {}
                }
            }
            /* at this point, receiver socket is still blocking,
             * waiting for the next incoming packet. use the terminate
             * button in eclipse or ctrl+c at terminal to close the socket.
             */ 
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
