package fil.project.exceptions;

/**
 * Exception thrown when there is a bad argument when launching the executable
 * 
 * @author Adrien Holvoet
 */
public class WrongArgumentException extends RuntimeException {

	private static final long serialVersionUID = 3892287429020551760L;

	/**
	 * Constructor
	 * 
	 * @param message Message of exception
	 */
	public WrongArgumentException(String message) {
		super(message);
	}
}
