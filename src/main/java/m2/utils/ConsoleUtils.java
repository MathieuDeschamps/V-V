package m2.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * 
 * @author DESCHAMPS Mathieu && ESNAULT Jeremie
 * This class has the role to provide method which will change the behavior of the console
 *
 */
public class ConsoleUtils {
	
	/**
	 * Redirection the System.out into the outputPath
	 * @param outputPath where is redirect the System.out
	 */
	public static void redirect(String outputPath) {
		PrintStream out;
		try {
			out = new PrintStream(
					new FileOutputStream(outputPath, false));
			System.setOut(out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Redirect the System.out into the newPrintStream
	 * @param newPrintStream where is redirect the System.out
	 */
	public static void redirect(PrintStream newPrintStream) {
		System.setOut(newPrintStream);
	}
}
