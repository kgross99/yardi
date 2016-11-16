public interface CS425EndpointInterface {
	
	/**
	 * Sends a String over a previously established connection endpoint
	 * <p>
	 * The String must not be null, but is allowed to be empty.
	 * <p>
	 * The calling thread is blocked until the request completes.  If the connection
	 * fails (because, for example, the infamous backhoe cut the cable) then send
	 * returns -1 to indicate that at least some portion of the String was
	 * not delivered.
	 * <p>
	 * WARNING:  Do not modify this interface.  If you find a defect, contact the author.
	 * 
	 * @param s  The String containing the data to be sent to the remote service.
	 * @return Number of characters sent or -1 if an error occurs
	 */
	public int send(String s);
	
	/**
	 * Receives a String over a previously established connection.  The calling
	 * thread is blocked until the request completes.
	 * 
	 * @return The received String or null if an error occurs
	 */
	public String recv();
	
	/**
	 * Closes an established connection
	 * <p>
	 * If the transport supports graceful close, the calling thread will block until
	 * all previously sent data has been delivered.
	 * 
	 * @return 0 or -1 if an error occurs
	 */
	public int close();

}
