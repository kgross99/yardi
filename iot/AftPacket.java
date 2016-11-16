import java.io.UnsupportedEncodingException;

public class AftPacket {
	private boolean pollBit, requestBit, responseBit;
	private String pageName, pftName, aftName, aftGreeting;
	private int aftId, timeoutLength;
	private String pageContents;

	/**
	 * Constructor. Ensure you pass in "0" (zero) OR "false" values in for
	 * fields you are not using. DO NOT USE NULL VALUES
	 * 
	 * @param pollBit
	 * @param requestBit
	 * @param responseBit
	 * @param pageName
	 * @param pftName
	 * @param aftName
	 * @param aftGreeting
	 * @param aftId
	 * @param timeoutLength
	 * @param pageContents
	 */
	AftPacket(boolean pollBit, boolean requestBit, boolean responseBit,
			String pageName, String pftName, String aftName, String aftGreeting,
			int aftId, int timeoutLength, String pageContents) {
		this.pollBit = pollBit;
		this.requestBit = requestBit;
		this.responseBit = responseBit;
		this.pageName = pageName;
		this.pftName = pftName;
		this.aftName = aftName;
		this.aftGreeting = aftGreeting;
		this.aftId = aftId;
		this.timeoutLength = timeoutLength;
		this.pageContents = pageContents;
	}

	/**
	 * Constructs an AftPacket by reading in a byte array. Used for constructing
	 * and interpreting the data in a datagram packet.
	 * 
	 * @param bytes
	 */
	AftPacket(byte[] bytes) {
		String message = bytes.toString();
		String[] msgParts = message.split("::");
		pollBit = Boolean.parseBoolean(msgParts[0]);
		requestBit = Boolean.parseBoolean(msgParts[1]);
		responseBit = Boolean.parseBoolean(msgParts[2]);
		pageName = msgParts[3];
		pftName = msgParts[4];
		aftName = msgParts[5];
		aftGreeting = msgParts[6];
		aftId = Integer.parseInt(msgParts[7]);
		timeoutLength = Integer.parseInt(msgParts[8]);
		pageContents = msgParts[9];
	}

	/**
	 * Method that encodes the variables, and returns a byte array of all the
	 * data packed.
	 * 
	 * @return byte array of data
	 */
	public byte[] encode() {
		StringBuilder sb = new StringBuilder();
		sb.append(pollBit);
		sb.append("::");
		sb.append(requestBit);
		sb.append("::");
		sb.append(responseBit);
		sb.append("::");
		sb.append(pageName);
		sb.append("::");
		sb.append(pftName);
		sb.append("::");
		sb.append(aftName);
		sb.append("::");
		sb.append(aftGreeting);
		sb.append("::");
		sb.append(aftId);
		sb.append("::");
		sb.append(timeoutLength);
		sb.append("::");
		sb.append(pageContents);

		return sb.toString().getBytes();
	}

	public boolean getPollBit() {
		return pollBit;
	}

	// TODO double check... Commented out all setters - should be using the
	// constructor, not setters.

	// public void setPollBit(boolean pollBit) {
	// this.pollBit = pollBit;
	// }

	public boolean getRequestBit() {
		return requestBit;
	}

	// public void setRequestBit(boolean requestBit) {
	// this.requestBit = requestBit;
	// }

	public boolean getResponseBit() {
		return responseBit;
	}

	// public void setResponseBit(boolean responseBit) {
	// this.responseBit = responseBit;
	// }

	public String getPageName() {
		return pageName;
	}

	// public void setPageName(String pageName) {
	// this.pageName = pageName;
	// }

	public String getPftName() {
		return pftName;
	}

	// public void setPftName(String pftName) {
	// this.pftName = pftName;
	// }

	public String getAftName() {
		return aftName;
	}

	// public void setAftName(String aftName) {
	// this.aftName = aftName;
	// }

	public String getAftGreeting() {
		return aftGreeting;
	}

	// public void setAftGreeting(String aftGreeting) {
	// this.aftGreeting = aftGreeting;
	// }

	public int getAftId() {
		return aftId;
	}

	// public void setAftId(int aftId) {
	// this.aftId = aftId;
	// }

	public int getTimeoutLength() {
		return timeoutLength;
	}

	// public void setTimeoutLength(int timeoutLength) {
	// this.timeoutLength = timeoutLength;
	// }

	public String getPageContents() {
		return pageContents;
	}

	// public void setPageContents(String pageContents) {
	// this.pageContents = pageContents;
	// }
}
