package fil.project;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import fil.project.client.FtpClient;
import fil.project.exceptions.BadCommandResponseException;

public class FtpClientTest {
	private FtpClient ftpClient;
	private String ftpAdress;
	private String anonymous;

	@Before
	public void setup() {
		ftpAdress = "ftp.free.fr";
		anonymous = "anonymous";
	}

	@Test
	public void connect_ShouldConnectTheSocket_WhenCorrectFtpAdressIsUsed() throws IOException {

		ftpClient = new FtpClient(ftpAdress, anonymous, anonymous);

		ftpClient.connect();

		assertTrue(ftpClient.getSocket().isConnected());
	}

	@Test(expected = UnknownHostException.class)
	public void connect_ShouldThrowUnknownHostException_WhenWrongFtpAdressIsUsed() throws IOException {

		ftpClient = new FtpClient("WRONG", anonymous, anonymous);
		ftpClient.connect();
	}

	@Test(expected = BadCommandResponseException.class)
	public void connect_ShouldThrowBadCommandResponseException_WhenWrongUsernameIsUsed() throws IOException {

		ftpClient = new FtpClient(ftpAdress, "WRONG", anonymous);

		ftpClient.connect();
	}

	@Test(expected = BadCommandResponseException.class)
	public void connect_ShouldThrowBadCommandResponseException_WhenWrongPasswordIsUsed() throws IOException {

		ftpClient = new FtpClient(ftpAdress, "WRONG", anonymous);

		ftpClient.connect();
	}

	@Test
	public void deepList_ShouldHaveASocketConnected_WhenIsFinish() throws Exception {

		ftpClient = new FtpClient(ftpAdress, anonymous, anonymous);

		ftpClient.connect();
		ftpClient.deepList();

		assertTrue(ftpClient.getSocket().isConnected());
	}

	@Test(expected = NullPointerException.class)
	public void deepList_ShouldThrowNullPointerException_WhenTheConnectMethodHasntBeenCall() throws Exception {

		ftpClient = new FtpClient(ftpAdress, anonymous, anonymous);

		ftpClient.deepList();

	}

	@Test
	public void disconnect_ShouldCloseTheSocket_WhenSocketIsOpen() throws IOException {

		ftpClient = new FtpClient(ftpAdress, anonymous, anonymous);

		ftpClient.connect();

		ftpClient.disconnect();

		assertTrue(ftpClient.getSocket().isClosed());
	}

	@Test(expected = NullPointerException.class)
	public void disconnect_ShouldHaveASocketNull_WhenSocketIsClose() throws IOException {

		ftpClient = new FtpClient(ftpAdress, anonymous, anonymous);

		ftpClient.disconnect();

		assertNull(ftpClient.getSocket());
	}
}
