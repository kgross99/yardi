import java.net.DatagramPacket;


/**
 * Defines the interface to a concrete CS425Network class.  In addition to the
 * methods below, your CS425Network class must include a parameterless constructor
 * that builds an instance of CS425Network.  
 * <p>
 * You may implement additional constructors useful for your own development
 * and testing.  For example, you may want to configure the network to lose
 * or duplicate out-bound packets.  
 * <p>
 * Such an implementation of CS425Network displays some attributes of a
 * Test Double, an object implementing the behaviors of the released
 * product in a way that facilitates testing.  Test Doubles are
 * widely used to exercise important but rarely encountered behaviors.
 * <p>
 * WARNING:  Do not modify this interface.  If you find a defect, contact the author.
 *  
 * @author jimconrad
 *
 */
public interface CS425NetworkInterface {
	
	/**
	 * Send datagram to remote service
	 * @param dg Remote host's IP Address, UDP port and the data to be sent.
	 * @return 0 if packet has been sent, -1 if an error occurs
	 */
	 int send(DatagramPacket dg);
	 
	 /**
	  * Receive datagram.  The caller is blocked until the request completes.
	  * @param dg Received data, remote host's IP Address and UDP port
	  * @return 0 if packet has been received, -1 if an error occurs
	  */
	 int recv(DatagramPacket dg);

}
