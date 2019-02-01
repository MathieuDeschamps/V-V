package m2.model;

public class MethodInfo {
	
	private String className;
	private String name;
	private int beginLine;
	
	public MethodInfo(String name, String className) {
		this.name = name;
		this.className = className;
	}
	
	public void setBeginLine(int beginLine) {
		this.beginLine = beginLine;
	}
}
