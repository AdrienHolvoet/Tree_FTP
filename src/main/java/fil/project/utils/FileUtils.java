package fil.project.utils;

/**
 * FileUtils(Singleton pattern) used to manage the print of folders / files
 * retrieved by FtpClient in a specific format . Distinguish files from folders.
 * Get the name of the file/folder Put at final for finalizing the
 * implementation of the class, methods, and variables like a static class
 * 
 * @author Adrien Holvoet
 */
public final class FileUtils {

	// All static final because there are all constants
	public static final char DIRECTORY = 'd';
	public static final char LINK = 'l';
	public static final String VERTICAL_BAR = "\u2502";
	public static final String SEMI_VERTICAL_BAR = "\u2514";
	public static final String HORIZONTAL_BAR = "\u2500";

	/**
	 * Prevent instantiation
	 */
	private FileUtils() {
	}

	/**
	 * Print a folder to standard output
	 * 
	 * @param folder
	 */
	public static void printDirectory(String folder) {
		System.out.println("+--/" + folder);
	}

	/**
	 * Print a Link to standard output
	 * 
	 * @param link
	 */
	public static void printLink(String link) {
		System.out.println(SEMI_VERTICAL_BAR + HORIZONTAL_BAR + "> " + link);
	}

	/**
	 * Print a file to standard output
	 * 
	 * @param file
	 */
	public static void printFile(String file) {
		System.out.println(SEMI_VERTICAL_BAR + HORIZONTAL_BAR + HORIZONTAL_BAR + " " + file);
	}

	/**
	 * Print on the standard output the information if the folder is empty or if the
	 * server failed to listing the directory
	 * 
	 * @param message
	 * @param indent  the current indent to keep the consistency in the tree display
	 */
	public static void printEmptyOrNotOpen(String indent, String message) {
		System.out.println(indent + " " + message);
	}

	/**
	 * Check if the string passed in parameter is a directory
	 * 
	 * @param file can be a folder a file or a link.
	 * @return boolean
	 */
	public static boolean isDirectory(String file) {
		return file.charAt(0) == DIRECTORY;
	}

	/**
	 * Check if the string passed in parameter is a file
	 * 
	 * @param file can be a folder a file or a link.
	 * @return boolean
	 */
	public static boolean isLink(String file) {
		return file.charAt(0) == LINK;
	}

	/**
	 * Get the name of the file/link/folder passed in parameter
	 * 
	 * @param str can be a folder a file or a link.
	 * @return name
	 */
	public static String getName(String str) {
		String[] arrOfStr = str.split(" ");
		String name = arrOfStr[arrOfStr.length - 1];
		return name;
	}
}
