package m2.model;
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
	
	public Model ( String fileName, int nOkTests, int nKoTests, int nIgnoredTests, String trace )
	{
		this.fileName = fileName;
		this.nOkTest = nOkTests;
		this.nKoTest = nKoTests;
		this.nIgnoredTest = nIgnoredTests;
		this.executionTrace = trace;	
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
	

}
