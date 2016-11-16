import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CS425Endpoint implements CS425EndpointInterface {
	CS425Network network;
	private int rcvState = 0;
	private int sendState = 0;
	private int initServSeq = 0;
	private int initClientSeq = 0;
	private final int BYTESIZEARRAY = 1500;
	private InetAddress clientInetAddr = null;
	private int clientPortNumber = 0;
	private CS425Packet packet;

	public CS425Endpoint(CS425Network network, InetAddress clientInetAddress,
			int clientPortNumber, int initialServerSeqNum,
			int initialClientSeqNum) {
		this.network = network;
		initServSeq = initialServerSeqNum;
		initClientSeq = initialClientSeqNum;
		clientInetAddr = clientInetAddress;
		this.clientPortNumber = clientPortNumber;
	}

	/**
	 * Jim's comments: Sends a String over a previously established connection
	 * endpoint. The String must not be null, but is allowed to be empty. The
	 * calling thread is blocked until the request completes. If the connection
	 * fails (because, for example, the infamous backhoe cut the cable) then
	 * send returns -1 to indicate that at least some portion of the String was
	 * not delivered.
	 * 
	 * @param s
	 *            The String containing the data to be sent to the remote
	 *            service.
	 * @return Number of characters sent or -1 if an error occurs
	 */

	public int send(String s) {

		if (s == null)
			return (-1);

		int pktCtr = 0;
		// convert string s into packet, state machine until it completes
		// int seqNum = 0;//seqNum is an int in the packet
		byte seqNum = 8;// testing
		DatagramPacket sendPacket, recvPacket;
		byte[] recvBytes, sendBytes;

		while (true) {
			switch (sendState) {
			case (0): // Wait for call 0 from above
				recvBytes = new byte[BYTESIZEARRAY];
				recvPacket = new DatagramPacket(recvBytes, recvBytes.length,
						clientInetAddr, clientPortNumber);// TODO update to
															// server port?
				network.recv(recvPacket);
				if (network.recv(recvPacket) == 0) {// if recv packet, loop back
					break;
				} else {

					sendBytes = new byte[BYTESIZEARRAY];
					sendBytes[0] = seqNum;
					sendPacket = new DatagramPacket(sendBytes,
							sendBytes.length, clientInetAddr, clientPortNumber);// TODO
																				// change
					network.send(sendPacket);
					pktCtr++;
					sendState = 1;
					// TODO Start_Timer

					break;
				}

			case (1): // Wait for ACK 0
				recvBytes = new byte[BYTESIZEARRAY];
				recvPacket = new DatagramPacket(recvBytes, recvBytes.length,
						clientInetAddr, clientPortNumber);// TODO update to
															// server port?
				network.recv(recvPacket);
				recvBytes = recvPacket.getData();
				packet = new CS425Packet(recvBytes);

				if (packet.getAckFlag() && (packet.getAckNumber() == 1)) {// wrong
																			// ACK

					break;
				}
				// if(timeout)//timeout
				{
					sendBytes = new byte[BYTESIZEARRAY];
					sendPacket = new DatagramPacket(sendBytes,
							sendBytes.length, clientInetAddr, clientPortNumber);// TODO
																				// change
					network.send(sendPacket);
					// start timer again
				}
				if (packet.getAckFlag() && (packet.getAckNumber() == 0)) {
					// stop timer
					if (s.isEmpty()) {
						sendState = 5;
					} else {
						sendState = 2;
					}

					break;
				}

				break;

			case (2): // Wait for call 1 from above
				recvBytes = new byte[BYTESIZEARRAY];
				recvPacket = new DatagramPacket(recvBytes, recvBytes.length,
						clientInetAddr, clientPortNumber);// TODO update to
															// server port?
				if (network.recv(recvPacket) == 0) {// if recv packet, loop back
					break;
				} else {
					sendBytes = new byte[BYTESIZEARRAY];
					sendBytes[0] = seqNum;
					sendPacket = new DatagramPacket(sendBytes,
							sendBytes.length, clientInetAddr, clientPortNumber);// TODO
																				// change
					network.send(sendPacket);
					pktCtr++;
					sendState = 3;
					// TODO Start_Timer

					break;
				}

			case (3): // Wait for ACK 1
				recvBytes = new byte[BYTESIZEARRAY];
				recvPacket = new DatagramPacket(recvBytes, recvBytes.length,
						clientInetAddr, clientPortNumber);// TODO update to
															// server port?
				network.recv(recvPacket);
				recvBytes = recvPacket.getData();
				packet = new CS425Packet(recvBytes);

				if (packet.getAckFlag() && (packet.getAckNumber() == 0)) {// wrong
																			// ACK

					break;
				}
				// if(timeout)//timeout
				{
					sendBytes = new byte[BYTESIZEARRAY];
					sendPacket = new DatagramPacket(sendBytes,
							sendBytes.length, clientInetAddr, clientPortNumber);// TODO
																				// change
					network.send(sendPacket);
					// start timer again
				}
				if (packet.getAckFlag() && (packet.getAckNumber() == 1)) {
					// stop timer
					if (s.isEmpty()) {
						sendState = 5;
					} else {
						sendState = 2;
					}

					break;
				}

				break;
			}

			if (s.isEmpty() && (sendState == 5)) {// send complete, state = 5
													// means
													// recv'd ack for last
													// transmition
				System.out.println("packet sent");
				return pktCtr;
			}
			if (sendState == 6) {
				System.out.println("packet send failed on: " + pktCtr);
				return (-1);
			}
		}
	}

	/**
	 * Jim's comments: Receives a String over a previously established
	 * connection. The calling thread is blocked until the request completes.
	 * 
	 * @return The received String or null if an error occurs
	 */
	public String recv() {
		while (true) {
			try {
				byte[] bytes = new byte[1500];
				DatagramPacket dgp = new DatagramPacket(bytes, bytes.length);
				network.recv(dgp);
				CS425Packet pack = new CS425Packet(dgp.getData());
				switch (rcvState) {
				case 0:
					if (pack.getSequenceNumber() != 0) {
						return null; // Or resend? No, I think recv()'s caller
										// should make that decision.
						break;
					}
					rcvState = 1;
					return pack.getData();
					break;
				case 1:
					if (pack.getSequenceNumber() != 1) {
						return null; // Or resend? No, I think recv()'s caller
										// should make that decision.
						break;
					}
					rcvState = 0;
					return pack.getData();
					break;
				default:
					System.err.println("I just don't know what went wrong!"); // Derpy
																				// Hooves
																				// forever!
					return null;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * Jim's comments: Closes an established connection. If the transport
	 * supports graceful close, the calling thread will block until all
	 * previously sent data has been delivered.
	 * 
	 * @return 0 or -1 if an error occurs
	 */
	public int close() {
		try {
			while (sendState != recvState) {
				recv(); // TODO might cause infinite loop
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
}
