package fil.project;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import fil.project.client.FtpClient;
import fil.project.client.FtpClientInterface;
import fil.project.exceptions.WrongArgumentException;

/**
 * Application entry point with 3 arguments 1 : the address of the remote FTP
 * server - mandatory 2 : username - optional 3 : password - optional
 * 
 * @author Adrien Holvoet
 */

public class Main {

	// Class constant
	private static final String ANONYMOUS = "anonymous";

	public static void main(String[] args) throws Exception {
		try {
			if (args.length == 0)
				throw new WrongArgumentException(
						"You must at least specify an argument corresponding to the remote FTP server, under this format: executable server_name");

			if (args.length > 3)
				throw new WrongArgumentException(
						"The maximum number of arguments is 3 with the form executable argument1 argument2 (optional) argument3 (optional) ");

			FtpClientInterface ftpClient = new FtpClient(args.length > 0 ? args[0] : ANONYMOUS,
					args.length > 1 ? args[1] : ANONYMOUS, args.length > 2 ? args[2] : ANONYMOUS);

			ftpClient.connect();
			ftpClient.deepList();
			ftpClient.disconnect();

		} catch (SocketTimeoutException e) {
			throw new SocketTimeoutException("The FTP server has too many files to read: " + e.getMessage());
		} catch (SocketException e) {
			throw new SocketException("FTP client no longer has internet access : " + e.getMessage());
		}
	}
}
