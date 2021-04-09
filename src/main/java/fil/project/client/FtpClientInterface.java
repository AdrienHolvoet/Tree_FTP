package fil.project.client;

import java.io.IOException;

/**
 * Defined the main methods necessary for the proper functioning of the FTP
 * Client which are: connection, in-depth browsing, and disconnection
 * 
 * @author Adrien Holvoet
 */

public interface FtpClientInterface {

	/**
	 * Connects to an FTP server and logs in with the supplied username and
	 * password.
	 * 
	 * @throws IOException
	 */
	public void connect() throws IOException;

	/**
	 * Disconnects from the FTP server.
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException;

	/**
	 * Browse the current directory in depth
	 * 
	 * @throws Exception
	 */
	public void deepList() throws Exception;

}
