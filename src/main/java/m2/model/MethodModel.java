package m2.model;

import java.util.ArrayList;
import java.util.List;

import javassist.CtMethod;
import javassist.expr.MethodCall;

public class MethodModel {
	
	private String name;
	private String className;
	private List<String> parameters;
	private String returnType;
	
	public MethodModel(String name, String className, List<String> parameters, String returnType) {
		super();
		this.name = name;
		this.className = className;
		this.parameters = parameters;
		this.returnType = returnType;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public List<String> getParameters() {
		return parameters;
	}
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof MethodModel)) {
			return false;
		}
		MethodModel mm = (MethodModel) o;
		if(parameters.size() != mm.parameters.size()) {
			return false;
		}
		for(int i = 0; i < parameters.size(); i++) {
			if(!parameters.get(i).equals(mm.parameters.get(i))) {
				return false;
			}
		}
		return name.equals(mm.name) &&
			className.equals(mm.className) &&
			returnType.equals(mm.returnType);
				
	}
	
	public static  MethodModel parseMethodModel(CtMethod m) {
		String name = m.getName();
		String className = m.getDeclaringClass().getName();
		List<String> parameters = new ArrayList<String>();
		String returnType = "";
		
		return new MethodModel(name, className, parameters, returnType);
		
	}
	
	public static MethodModel parseMethodModel(MethodCall mc) {
		String name = mc.getMethodName();
		String className = mc.getClassName();
		List<String> parameters = new ArrayList<String>();
		String returnType = "";
		
		return new MethodModel(name, className, parameters, returnType);
	}
	
	@Override
	public String toString() {
		String result = "";
		result += name;
		return result;
				
	}
	
}
