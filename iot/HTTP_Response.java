//import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

public class HTTP_Response {
	
	public HashMap<Integer,String> reasonPhraseList;
	public HashMap<String,String> reasonPhrase;
	public HashMap<String,String> statusCode;
	
	public HTTP_Response() {
		reasonPhraseList = new HashMap<Integer,String>();
		reasonPhrase = new HashMap<String,String>();
		statusCode = new HashMap<String,String>();
		
		reasonPhraseList.put(1, "Informational");
		reasonPhraseList.put(2, "Success");
		reasonPhraseList.put(3, "Redirection");
		reasonPhraseList.put(4, "Client Error");
		reasonPhraseList.put(5, "Server Error");
		
		reasonPhrase.put("Informational", "Request received, continuing process");
		reasonPhrase.put("Success", "The action was successfully received, understood, and accepted");
		reasonPhrase.put("Redirection", "Further action must be taken in order to complete the request");
		reasonPhrase.put("Client Error", "The request contains bad syntax or cannot be fulfilled");
		reasonPhrase.put("Server Error", "The server failed to fulfill an apparently valid request");
		
		statusCode.put("100", "Continue");
		statusCode.put("101", "Switching Protocols");
		statusCode.put("200", "OK");
		statusCode.put("201", "Created");
		statusCode.put("202", "Accepted");
		statusCode.put("203", "Non-Authoritative Information");
		statusCode.put("204", "No Content");
		statusCode.put("205", "Reset Content");
		statusCode.put("206", "Partial Content");
		statusCode.put("300", "Multiple Choices");
		statusCode.put("301", "Moved Permanently");
		statusCode.put("302", "Found");
		statusCode.put("303", "See Other");
		statusCode.put("304", "Not Modified");
		statusCode.put("305", "Use Proxy");
		statusCode.put("307", "Temporary Redirect");
		statusCode.put("400", "Bad Request");
		statusCode.put("401", "Unauthorized");
		statusCode.put("402", "Payment Required");
		statusCode.put("403", "Forbidden");
		statusCode.put("404", "Not Found");
		statusCode.put("405", "Method Not Allowed");
		statusCode.put("406", "Not Acceptable");
		statusCode.put("407", "Proxy Authentication Required");
		statusCode.put("408", "Request Time-out");
		statusCode.put("409", "Conflict");
		statusCode.put("410", "Gone");
		statusCode.put("411", "Length Required");
		statusCode.put("412", "Precondition Failed");
		statusCode.put("413", "Request Entity Too Large");
		statusCode.put("414", "Request-URI Too Large");
		statusCode.put("415", "Unsupported Media Type");
		statusCode.put("416", "Requested range not satisfiable");
		statusCode.put("417", "Expectation Failed");
		statusCode.put("418", "I'm a teapot");
		statusCode.put("500", "Internal Server Error");
		statusCode.put("501", "Not Implemented");
		statusCode.put("502", "Bad Gateway");
		statusCode.put("503", "Service Unavailable");
		statusCode.put("504", "Gateway Time-out");
		statusCode.put("505", "HTTP Version not supported");
	}
}
