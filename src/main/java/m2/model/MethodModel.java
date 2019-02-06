package m2.model;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.MethodCall;

public class MethodModel {
	
	private String name;
	private String className;
	private List<ParameterModel> parameters;
	private String returnType;
	
	public MethodModel(String name, String className, List<ParameterModel> parameters, String returnType) {
		super();
		this.name = name;
		this.className = className;
		if(parameters == null) {
			this.parameters = new ArrayList<>();
		}else {
			this.parameters = parameters;
		}
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
	
	public void addParameter(ParameterModel parameter) {
		this.parameters.add(parameter);
	}
	
	public ParameterModel parameterAt(int index) {
		assert index >= 0 && index < parameters.size();
		return parameters.get(index);		
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
//		if(parameters.size() != mm.parameters.size()) {
//			return false;
//		}
//		for(int i = 0; i < parameters.size(); i++) {
//			if(!parameters.get(i).equals(mm.parameters.get(i))) {
//				return false;
//			}
//		}
		return name.equals(mm.name) &&
//			returnType.equals(mm.returnType) &&
			className.equals(mm.className);
				
	}
	
	public static  MethodModel parseMethodModel(CtMethod m) {
		String name = m.getName();
		String className = m.getDeclaringClass().getName();
		List<ParameterModel> parameters = new ArrayList<ParameterModel>();
		ParameterModel newParameter = new ParameterModel();
		try {
			for(CtClass parameter: m.getParameterTypes()) {
				newParameter.setType(parameter.getName());
				parameters.add(newParameter);
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String returnType = "any";
		try {
			returnType = m.getReturnType().toString();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new MethodModel(name, className, parameters, returnType);
	}
	
	public static MethodModel parseMethodModel(MethodCall mc) {
		MethodModel method = new MethodModel("unknown", "unknow", null, "any");		
		try {
			method = parseMethodModel(mc.getMethod());
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return method;
	}
	
	@Override
	public String toString() {
		String result = "";
		result += name;
		result += "(";
		String separator = "";
		for(ParameterModel parameter: parameters) {
			result += separator + parameter.getType() + ":" + parameter.getValue();
			separator = ", ";
		}
		result += ")";
		return result;
				
	}
	
	public String toDebug() {
		String result = "";
		result += name;
		result += "(";
		String separator = "";
		for(ParameterModel parameter: parameters) {
			result += separator + parameter.getType() + ":" + parameter.getValue();
			separator = ", ";
		}
		result += ")";
		return result;
	}

	public void setParameters(List<ParameterModel> parameters) {
		if(parameters != null) {
			for(ParameterModel parameter: parameters) {
				this.parameters.add(parameter);
			}
		}
	}
	
}
