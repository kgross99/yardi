import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.StringTokenizer;

/*
 * Parses an HTTP GET request to determine if the request is valid in
 * all the parts of interest. The scope of the parser is limited to
 * validating the first line of a GET request.
 * 
 */
public class ParseGET {

	/*
	 * Data fields containing some regex pattern objects and some
	 * string containers.
	 */
    private Pattern requestPattern;
    
	private String validatedRequest;
	private String validatedMethod;
	private String validatedFile;
	private String httpVersion;
	private String clientRequest;
	
    private String responseStatusCode;
    private String responseReasonPhrase;

    /*
	 * Default constructor sets up several regex patterns and initializes
	 * some data fields to null.
	 */
	public ParseGET() {
	    requestPattern = Pattern.compile(
	            "^[A-Z]+ /[^ \t\r\n]* HTTP/[0-9].[0-9]\r\n");
	    
	    validatedRequest = null;
	    validatedMethod = null;
	    validatedFile = null;
	    httpVersion = null;
	    clientRequest = null;
	    responseStatusCode = null;
	    responseReasonPhrase = null;
	}
	
	/*
	 * Parses the request.
	 * Status code and response phrase are set by helper methods.
	 */
	public void parse(String clientRequest) {
	    this.clientRequest = clientRequest;
	    
	    if(!validateRequest()) {
	        return;
	    } else {
	        //System.out.println(validatedRequest);
	    }
	    
        if(!validateMethod()) {
            return;
        } else {
            //System.out.println(validatedMethod);
        }
        
        validateHttpVersionPattern();
        //System.out.println(httpVersion);
        
        validateFile();
        //System.out.println(validatedFile);
	}
	
	/*
	 * Verify whether the GET request conforms to RFC 2616, which is the
	 * standard for this assignment. Per RFC 2616, valid request has
	 * this format:
	 * Request-Line   = Method SP Request-URI SP HTTP-Version CRLF, where
	 * Method is an upper-case-only possibly consisting of "GET",
	 * SP is a space character,
	 * Request-URI is any string without space characters, and
	 * Version is "digit.digit"
	 */
	public boolean validateRequest() {
        validatedRequest = matchPattern(requestPattern, clientRequest);
        
        if(validatedRequest == null) {
            responseStatusCode = "400";
            responseReasonPhrase = "Bad Request";
            return false;
        } else {
            return true;
        }
	}
	
	/*
	 * Validate request method. If not valid, sets status code and
	 * response phrase.
	 */
	private boolean validateMethod() {
	    StringTokenizer st = new StringTokenizer(validatedRequest.trim(), " ");
	    String token = null;
	    
	    validatedMethod = null;
	    
	    if(st.hasMoreTokens()) {
	        token = st.nextToken();
	    } else {
            // this should have been screened out earlier, but it's
            // repeated here for completeness
            responseStatusCode = "400";
            responseReasonPhrase = "Bad Request";
            return false;
	    }
	    
	    if(token.equals("GET")) {
	        validatedMethod = token;
            return true;
        } else {
            responseStatusCode = "501";
            responseReasonPhrase = "Not Implemented";
            return false;
        }
	}
	
    /*
     * Verify that the file named in the GET request exists, is a
     * regular file and is not a symbolic link. Set status code and
     * response phrase based on file status.
     */
    private boolean validateFile() {
        if(!validateFilePattern()) {
            return false;
        } else {}
        
        if(validatedFile == null) {
            responseStatusCode = "404";
            responseReasonPhrase = "Not Found";
            return false;
        } else {}
        
        if(validatedFile.equals("/")) {
            responseStatusCode = "200";
            responseReasonPhrase = "OK";
            validatedFile = "index.html";
            return true;
        } else {}
        
        try {
            Path file = Paths.get("www/" + validatedFile);
            
            if(!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
                responseStatusCode = "404";
                responseReasonPhrase = "Not Found";
                validatedFile = "page_not_found.html";
                return false;
            } else {}
            
            BasicFileAttributes attrs =
                    Files.readAttributes(file, BasicFileAttributes.class);
            if(attrs.isRegularFile() && !attrs.isSymbolicLink()) {
                if(file.toFile().canRead()) {
                    responseStatusCode = "200";
                    responseReasonPhrase = "OK";
                    return true;
                } else {
                    responseStatusCode = "404";
                    responseReasonPhrase = "Not Found";
                    validatedFile = "page_not_found.html";
                    return false;
                }
            } else {
                responseStatusCode = "404";
                responseReasonPhrase = "Not Found";
                validatedFile = "page_not_found.html";
                return false;
            }
        } catch(IOException ex) {
            //System.out.println("Exception");
            responseStatusCode = "404";
            responseReasonPhrase = "Not Found";
            validatedFile = "page_not_found.html";
            return false;
        }
        
    }
    
    /*
     * Verifies that the requested file name is consistent with a valid
     * file name in a get request.
     */
    private boolean validateFilePattern() {
        StringTokenizer st = new StringTokenizer(validatedRequest.trim(), " ");
        
        validatedFile = null;
        
        // discard first token
        if(st.hasMoreTokens()) {
            st.nextToken();
        } else {
            // this should have been screened out earlier, but it's
            // repeated here for completeness
            responseStatusCode = "400";
            responseReasonPhrase = "Bad Request";
            return false;
        }
        
        if(st.hasMoreTokens()) {
            validatedFile = st.nextToken();
            if(validatedFile.startsWith("/") && !validatedFile.equals("/")) {
                validatedFile = validatedFile.substring(1);
            } else {}
            return true;
        } else {
            // this should have been screened out earlier, but it's
            // repeated here for completeness
            responseStatusCode = "400";
            responseReasonPhrase = "Bad Request";
            return false;
        }
    }
    
    /*
     * Verifies that the http version in the client request is consistent
     * with the pattern digit.digit.
     */
    private void validateHttpVersionPattern() {
        StringTokenizer st = new StringTokenizer(validatedRequest.trim(), " ");
        String token = null;
        
        httpVersion = null;
        
        // discard first two tokens
        for(int i = 0; st.hasMoreTokens() && i < 2; i++) {
            token = st.nextToken();
        }
        
        if(st.hasMoreTokens()) {
            token = st.nextToken();
            httpVersion = token;
            if(httpVersion.contains("HTTP/")) {
                httpVersion = httpVersion.substring(("HTTP/").length());
            } else {
                httpVersion = "";
            }
        } else {
            httpVersion = "";
        }
    }
    
	/*
	 * Return file name. Return value can be null.
	 */
	public String getFilename() {
		return validatedFile;
	}
	
    /*
     * Return response status code.
     */
    public String getStatusCode() {
        return responseStatusCode;
    }
    
    /*
     * Return response reason phrase.
     */
    public String getReasonPhrase() {
        return responseReasonPhrase;
    }
    
    /*
     * Return http version.
     */
    public String getHttpVersion() {
        return httpVersion;
    }
    
    /*
     * Attempts to match a pattern
     */
    private String matchPattern(Pattern pattern, String source) {
        String result = null;
        try {
            Matcher m = pattern.matcher(source);
            if(m.find()) {
                result = m.group();
                //System.out.println("1) " + source);
                return result;
            } else {
                //System.out.println("2) no match: " + source);
                return null;
            }
        } catch(IllegalStateException ex) {
            //System.out.println("3)");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }
    
}

