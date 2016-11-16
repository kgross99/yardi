import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.lang.InterruptedException;

/**
 * This thread class services browser connections established by a
 * socket accept method running in a calling class.
 */
public class BrowserProxyService implements Runnable {
	
	private Socket connection;			//The TCP connection to this object's client
	private Scanner in;					//Used to receive data from the client
	private PrintStream out;			//Used to send data to the client
	private static String[] timeDocs;  //Array of time.html docs for all aft's
	private static int nConnections=0;	//Number of established connections
	private static boolean debug;
	private BufferedReader br;
	private String receivedMessage;
	static ParseGET parser;
	static File DiskFile;
	 static FileInputStream DiskReader;
	 BufferedOutputStream toClient;

	/* Constructor builds ConnectionService for a specified connection */
	BrowserProxyService(Socket connection, int nAfts, boolean debug) throws IOException {
		this.connection = connection;		
		in = new Scanner(connection.getInputStream());
		 br = new BufferedReader(
                 new InputStreamReader(connection.getInputStream()));
		 toClient = new BufferedOutputStream(
                 connection.getOutputStream());
		out = new PrintStream(connection.getOutputStream());
		timeDocs = new String[nAfts];
		/* element at index == 0 will never be used because aft id numbers are
		 * one-based, not zero-based */
		timeDocs[0] = "";
		this.debug = debug;
		if(debug) incrementNConnections();
	}
	  public BrowserProxyService(Socket connection2, boolean dEBUG2,
			ConcurrentLinkedDeque<AftServicer> aftCache) {
		// TODO Auto-generated constructor stub
	}
	private static String createResponseHeader() {
	        StringBuilder responseString = new StringBuilder();
	        String httpVersion = parser.getHttpVersion();
	        responseString.append("HTTP/" + httpVersion + " ");
	        responseString.append(parser.getStatusCode() + " ");
	        responseString.append(parser.getReasonPhrase() + "\r\n");
	        responseString.append("Request Version: HTTP/" + httpVersion + "\r\n");
	        responseString.append("Status Code: " + parser.getStatusCode() + "\r\n");
	        responseString.append("Response Phrase: " + parser.getReasonPhrase() + "\r\n");
	        responseString.append("Server:BigSecret/.9.9.9(My OS)\r\n");
	        responseString.append("Content-language: en\r\n");
	        responseString.append("Content-Type: text/html; charset=UTF-8\r\n");
	        String statusCode = parser.getStatusCode();
	        if(statusCode.equals("200") || statusCode.equals("404")) {
	            File DiskFile=new File("www/" + parser.getFilename());
	            String FLength="Content-Length: " + DiskFile.length() + "\r\n";
	            responseString.append(FLength);
	            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	            String Modified="Last-Modified: "+sdf.format(DiskFile.lastModified())+"\r\n";
	            responseString.append(Modified);
	        } else {}
	        responseString.append("Date: " + (new Date()).toString() + "\r\n\r\n");
	        return responseString.toString();
	    }
	  
	  private static void copyFile(String filename, BufferedOutputStream toClient){
	        try{
	            DiskFile=new File(filename);
	            DiskReader=new FileInputStream(DiskFile);
	            int content;
	            while ((content = DiskReader.read()) != -1) {
	                toClient.write(content);
	            }

	        } catch (IOException e){
	            System.out.println("IO Error: " + e.getMessage());
	            e.printStackTrace();

	        }
	        try {
	            DiskReader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	/** This is the Runnable interface's run method servicing the connection.
	 * This method is not synchronized because each object's thread accesses
	 * *only* the instance data for that object --- multiple threads are not
	 * accessing the exact same variables/objects.
	 */
	@Override
	public void run() {
		try {
			/* Read lines until client closes connection */
			
		   
            char[] cbuf = new char[1024];
            br.read(cbuf, 0, 1024);
            receivedMessage = String.valueOf(cbuf);
            parser = new ParseGET();
            parser.parse(receivedMessage);
            String filename = parser.getFilename();
            out.write(createResponseHeader().getBytes());
            String statusCode = parser.getStatusCode();
            if(statusCode.equals("200") || statusCode.equals("404")) {
                copyFile("www/" + filename, toClient);
            } else {}
			
			
			
			
			
			
			
			
			while(in.hasNextLine()) {
				String request = in.nextLine();
				if(debug) System.out.println(request);
				int i = Integer.parseInt(request);
				while(timeDocs[i] == null) {
					Thread.sleep(100);
				}
				if(debug) System.out.println("Browser Proxy Serving timeDoc: " +
						timeDocs[i]);
				out.println(timeDocs[i]);
			}
		} catch(InterruptedException ex) {
			/* we've been interrupted!
			 * nothing to do, just move on to closing the resources
			 */
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* Close the connection (flushes data through to client) */
		try {
			connection.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if(debug) decrementNConnections();
	}
	
	/** Increment count of connections.  This method is synchronized because multiple threads
	 *will increment the static nConnections variable concurrently.
	 */
	static synchronized void incrementNConnections() {System.out.println(++nConnections);}
	
	/** Decrement count of connections.  This method is synchronized because multiple threads
	 *will decrement the static nConnections variable concurrently.
	 */
	static synchronized void decrementNConnections() {System.out.println(--nConnections);}
	
	/**
	 * Provides for updating the resources that are served. Synchronized
	 * because multiple aft servicing threads use this to concurrently update a
	 * common collection of static resources that may be served to browsers.
	 * @param doc String that contains a page to be served.
	 * @param index Index to the array of server pages for the aft
	 * whose ID number matches the index value.
	 */
	static synchronized void updateTimeDoc(String doc, int index) {
		timeDocs[index] = doc;
		System.out.println("timeDoc updated: " + timeDocs[index]);
	}

}

