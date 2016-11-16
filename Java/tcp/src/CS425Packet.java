import java.nio.charset.Charset;
import java.util.Arrays;
import java.io.UnsupportedEncodingException;

public class CS425Packet {
	
	/**
	 * class data fields
	 */
	byte flags; // bit field for syn, ack and fin bits.
	boolean fSyn;
	boolean fAck;
	boolean fFin;
	String serviceNameString;
	String dataString;
	byte[] serviceName;
	byte[] data;
	byte[] packet;
	int packetLength;
	byte[] serviceNameLength = new byte[2];
	byte[] dataLength = new byte[2];
	
	final int SYN_MASK = 4;
	final int ACK_MASK = 2;
	final int FIN_MASK = 1;
	final int SYN_SHIFT = 2;
	final int ACK_SHIFT = 2;
	final int FIN_SHIFT = 2;
	
	int sequenceNumber;
	int ackNumber;
	int windowSize;
	byte[] sequenceNumberField = new byte[4];
	byte[] ackNumberField = new byte[4];
	byte[] windowSizeField = new byte[4];
	
	final int MIN_PACKET_SIZE =
			sequenceNumberField.length +
			ackNumberField.length +
			windowSizeField.length +
			1 +
			serviceNameLength.length +
			dataLength.length;
	
	/**
	 * Parameter-less constructor that initializes the data fields.
	 */
	public CS425Packet() {
		flags = 0;
		fSyn = false;
		fAck = false;
		fFin = false;
		serviceName = null;
		data = null;
		packet = null;
		packetLength = 0;
		serviceNameString = null;
		dataString = null;
		sequenceNumber = 0;
		ackNumber = 0;
		windowSize = 0;
	}
	
	/**
	 * Constructor that takes a byte array and interprets it as
	 * contents of a Packet object.
	 * @param bytes array of bytes presumed to be in the format the
	 * contents of a Packet object.
	 */
	public CS425Packet(byte[] bytes) {
		this();
		if(bytes != null) {
			if(bytes.length >= MIN_PACKET_SIZE) {
				packet = new byte[bytes.length];
				System.arraycopy(bytes, 0, packet, 0, bytes.length);
				packetLength = bytes.length;
				decodePacket();
			} else {}
		} else {}
	}
	
	/**
	 * Stuffs the constituent fields of a packet with bytes from
	 * user supplied byte array.
	 */
	private void decodePacket() {
		if(packet == null) return;
		/* stuff fields with bytes from packet */
		int startPosition = 0;
		System.arraycopy(
				packet,
				startPosition,
				sequenceNumberField, 0, sequenceNumberField.length);
		startPosition += sequenceNumberField.length;
		
		System.arraycopy(
				packet,
				startPosition,
				ackNumberField, 0, ackNumberField.length);
		startPosition += ackNumberField.length;
		
		System.arraycopy(
				packet,
				startPosition,
				windowSizeField, 0, windowSizeField.length);
		startPosition += windowSizeField.length;
		
		flags = packet[startPosition];
		startPosition += 1;
		
		System.arraycopy(
				packet,
				startPosition,
				serviceNameLength, 0, serviceNameLength.length);
		startPosition += serviceNameLength.length;
		
		System.arraycopy(
				packet,
				startPosition,
				dataLength, 0, dataLength.length);
		startPosition += dataLength.length;
		
		System.arraycopy(
				packet,
				startPosition,
				serviceName, 0, serviceName.length);
		startPosition += serviceName.length;
		
		System.arraycopy(
				packet,
				startPosition,
				serviceName, 0, data.length);
	}
	
	/**
	 * Decodes the bytes from the service name field of a packet into a
	 * string.
	 * @return string containing the name of service to which the
	 * packet is/was sent. 
	 */
	public String getServiceName() {
		if(serviceNameString == null) {
			try {
				serviceNameString = new String(serviceName, "UTF-8").trim();
			} catch(UnsupportedEncodingException ex) {
				serviceNameString = "";
			}
		} else {}
		return serviceNameString;
	}
	
	/**
	 * Decodes the bytes from the data field of a packet into a
	 * string.
	 * @return string containing the data payload of the packet.
	 */
	public String getData() {
		if(dataString == null) {
			try {
				dataString = new String(data, "UTF-8").trim();
			} catch(UnsupportedEncodingException ex) {
				dataString = "";
			}
		} else {}
		return dataString;
	}
	
	/**
	 * Decodes the flag field of the packet and extracts the boolean
	 * interpretation of the SYN bit.
	 * @return the boolean interpretation of the SYN bit.
	 */
	public boolean getSynFlag() {
		if(packet == null) return false;
		fSyn = ((((byte)(flags & (byte)SYN_MASK)) >> SYN_SHIFT) == 1) ? true : false;
		return fSyn;
	}
	
	/**
	 * Decodes the flag field of the packet and extracts the boolean
	 * interpretation of the ACK bit.
	 * @return the boolean interpretation of the ACK bit.
	 */
	public boolean getAckFlag() {
		if(packet == null) return false;
		fAck = ((((byte)(flags & (byte)ACK_MASK)) >> ACK_SHIFT) == 1) ? true : false;
		return fAck;
	}
	
	/**
	 * Decodes the flag field of the packet and extracts the boolean
	 * interpretation of the FIN bit.
	 * @return the boolean interpretation of the FIN bit.
	 */
	public boolean getFinFlag() {
		if(packet == null) return false;
		fFin = ((((byte)(flags & (byte)FIN_MASK)) >> FIN_SHIFT) == 1) ? true : false;
		return fFin;
	}
	
	/**
	 * Decodes the bytes from the sequence number field of a
	 * packet into an int, stores it and returns it.
	 * @return int sequence number from the packet.
	 */
	public int getSequenceNumber() {
		if(packet == null) return -1;
		decodeSequenceNumber();
		return sequenceNumber;
	}
	
	/**
	 * Decodes the bytes from the ack number field of a
	 * packet into an int, stores it and returns it.
	 * @return int ack number from the packet.
	 */
	public int getAckNumber() {
		if(packet == null) return -1;
		decodeAckNumber();
		return ackNumber;
	}
	
	/**
	 * Decodes the bytes from the window size field of a
	 * packet into an int, stores it and returns it.
	 * @return int window size from the packet.
	 */
	public int getWindowSize() {
		if(packet == null) return -1;
		decodeWindowSize();
		return windowSize;
	}
	
	/**
	 * Decodes the bytes of the sequence number field into an
	 * int.
	 */
	private void decodeSequenceNumber() {
		sequenceNumber = ((int)sequenceNumberField[0] << 8*0);
		sequenceNumber += ((int)sequenceNumberField[1] << 8*1);
		sequenceNumber += ((int)sequenceNumberField[2] << 8*2);
		sequenceNumber += ((int)sequenceNumberField[3] << 8*3);
	}
	
	/**
	 * Decodes the bytes of the ack number field into an
	 * int.
	 */
	private void decodeAckNumber() {
		ackNumber = ((int)ackNumberField[0] << 8*0);
		ackNumber += ((int)ackNumberField[1] << 8*1);
		ackNumber += ((int)ackNumberField[2] << 8*2);
		ackNumber += ((int)ackNumberField[3] << 8*3);
	}
	
	/**
	 * Decodes the bytes of the window size field into an
	 * int.
	 */
	private void decodeWindowSize() {
		windowSize = ((int)windowSizeField[0] << 8*0);
		windowSize += ((int)windowSizeField[1] << 8*1);
		windowSize += ((int)windowSizeField[2] << 8*2);
		windowSize += ((int)windowSizeField[3] << 8*3);
	}
	
	/**
	 * Getter method that gives user access to the completed packet's
	 * contents.
	 * @return the packet byte array.
	 */
	public byte[] getEncodedPacket() {
		encodePacket();
		packetLength = (packet == null) ? 0 : packet.length; 
		return packet;
	}
	
	/**
	 * Getter method that allows user to obtain the length of the
	 * packet's contents in bytes.
	 * @return
	 */
	public int length() {
		return packetLength;
	}
	
	/**
	 * Setter method that allows user to set the packet's SYN bit.
	 * @param fSyn boolean that is true to set SYN bit, false to
	 * clear it.
	 */
	public void setSynFlag(boolean fSyn) {
		this.fSyn = fSyn;
		flags = this.fSyn ?
				(byte)(flags | (byte)SYN_MASK) :
				(byte)(flags & ~((byte)(1 << SYN_SHIFT)));
	}
	
	/**
	 * Setter method that allows user to set the packet's ACK bit.
	 * @param fAck boolean that is true to set ACK bit, false to
	 * clear it.
	 */
	public void setAckFlag(boolean fAck) {
		this.fAck = fAck;
		flags = this.fAck ?
				(byte)(flags | (byte)ACK_MASK) :
				(byte)(flags & ~((byte)(1 << ACK_SHIFT)));
	}
	
	/**
	 * Setter method that allows user to set the packet's FIN bit.
	 * @param fFin boolean that is true to set FIN bit, false to
	 * clear it.
	 */
	public void setFinFlag(boolean fFin) {
		this.fFin = fFin;
		flags = fFin ?
				(byte)(flags | (byte)FIN_MASK) :
				(byte)(flags & ~((byte)(1 << FIN_SHIFT)));
	}
	
	/**
	 * Setter method that allows user to set the name of the
	 * service to which this packet will be sent.
	 * @param serviceName String with name of a service available
	 * somewhere in the network.
	 */
	public void setServiceName(String serviceName) {
		serviceNameString = serviceName;
		encodeServiceName();
	}
	
	/**
	 * Setter method that allows user to set the data to be
	 * placed into the packet byte array.
	 * @param data String that is the packet payload.
	 */
	public void setData(String data) {
		dataString = data;
		encodeData();
	}
	
	/**
	 * Setter method that allows user to set the sequence
	 * number to be placed into the sequence number byte array.
	 * @param sequenceNumber value at which to set the sequence number.
	 */
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
		encodeSequenceNumber();
	}
	
	/**
	 * Setter method that allows user to set the ack
	 * number to be placed into the ack number byte array.
	 * @param ackNumber value at which to set the ack number.
	 */
	public void setAckNumber(int ackNumber) {
		this.ackNumber = ackNumber;
		encodeAckNumber();
	}
	
	/**
	 * Setter method that allows user to set the window
	 * size to be placed into the window size byte array.
	 * @param windowSize value at which to set the window size.
	 */
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
		encodeWindowSize();
	}
	
	/**
	 * Stuffs the data service name array with the bytes from the
	 * service name string.
	 */
	private void encodeServiceName() {
		serviceName = new byte[serviceNameString.length()];
		serviceName = serviceNameString.getBytes(Charset.forName("UTF-8"));
		encodeServiceNameLength();
	}
	
	/**
	 * Sets the value of the service length field of the packet
	 * array.
	 */
	private void encodeServiceNameLength() {
		serviceNameLength[0] = (byte)(serviceName.length >> 8*0 & 0xf);
		serviceNameLength[1] = (byte)(serviceName.length >> 8*1 & 0xf);
	}
	
	/**
	 * Stuffs the data byte array with the bytes from the
	 * data string.
	 */
	private void encodeData() {
		data = new byte[dataString.length()];
		data = dataString.getBytes(Charset.forName("UTF-8"));
		encodeDataLength();
	}
	
	/**
	 * Sets the value of the data length field of the packet
	 * array.
	 */
	private void encodeDataLength() {
		dataLength[0] = (byte)(data.length >> 8*0 & 0xf);
		dataLength[1] = (byte)(data.length >> 8*1 & 0xf);
	}
	
	/**
	 * Stuffs the sequence number array with the bytes from the
	 * sequence number int.
	 */
	private void encodeSequenceNumber() {
		sequenceNumberField[0] = (byte)(sequenceNumber >> 8*0 & 0xf);
		sequenceNumberField[1] = (byte)(sequenceNumber >> 8*1 & 0xf);
		sequenceNumberField[2] = (byte)(sequenceNumber >> 8*2 & 0xf);
		sequenceNumberField[3] = (byte)(sequenceNumber >> 8*3 & 0xf);
	}
	
	/**
	 * Stuffs the ack number array with the bytes from the
	 * ack number int.
	 */
	public void encodeAckNumber() {
		ackNumberField[0] = (byte)(ackNumber >> 8*0 & 0xf);
		ackNumberField[1] = (byte)(ackNumber >> 8*1 & 0xf);
		ackNumberField[2] = (byte)(ackNumber >> 8*2 & 0xf);
		ackNumberField[3] = (byte)(ackNumber >> 8*3 & 0xf);
	}
	
	/**
	 * Stuffs the window size array with the bytes from the
	 * window size int.
	 */
	private void encodeWindowSize() {
		windowSizeField[0] = (byte)(windowSize >> 8*0 & 0xf);
		windowSizeField[1] = (byte)(windowSize >> 8*1 & 0xf);
		windowSizeField[2] = (byte)(windowSize >> 8*2 & 0xf);
		windowSizeField[3] = (byte)(windowSize >> 8*3 & 0xf);
	}
	
	/**
	 * Stuffs the packet byte array with the flags, service name and
	 * data and sets the required length fields.
	 */
	private void encodePacket() {
		if(serviceName == null) setServiceName("");
		if(data == null) setData("");
		
		/* set packet length and initialize with zeros */
		int tempPacketLength =
				sequenceNumberField.length +
				ackNumberField.length +
				windowSizeField.length +
				1 + // flags byte
				serviceNameLength.length +
				dataLength.length +
				serviceName.length +
				data.length;
		int packetPaddingLength = (4 - tempPacketLength % 4) % 4;
		packetLength = tempPacketLength + packetPaddingLength;
		packet = new byte[packetLength];
		Arrays.fill(packet, (byte)0);
		
		/* stuff packet with bytes */
		int startPosition = 0;
		
		System.arraycopy(sequenceNumberField, 0, packet,
				startPosition,
				sequenceNumberField.length);
		startPosition += sequenceNumberField.length;
		
		System.arraycopy(ackNumberField, 0, packet,
				startPosition,
				ackNumberField.length);
		startPosition += ackNumberField.length;
		
		System.arraycopy(windowSizeField, 0, packet,
				startPosition,
				windowSizeField.length);
		startPosition += windowSizeField.length;
		
		packet[startPosition] = flags;
		startPosition += 1;
		
		System.arraycopy(serviceNameLength, 0, packet,
				startPosition,
				serviceNameLength.length);
		startPosition += serviceNameLength.length;
		
		System.arraycopy(dataLength, 0, packet,
				startPosition,
				dataLength.length);
		startPosition += dataLength.length;
		
		System.arraycopy(serviceName, 0, packet,
				startPosition,
				serviceName.length);
		startPosition += serviceName.length;
		
		System.arraycopy(data, 0, packet,
				startPosition,
				data.length);
	}
		
}
