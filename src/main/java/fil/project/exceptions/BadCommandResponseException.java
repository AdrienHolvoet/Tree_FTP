package fil.project.exceptions;

/**
 * Exception thrown when the socket does not receive a successful response from
 * the FTP server after sending a File Transfer Protocol (RFC 959) command.
 * 
 * @author Adrien Holvoet
 */
public class BadCommandResponseException extends RuntimeException {

	private static final long serialVersionUID = 3383647278108352721L;

	/**
	 * Constructor
	 * 
	 * @param message Message of exception
	 */
	public BadCommandResponseException(String message) {
		super(message);
	}
}
