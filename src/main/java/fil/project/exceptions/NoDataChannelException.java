package fil.project.exceptions;

/**
 * Exception thrown when it's impossible to create the connection to the data
 * channel.
 * 
 * @author Adrien Holvoet
 */
public class NoDataChannelException extends RuntimeException {

	private static final long serialVersionUID = -240499385650836769L;

	/**
	 * Constructor
	 * 
	 * @param message Message of exception
	 */
	public NoDataChannelException(String message) {
		super(message);
	}
}
