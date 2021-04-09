package fil.project.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import fil.project.exceptions.BadCommandResponseException;
import fil.project.exceptions.NoDataChannelException;
import fil.project.exceptions.SocketNotAcceptedException;
import fil.project.utils.FileUtils;

/**
 * FtpClient used to display the tree structure of a remote directory(FTP
 * Server) on the standard output of a terminal and contains all the necessary
 * commands from RFC 959 (File Transfer Protocol)
 * 
 * @author Adrien Holvoet
 */
public class FtpClient implements FtpClientInterface {

	// All static final because there are all constants
	public static final int PORT_MULTIPLIER = 256;
	public static final int FTP_PORT = 21;
	public static final String LIST = "LIST";
	public static final String USER = "USER ";
	public static final String PASS = "PASS ";
	public static final String QUIT = "QUIT ";
	public static final String CWD = "CWD ";
	public static final String PWD = "PWD ";
	public static final String PASV = "PASV ";
	public static final String INDENT = FileUtils.VERTICAL_BAR + "  ";

	// All private because there are all only used inside the class
	private String server;
	private String user;
	private String password;

	// Variables used for the command channel
	private Socket socket;
	private OutputStream osCommand;
	private PrintWriter printerCommand;
	private InputStream clCommand;
	private BufferedReader readerClCommand;

	// Variables used for the data channel
	private InputStream clData;
	private BufferedReader readerClData;

	/**
	 * Constructor
	 * 
	 * @param server
	 * @param user
	 * @param password
	 */
	public FtpClient(String server, String user, String password) {
		this.server = server;
		this.user = user;
		this.password = password;
	}

	/**
	 * Connects to an FTP server and logs in with the supplied username and
	 * password.
	 * 
	 * @throws IOException
	 */
	public void connect() throws IOException {
		try {

			socket = new Socket(server, FTP_PORT);

			// Returns an output stream for transmitting the data to the socket
			osCommand = socket.getOutputStream();
			printerCommand = new PrintWriter(osCommand, true);

			// Return an input stream to receive the data from the socket
			clCommand = socket.getInputStream();
			readerClCommand = new BufferedReader(new InputStreamReader(clCommand));

			String response = readerClCommand.readLine();
			if (!response.startsWith("220")) {
				throw new SocketNotAcceptedException(
						"TreeFTP receive  an unknown response when connecting the socket to the FTP Server : "
								+ response);
			}

			response = this.sendCommand(USER + this.user);
			if (!response.startsWith("331")) {
				throw new BadCommandResponseException("TreeFTP failed to connect with this username : " + response);
			}

			response = this.sendCommand(PASS + this.password);

			if (!response.startsWith("230")) {
				throw new BadCommandResponseException("TreeFTP failed to connect with this password : " + response);
			}

		} catch (UnknownHostException e) {
			throw new UnknownHostException("TreeFtp does not recognize this host : " + e.getMessage());

		} catch (ConnectException e) {
			throw new ConnectException("TreeFtp could not connect to the server : " + e.getMessage());
		}
	}

	/**
	 * Disconnects from the FTP server.
	 * 
	 * @throws IOException
	 * 
	 */
	public void disconnect() throws IOException {
		try {
			this.sendCommand(QUIT);
			System.out.println("END");
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	/**
	 * Browse the current directory in depth
	 * 
	 * @throws IOException
	 */
	public void deepList() throws Exception {

		String response = this.sendCommand(PWD);

		if (!response.startsWith("257")) {
			throw new BadCommandResponseException("TreeFTP failed to retrieve the main directory : " + response);
		}

		String firstDirectory = response.split("\"")[1];
		if (!firstDirectory.equals("/")) {
			FileUtils.printDirectory(firstDirectory);
		} else {
			FileUtils.printDirectory("");
		}

		this.list("", INDENT);
	}

	/**
	 * Communicates with the data exchange channel to returns the information /
	 * content of the directory specified in parameter
	 * 
	 * @throws Exception
	 * @param directory the path of the directory listed
	 * @param indent    the indentation related to the depth of the folder in the
	 *                  tree structure
	 */
	private void list(String directory, String indent) throws Exception {

		String response = null;

		if (directory != "") {
			this.sendCommand(CWD + directory);
		}

		Socket dataSocket = this.dataExchangeChannel();
		ArrayList<String> datas = new ArrayList<String>();

		this.sendCommand(LIST);

		clData = dataSocket.getInputStream();
		readerClData = new BufferedReader(new InputStreamReader(clData));

		while ((response = readerClData.readLine()) != null) {
			datas.add(response);
		}

		response = readerClCommand.readLine();
		// if the response is null it's mean that the server closed the connection
		// because it failed to retrieve directory listing
		// but we still want to see the others folder so we reconnect
		if (datas.size() == 0 || response == null) {
			String message = null;
			if (datas.size() == 0) {
				message = "Empty directory listing.";
			}

			if (response == null) {
				this.connect();
				message = "Failed to retrieve directory listing.";
			}
			FileUtils.printEmptyOrNotOpen(indent, message);
		}
		printTree(datas, indent, directory);
	}

	/**
	 * Display the contents of the directory passed as a parameter in the form of a
	 * tree
	 * 
	 * @throws Exception
	 * @param directory the path of the current directory listed
	 * @param indent    the indentation related to the depth of the folder in the
	 *                  tree structure
	 * @param datas     the data files/folder contained inside the directory
	 *                  parameter
	 */
	private void printTree(ArrayList<String> datas, String indent, String directory) throws Exception {
		String name;
		for (String s : datas) {
			System.out.print(indent);
			name = FileUtils.getName(s);
			if (FileUtils.isDirectory(s)) {
				FileUtils.printDirectory(name);
				if (!name.equals(".") && !name.equals("..")) // to avoid infinite recursion on these folders (experiment
																// with on local windows ftp)
				{
					this.list(directory + "/" + FileUtils.getName(s), indent + INDENT);
				}
			} else if (FileUtils.isLink(s)) {
				FileUtils.printLink(name);
			} else {
				FileUtils.printFile(name);
			}
		}
	}

	/**
	 * Construction of the data exchange channel
	 * 
	 * @throws Exception
	 */
	private Socket dataExchangeChannel() throws Exception {

		String response = this.sendCommand(PASV);

		if (!response.startsWith("227")) {
			throw new BadCommandResponseException("FreeFtp could not connect in passive mode: " + response);
		}

		int begin = response.indexOf('(');
		int end = response.indexOf(')', begin + 1);

		String dataExchange = response.substring(begin + 1, end);
		String[] datasExchangeArray = dataExchange.split(",");
		String ip = null;
		int port = -1;

		try {
			ip = datasExchangeArray[0] + "." + datasExchangeArray[1] + "." + datasExchangeArray[2] + "."
					+ datasExchangeArray[3];
			port = Integer.parseInt(datasExchangeArray[4]) * PORT_MULTIPLIER + Integer.parseInt(datasExchangeArray[5]);
		} catch (Exception e) {
			throw new NoDataChannelException("TreeFtp did not receive all the data to connect to the data channel.");
		}
		return new Socket(ip, port);
	}

	/**
	 * Returns the response sent by the server of the command passed as a parameter
	 * 
	 * @param command
	 */
	private String sendCommand(String command) throws IOException {
		printerCommand.println(command);
		return readerClCommand.readLine();
	}

	public Socket getSocket() {
		return this.socket;
	}
}
