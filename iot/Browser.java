import java.io.*;
import java.net.*;
 
/**
 * This class is a simplistic stand-in for an actual browser program.
 * It does connect to the browser proxy server, requests a document and
 * receives a document. It prints the document to stdout.
 * It requests a document from a specific aft id number.
 * It only works with up to two afts.
 */
public class Browser {
	
    public static void main(String[] args) throws IOException {
         
        if (args.length != 3) {
            System.err.println(
                "Usage: java Browser <host name> <port number> <aft number>");
            System.exit(1);
        } else {}
 
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        int aftNumber = Integer.parseInt(args[2]);
 
        try (
            Socket socket = new Socket(hostName, portNumber);
            PrintWriter out =
                new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        ) {
        	System.out.println("Browser says: gimmie yer data!");
        	/* a request is just the number of the aft whose resources are
        	 * sought. */
        	out.println(String.format("%d", aftNumber));
            System.out.println("browser recv'd: " + in.readLine());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        } 
    }
}
