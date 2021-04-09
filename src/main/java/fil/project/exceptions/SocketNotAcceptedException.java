package fil.project.exceptions;

/**
 * Exception thrown when the socket receives an unknown response when connecting
 * to the FTP Server
 * 
 * @author Adrien Holvoet
 */
public class SocketNotAcceptedException extends RuntimeException {

	private static final long serialVersionUID = 1013982018865267421L;

	/**
	 * Constructor
	 * 
	 * @param message Message of exception
	 */
	public SocketNotAcceptedException(String message) {
		super(message);
	}
}
