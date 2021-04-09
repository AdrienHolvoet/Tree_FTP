package fil.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import fil.project.utils.FileUtils;

/**
 * Unit test for simple App.
 */
public class FileUtilsTest {

	String file;
	String directory;
	String link;

	@Before
	public void setup() {
		directory = "drwxrwxr-x    3 997      997          4096 Jul 26  2018 bionic";
		file = "-rw-rw-r--    1 997      997          1150 Jun 16  2011 favicon.ico";
		link = "lrwxrwxrwx    1 997      997            26 Nov 21 22:03 ubuntu-ports -> /srv/ftp.root/ubuntu-ports";
	}

	@Test
	public void isDirectory_ShouldReturnTrue_WhenDirectoryInParam() {
		assertTrue(FileUtils.isDirectory(directory));
	}

	@Test
	public void isDirectory_ShouldReturnFalse_WhenFileInParam() {
		assertFalse(FileUtils.isDirectory(file));
	}

	@Test
	public void isDirectory_ShouldReturnFalse_WhenLinkInParam() {
		assertFalse(FileUtils.isDirectory(link));
	}

	@Test
	public void isLink_ShouldReturnTrue_WhenLinkInParam() {
		assertTrue(FileUtils.isLink(link));
	}

	@Test
	public void isLink_ShouldReturnFalse_WhenFileInParam() {
		assertFalse(FileUtils.isLink(file));
	}

	public void isLink_ShouldReturnFalse_WhenDirectoryInParam() {
		assertFalse(FileUtils.isLink(directory));
	}

	@Test
	public void getName_ShouldReturnCorrectName_WhenFullPathFileInParam() {
		assertEquals("favicon.ico", FileUtils.getName(file));
	}

	@Test
	public void getName_ShouldReturnCorrectName_WhenFullPathDirectoryInParam() {
		assertEquals("bionic", FileUtils.getName(directory));
	}

	@Test
	public void getName_ShouldReturnCorrectName_WhenFullPathLinkInParam() {
		assertEquals("/srv/ftp.root/ubuntu-ports", FileUtils.getName(link));
	}

}
