import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.BindException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is a simple HTTP server run from the command line.  It takes a single optional integer argument
 * as a port number and will run indefinitely.  If no argument is passed, this server will default to
 * TCP port 8080.
 */
public class BareServer {
    static FileInputStream DiskReader;
    static ParseGET parser;
    static File DiskFile;

    /**
     * Copies the passed string into the passed BufferedOutputStream.
     * 
     * @param A filename
     * @param A connection to the client
     */
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

    /**
     * Creates the HTTP response header containing the HTTP version, status code, response phrase,
     * server version, language, and content-type.  Each header line concludes with CRLF.
     * 
     * @return the server's response header
     */
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

    public static void main(String[] args) throws IOException {
        String receivedMessage;
     

        int portNumber=8080;
        if (args.length > 1) {
            printUsage();
            System.exit(1);
        } else if (args.length ==1) {
            try {
                portNumber = Integer.parseInt(args[0]);// parses first command line option for port number
            } catch (NumberFormatException e) {
                printUsage();
                System.exit(1);
            }
        } else {
            portNumber = 8080;
            System.out.println("Defaulting to port 8080");
        }
        System.out.println("Using port number " + portNumber
                + " to listen for requests");

        /*
         * Create a server socket on the specified porti after the port is
         * created start waiting for data until a client connects.
         */
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Created server socket");
            while (true){

                System.out.println("Waiting for connection");
                Socket clientSocket = serverSocket.accept();
                System.out.println("accepted connection to "+clientSocket.getInetAddress());
                Date date = new Date();
                System.out.println(date.toString());
                /*
                 * Use an output stream rather than a printwriter so all types of files
                 * can be sent rather than just text.
                 */ 
                BufferedOutputStream toClient = new BufferedOutputStream(
                        clientSocket.getOutputStream());

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                char[] cbuf = new char[4096];
                br.read(cbuf, 0, 4096);
                receivedMessage = String.valueOf(cbuf);
                System.out.println(receivedMessage);

                /*
                 * parser the client's request and respond appropriately.
                 */
                parser = new ParseGET();
                parser.parse(receivedMessage);
                String filename = parser.getFilename();
                toClient.write(createResponseHeader().getBytes());
                String statusCode = parser.getStatusCode();
                if(statusCode.equals("200") || statusCode.equals("404")) {
                    copyFile("www/" + filename, toClient);
                } else {}
                
                /*
                 * Clean up.
                 */
                toClient.flush();
                toClient.close();
                clientSocket.close();
            }
        } catch(BindException ex) {
            //ex.printStackTrace();
            System.err.println("Please use another port. " + ex.getMessage());
        }
    }
    
    /**
     * Prints the program usage to the command line.
     */
     private static void printUsage(){
         System.out.println("Usage: java BareServer [port number]");
     }
}
