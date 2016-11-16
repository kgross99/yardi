import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
 
/**
 * This class is a simplistic stand-in for an actual aft program.
 * It does connect to an aft servicing thread, receive queued
 * requests for document updates and sends document updates.
 * It supplies the time.
 */
public class Aft {
 private static int sleepSeconds;
 private static int broadcastPort;
 private static int listeningPort;
 private static int aftID;
 private static String aftName;
 private static String myPFT;
 private static InetAddress pftAddress;
 
 
    public static void main(String[] args) throws IOException {
    	
        if (args.length != 3) {
            printUsage();
            System.exit(1);
        } else {
        	broadcastPort= Integer.parseInt(args[0]);
        	sleepSeconds = Integer.parseInt(args[1]);
        	aftName = args[2];
        	listeningPort = -1;
        	aftID = -1;
        	myPFT = "";
        	pftAddress = null;
        }

	    DatagramSocket socket = new DatagramSocket();
        try {
        	// Sending out initial wakeup.
            AftPacket aftBroadSendPack = new AftPacket(false, false, false, "index.html", "0",
            		getAftName(), "kcna?", getAftID(), 0, getIndex());
	        // create a datagram socket
            byte[] broadSendBuf = aftBroadSendPack.encode();
	        InetAddress group = InetAddress.getByName("203.0.113.0");  // "Borrowed" from Oracle's tutorial
            DatagramPacket broadSendPacket = new DatagramPacket(broadSendBuf, broadSendBuf.length, group, broadcastPort);
            socket.send(broadSendPacket);
            
            try {
            	Thread.sleep(10000);
            } catch(InterruptedException e){
            	
            }
            // Receive response from PFT
            byte[] broadRecvBuf = new byte[65536];
            DatagramPacket broadRecvPacket = new DatagramPacket(broadRecvBuf, broadRecvBuf.length);
            AftPacket aftBroadRecvPack = new AftPacket(broadRecvPacket.getData());
            
            // Extract information from response
            listeningPort = broadRecvPacket.getPort();
            aftID = aftBroadRecvPack.getAftId();
            myPFT = aftBroadRecvPack.getPftName();
            pftAddress = broadRecvPacket.getAddress();
            
            try {
            	Thread.sleep(sleepSeconds * 1000);
            } catch(InterruptedException e){
            	
            }
            
            // Request work and sleep cycle
            while (true){ 
            	// Create work inquiry
            	AftPacket aftCheckWorkPack = new AftPacket(true, false, false, "0", getMyPFT(),
            			getAftName(), "work?", getAftID(), 0, "0");
            	byte[] checkWorkBuf = aftCheckWorkPack.encode();
            	DatagramPacket checkWorkPack = new DatagramPacket(checkWorkBuf, checkWorkBuf.length,
            			getPFTAddress(), getCurrentPort());
            	socket.send(checkWorkPack);
            	
            	checkWorkBuf = new byte[65536];
            	checkWorkPack = new DatagramPacket(checkWorkBuf, checkWorkBuf.length);
            	socket.receive(checkWorkPack);
            	
            	aftCheckWorkPack = new AftPacket(checkWorkPack.getData());
            	
            	if (aftCheckWorkPack.getRequestBit()){
            		String resourceName = "";
            		String resourceContent = "";
            		
            		if (aftCheckWorkPack.getPageName().contains("index")){
            			resourceName = "index.html";
            			resourceContent = getIndex();
            		} else if (aftCheckWorkPack.getPageName().contains("date")){
            			resourceName = "date.html";
            			resourceContent = getDate();
            		} else if (aftCheckWorkPack.getPageName().contains("time")){
            			resourceName = "time.html";
            			resourceContent = getTime();
            		} else if (aftCheckWorkPack.getPageName().contains("config")){
            			resourceName = "config.html";
            			resourceContent = getConfig();
            		}
            		AftPacket aftWork = new AftPacket // TODO
            	} else {
            		
            	}
            	
                try {
                	Thread.sleep(sleepSeconds * 1000);
                } catch(InterruptedException e){
                	
                }
            }
        } catch (IOException e){
        	e.printStackTrace();
        }
        /*
        // notify aft servicing thread that we are awake
        byte[] buf = new byte[256];
        buf = String.format("aft# %d to aft service provider", port % 8080).getBytes(Charset.forName("UTF-8"));
        InetAddress address = InetAddress.getByName("127.0.0.1");
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
     
        // get aft servicing thread's request to update a resource
        buf = new byte[256];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
 
        // display the request
        String received = new String(packet.getData(), "UTF-8");
        System.out.println(String.format("aft# %d recv'd: %s", port % 8080, received));
     
        // respond to the request with an updated document
        buf = new byte[256];
        buf = String.format("aft# %d time: %s", port % 8080, new Date().toString()).getBytes(Charset.forName("UTF-8"));
        packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        // print what was sent
        System.out.println(new String(packet.getData(), "UTF-8")); */
        
        socket.close();
    }
    
    private static void printUsage(){
    	System.err.println("Usage: java Aft <Initial Port Number> <Initial Sleep Time>");
    }
    
    /**
     * Returns a string representing an HTML document displaying current time on the aft
     * Format: HH:mm:ss
     * 
     * @return the current time surrounded by HTML tags
     */
    public static String getTime(){
     DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
     Date date = new Date();
     return getHTMLTop() + "<h1> The current time is: " + dateFormat.format(date) + "</h1>" + getHTMLBottom();
    }
    
    /**
     * Returns a string representing an HTML document displaying the current date of the aft
     * Format: MM/dd/yy
     * 
     * @return the current date surrounded by HTML tags
     */
    public static String getDate(){
     DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
     Date date = new Date();
     return getHTMLTop() + "<h1>The current date is: " + dateFormat.format(date) + "</h1>" + getHTMLBottom();
    }
    
    /**
     * Returns an HTML string containing a form which configures the AFT's
     * sleep time in seconds using the HTTP GET method.
     * 
     * @return a form in HTML tags
     */
    public static String getConfig(){
     return getHTMLTop() + "Please specify a new sleep interval in seconds: <form method ='GET'>"
     + "<input type='text' name='newSleepTime' placeholder='Integer goes here'>"
     + "<input type='submit' value='Submit'></form>"
     + getHTMLBottom();
    }
    
    /**
     * Returns an HTML string containing the AFT's welcome page
     * @return
     */
    public static String getIndex(){
     return getHTMLTop() + "<h1>You have connected to AFT " + getAftID()
      + "!</h1><p>This AFT is configured to sleep for " + getSleepSeconds() + " seconds.  "
      + "You can update this on the <a href='config.html'>config page</a>.</p>" + getHTMLBottom();
    }
    
    /**
     * Returns opening HTML tags.
     * @return
     */
    public static String getHTMLTop(){
     return "<html><head><title>Welcome to AFT " + getAftID() + "!</title></head><body>";
    }
    
    /**
     * Returns closing HTML tags.
     * @return
     */
    public static String getHTMLBottom(){
     return "</body></html>";
    }
    
    /**
     * Returns the aftID as a string.
     * 
     * @return
     */
    public static int getAftID(){
     return aftID;
    }
    
    public static String getAftName(){
    	return aftName;
    }
    
    /**
     * Returns the name of the associated PFT.
     * @return
     */
    public static String getMyPFT(){
    	return myPFT;
    }
    
    /**
     * Returns the sleep interval in seconds
     * 
     * @return
     */
    public static int getSleepSeconds() {
     return sleepSeconds; //TODO have not initialized
    }
    
    /**
     * Returns the current listening port
     * @return
     */
    public static int getCurrentPort() {
    	return listeningPort;
    }
    
    public static InetAddress getPFTAddress(){
    	return pftAddress;
    }
    
    /**
     * Sets the port number on which to communicate with the PFT.
     * 
     * @param the new port number
     */
    public static void setPortNumber(int port){
     listeningPort = port;
    }
}
