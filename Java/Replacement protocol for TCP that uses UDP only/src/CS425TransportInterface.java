/**
 * Defines the transport interface implemented by your CS425Transport.
 * <p> 
 * In addition to the methods below, your CS425Transport class must implement a constructor
 * that accepts a single parameter, a reference to an object implementing CS425NetworkInterface.
 * Your CS425Transport construct must build a transport that will operate over UDP port 8080.
 * You may, of course, implement other constructors for your own development/testing purposes,
 * but I will use the constructor described above.
 * <p>
 * WARNING:  Do not modify this interface definition.  If you find a defect, contact the author.
 * 
 * @author jimconrad
 *
 */
public interface CS425TransportInterface {
	
	/**
	 * Initiates a connection request with a service in a remote host
	 * <p>
	 * The calling thread is blocked until the connection request completes or fails.
	 * <p>
	 * The returned CS425Endpoint can be used to send/receive data to/from the remote service
	 * <p>
	 * If the transport supports multiplexing, multiple connections may be established to a remote host.
	 * If the transport does not support multiplexing, then connect will return null if a second
	 * connection is attempted.
	 * <p>
	 * An established connection has two endpoints, one on the local host and another on the remote host
	 * 
	 * @param remoteHost Domain name ("www.bozo.org") or IP Address (e.g. "192.168.0.11") of the remote host
	 * @param remoteServiceName Identifies a specific service on the remote host
	 * @return reference to a CS425Endpoint or null if the connection request fails 
	 */
	public CS425EndpointInterface connect(String remoteHost, String remoteServiceName);
	
	/**
	 * Awaits an incoming connection request from a remote host
	 * <p>
	 * The returned CS425Endpoint can be used to send/receive data to/from the remote service
	 * <p>
	 * Only one thread may be awaiting a connection request on a single specified service.  The
	 * calling thread is blocked until the request completes.
	 * <p>
	 * Service names must not be null or empty.
	 * <p>
	 * An established connection has two endpoints, one on the local host and another on the remote host
	 * 
	 * @param serviceName Identifies this specific service on the local host.
	 * @return CS425Endpoint or null if something fails
	 */
	public CS425EndpointInterface accept(String serviceName);

}
