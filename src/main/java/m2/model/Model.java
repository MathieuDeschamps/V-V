package m2.model;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
/**
 * 
 * @author DESCHAMPS Mathieu && ESNAULT Jeremie
 *Class describing theresults of the tests of a class tests and the execution trace
 */
public class Model {
	
	private String fileName;
	private int nOkTest;
	private int nKoTest;
	private int nIgnoredTest;
	private String executionTrace;
	private Map<String, BufferedImage> images;

	public Model ( String fileName, int nOkTests, int nKoTests, int nIgnoredTests, String trace, Map<String, BufferedImage> images)
	{
		this.fileName = fileName;
		this.nOkTest = nOkTests;
		this.nKoTest = nKoTests;
		this.nIgnoredTest = nIgnoredTests;
		this.executionTrace = trace;
		this.images = images;
	}

	public String getFileName() {
		return fileName;
	}

	public int getnOkTest() {
		return nOkTest;
	}

	public int getnKoTest() {
		return nKoTest;
	}

	public int getnIgnoredTest() {
		return nIgnoredTest;
	}

	public String getExecutionTrace() {
		return executionTrace;
	}
	
	public Map<String, BufferedImage> getImages() {
		return images;
	}


}
