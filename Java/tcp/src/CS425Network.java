import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;
import java.io.IOException;

public class CS425Network implements CS425NetworkInterface {

	private DatagramReceiver dgRcvr;

	/**
	 * Jim's comments:
     * Defines the interface to a concrete CS425Network class.
	 * In addition to the methods below, your CS425Network class
     * must include a parameterless constructor that builds an
     * instance of CS425Network.
	 */
	public CS425Network() {
		dgRcvr = new DatagramReceiver(12345);
	}

	/**
     * Constructor that lets caller set the port number.
     * @param localReceiverPortNumber port number of the local
     * datagram socket receiver that blocks waiting for an
     * incoming packet. Remote clients will need to use this
     * port number to send to this (local) Network instance.
     */
	public CS425Network(int localReceiverPortNumber) {
		dgRcvr = new DatagramReceiver(localReceiverPortNumber);
	}

	/**
	 * Jim's comments: Send datagram to remote service 
	 * @param dg Remote host's IP Address, UDP port and the
     * data to be sent.
	 * @return 0 if packet has been sent, -1 if an error occurs
	 */
	public int send(DatagramPacket dg) {
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.send(dg);
			System.out.println("packet sent");
			socket.close();
			return 0;
		} catch (UnknownHostException ex) {
			return (-1);
		} catch (IOException ex) {
			return (-1);
		}
	}

	/**
	 * Jim's comments:
     * Receive datagram. The caller is blocked until the request
	 * completes.
	 * @param dg Received data, remote host's IP Address and UDP port
	 * @return 0 if packet has been received, -1 if an error occurs
	 */
	public int recv(DatagramPacket dg) {
		DatagramPacket packet = dgRcvr.recv();
		if (packet == null) {
			return (-1);
		} else {
			dg.setData(packet.getData(), 0, dg.getLength());
			dg.setAddress(packet.getAddress());
			dg.setPort(packet.getPort());
			return 0;
		}
	}
}
