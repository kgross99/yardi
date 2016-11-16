import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.TimerTask;
import java.net.UnknownHostException;
import java.util.Timer;

/**
 * Jim's comments: In addition to the methods below, your CS425Transport class
 * must implement a constructor that accepts a single parameter, a reference to
 * an object implementing CS425NetworkInterface. Your CS425Transport construct
 * must build a transport that will operate over UDP port 8080. You may, of
 * course, implement other constructors for your own development/testing
 * purposes, but I will use the constructor described above.
 * 
 * CS425Transport must have a constructor with
 * a single parameter, a reference to an implementation of
 * CS425NetworkInterface, and implementing the CS425TransportInterface.
 * CS425Transport must access The Internet�s UDP service through a java class,
 * CS425Network having a default constructor and implementing the
 * CS425NetworkInterface.
 * 
 * Hints and Resources You may wish to think of CS425Transport as a state
 * machine, driven by internal (e.g. Timer) and external (from the various
 * interfaces) events.
 */

/**
 * DatagramPacket Needed*** DatagramPacket(byte[] buf, int length,
 * InetAddress address, int port) Constructs a datagram packet for sending
 * packets of length length with offset set to the specified port number on
 * the specified host.
 */

public class CS425Transport implements CS425TransportInterface {
	//int port = 8080;
	private CS425Network network;
	private Random rand;
	private final int MAX_INIT_SEQNUM = 1000000;
	private final int MAX_PACKET_DATA_SIZE = 65536; //1500-20; // ethernet frame - udp hdr
	private int handshakeState;
	private boolean handshakeDone;
	private InetAddress remoteHostInetAddress;
	private String remoteServiceName;
	private String localServiceName;
	
	private InetAddress clientInetAddress;
	private int clientPortNumber;

	public CS425Transport(CS425Network network) {
		this.network = network;
		rand = new Random();
		handshakeState = 0;
		handshakeDone = false;
	}

	/**
	 * Jim's comments: Initiates a connection request with a service in a remote
	 * host. The calling thread is blocked until the connection request
	 * completes or fails. The returned CS425Endpoint can be used to
	 * send/receive data to/from the remote service. If the transport supports
	 * multiplexing, multiple connections may be established to a remote host.
	 * If the transport does not support multiplexing, then connect will return
	 * null if a second connection is attempted. An established connection has
	 * two endpoints, one on the local host and another on the remote host
	 * 
	 * @param remoteHost
	 *            Domain name ("www.bozo.org") or IP Address (e.g.
	 *            "192.168.0.11") of the remote host
	 * @param remoteServiceName
	 *            Identifies a specific service on the remote host
	 * @return reference to a CS425Endpoint or null if the connection request
	 *         fails
	 */
	public CS425Endpoint connect(String remoteHost,
			String remoteServiceName) {
		try {
			this.remoteHostInetAddress = InetAddress.getByName(remoteHost);
		} catch(UnknownHostException ex) {
			System.err.println(ex.getMessage());
			return null;
		}
		this.remoteServiceName = remoteServiceName;
		int initialClientSeqNum = rand.nextInt(MAX_INIT_SEQNUM);
		int initialServerSeqNum = shakeHandsWithServer(initialClientSeqNum);
		if(initialClientSeqNum != -1) {
			return new CS425Endpoint(network, clientInetAddress, clientPortNumber,
					initialServerSeqNum, initialClientSeqNum);
		} else {
			return null;
		}
	}

	/**
	 * Jim's comments: Awaits an incoming connection request from a remote host.
	 * The returned CS425Endpoint can be used to send/receive data to/from the
	 * remote service. Only one thread may be awaiting a connection request on a
	 * single specified service. The calling thread is blocked until the request
	 * completes. Service names must not be null or empty. An established
	 * connection has two endpoints, one on the local host and another on the
	 * remote host
	 * 
	 * @param serviceName
	 *            Identifies this specific service on the local host.
	 * @return CS425Endpoint or null if something fails
	 */
	public CS425Endpoint accept(String serviceName) {
		// do the connection handshake
		// get the client's sequence number
		// send the client this server's sequence number
		// do the ACKs
		// return an endpoint to service the connection
		localServiceName = serviceName;
		int initialServerSeqNum = rand.nextInt(MAX_INIT_SEQNUM);
		int initialClientSeqNum = shakeHandsWithClient(initialServerSeqNum);
		if(initialClientSeqNum != -1) {
			return new CS425Endpoint(network, clientInetAddress, clientPortNumber,
					initialServerSeqNum, initialClientSeqNum);
		} else {
			return null;
		}
	}
	
	/**
	 * method to perform handshake with incoming connection client.
	 * @return 0 if success, otherwise -1
	 */
	private int shakeHandsWithServer(int initialServerSeqNum) {
        byte[] sendBytes = null;
        DatagramPacket sendPacket = null;
		int initialClientSeqNum = -1;
		while(!handshakeDone) {
			switch(handshakeState){
			case 0:
                //sendBytes = message.getBytes(Charset.forName("UTF-8"));
                //DatagramPacket sendPacket = new DatagramPacket(
                //        sendBytes, sendBytes.length,
                //        InetAddress.getByName(
                //                remoteReceiverHostName),
                //                remoteReceiverPortNumber);
                //network.send(sendPacket);
				break;
			default:
				handshakeDone = true;
				break;
			}
		}
		return initialClientSeqNum;
	}
	
	/**
	 * method to perform handshake with incoming connection client.
	 * @return 0 if success, otherwise -1
	 */
	private int shakeHandsWithClient(int initialServerSeqNum) {
        Packet pkt;
		int clientResult = -1;
		byte[] clientDataBytes = null;
        DatagramPacket clientPacket = null;
		int initialClientSeqNum = -1;
        Timer tmr = new Timer();
        int[] packetsArray = new int[100];
        PacketTimer[] packetSeqTmrArray = new PacketTimer[100];
        int serverSeqNum = initialServerSeqNum;
        
		while(!handshakeDone) {
			switch(handshakeState){
			case 0:
                clientDataBytes = new byte[MAX_PACKET_DATA_SIZE];
                clientPacket =
                		new DatagramPacket(clientDataBytes, clientDataBytes.length);
                clientResult = network.recv(clientPacket);
                if(clientResult != -1) {
                	clientInetAddress = clientPacket.getAddress();
                	clientPortNumber = clientPacket.getPort();
                	pkt = new Packet(clientPacket.getData());
                	if(pkt.length() != 0) {
                		if(pkt.getServiceName() == localServiceName) {
                			if(pkt.getSynFlag() == true) {
                				/* send SYN ACK */
                				initialClientSeqNum = pkt.getSequenceNumber();
                				Packet synackPak = new Packet();
                				synackPak.setSynFlag(true);
                				synackPak.setAckFlag(true);
                				synackPak.setSequenceNumber(initialServerSeqNum);
                				synackPak.setAckNumber(initialClientSeqNum+1);
                				byte[] tmpBytes = new byte[synackPak.length()];
                				tmpBytes = synackPak.getEncodedPacket();
                                DatagramPacket synackDGPak = new DatagramPacket(
                                        tmpBytes, tmpBytes.length,
                                        clientInetAddress, clientPortNumber);
                                packetSeqTmrArray[0] = new PacketTimer(
                                		tmr, 1000, serverSeqNum+0-initialServerSeqNum, packetsArray);
                                network.send(synackDGPak);
                                /*
                                 * get the packet
                                 * if same service
                                 * if seq == initialClientSeqNum + 1
                                 * if ack == initialServerSeqNum + 1
                                 */
                                handshakeState = 1;
                			} else {
                				return -1;
                			}
                		} else {
                			return -1;
                		}
                	} else {}
                } else {
                	return -1;
                }
				break;
			case 1:
				/* blocking for syn=0, seq=client_sn+1, ack=server_sn+1 */
                byte[] recvBytes = new byte[MAX_PACKET_DATA_SIZE];
                DatagramPacket recvPacket =
                        new DatagramPacket(recvBytes, recvBytes.length);
                network.recv(recvPacket);
				break;
			default:
				handshakeDone = true;
				break;
			}
		}
		return initialClientSeqNum;
	}
	
}
