package m2.code_coverage;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import m2.model.Edge;
import m2.model.Graph;
import m2.model.MethodModel;
import m2.model.Node;
import m2.model.ParameterModel;

public class ParseFunctionCall {

	private List<Edge<MethodModel>> allEdges;
	private String trace;

	public ParseFunctionCall(List<Edge<MethodModel>> allEdges, String trace) {
		if (allEdges == null) {
			this.allEdges = new ArrayList<>();
		} else {
			this.allEdges = allEdges;
		}
		if (trace == null) {
			this.trace = "";
		} else {
			this.trace = trace;
		}
	}

	/**
	 * Parse the trace
	 * 
	 * @return list of graph, one for each test method
	 */
	public List<Graph<MethodModel>> process() {
		List<Graph<MethodModel>> graphedTrace = new ArrayList<>();
		String[] testSections = trace.split("\\" + TraceFunctionCall.SEP_SECTION);
		Graph<MethodModel> newGraph;
		Stack<MethodModel> functionCallStack = new Stack<>();
		for (String testTrace : testSections) {
			String[] methodSections = testTrace.split("" + TraceFunctionCall.END_METHOD_CALL);
			newGraph = new Graph<>();
			for (String methodSection : methodSections) {
				
				if (methodSection.equals("")) {
					// Do nothing
				} else if (methodSection.charAt(0) == TraceFunctionCall.PRE_TEST) {
					newGraph = parseTestMethod(newGraph, functionCallStack, methodSection);
					// if parseTestMethod not failed add the new graph
					if (!functionCallStack.isEmpty()) {
						graphedTrace.add(newGraph);
					}
				} else if (methodSection.charAt(0) == TraceFunctionCall.PRE_CLASS) {
					Edge<MethodModel> newEdge = parseClassMethod(functionCallStack, methodSection);
					if (newEdge != null) {
						newGraph.addEdge(newEdge);
					}
				}else if(methodSection.charAt(0) == TraceFunctionCall.PRE_PARAM){
					MethodModel lastMethod = functionCallStack.pop();
					lastMethod.setParameters(parseParam(methodSection));
					functionCallStack.push(lastMethod);
				} else if (methodSection.charAt(0) == TraceFunctionCall.PRE_EXIT_METHOD) {
					if (!functionCallStack.isEmpty()) {
						functionCallStack.pop();
					}
				}
			}
			functionCallStack.clear();
		}
		return graphedTrace;
	}

	/**
	 * Parse the methodLine to retrieve a graph based on the methodLine data
	 * 
	 * @param graph
	 *            which will contains the methodLine data parsed
	 * @param functionCallStack
	 *            which contains the stack of all the called method
	 * @param methodLine
	 *            the string to parse
	 * @return graph with the parse data from methodLine
	 */
	private Graph<MethodModel> parseTestMethod(Graph<MethodModel> graph, Stack<MethodModel> functionCallStack,
			String methodLine) {
		int beginClassName = 1;
		int endClassName = methodLine.indexOf(TraceFunctionCall.SEP_CLASS_METHOD);
		int beginMethodName = methodLine.indexOf(TraceFunctionCall.SEP_CLASS_METHOD) + 1;

		if (beginClassName != -1 && endClassName != -1 && beginMethodName != -1) {
			String className = methodLine.substring(beginClassName, endClassName);
			String methodName = methodLine.substring(beginMethodName);

			functionCallStack.empty();
			functionCallStack.add(new MethodModel(methodName, className, null, null));

			graph.setName(methodName);
		}
		return graph;
	}

	/**
	 * Parse the methodLine to retrieve a edge based on the functionCallStack
	 * 
	 * @param functionCallStack
	 *            which contains the stack of all the called method
	 * @param methodLine
	 *            the string to parse
	 * @return edge with the parse data from methodLine
	 */
	private Edge<MethodModel> parseClassMethod(Stack<MethodModel> functionCallStack, String methodLine) {
		int beginClassName = 1;
		int endClassName = methodLine.indexOf(TraceFunctionCall.SEP_CLASS_METHOD);
		int beginMethodName = methodLine.indexOf(TraceFunctionCall.SEP_CLASS_METHOD) + 1;
		if (beginClassName != -1 && endClassName != -1 && beginMethodName != -1) {
			String className = methodLine.substring(beginClassName, endClassName);
			String methodName = methodLine.substring(beginMethodName);

			MethodModel classMethod = new MethodModel(methodName, className, null, null);
			return matchEdge(functionCallStack, classMethod);
		} else {
			return null;
		}
	}
	
	/**
	 * Parse the methodLine to retrieve the list of parameters from methodLine
	 * 
	 * @param methodLine
	 *            the string to parse
	 * @return the list of parameters with the type and value form methodLine
	 */
	private List<ParameterModel> parseParam(String methodLine) {
		
		List<ParameterModel> parameters = new ArrayList<>();
		int beginParam = methodLine.indexOf(TraceFunctionCall.PRE_PARAM) + 1;
		int endParam = methodLine.indexOf(TraceFunctionCall.END_PARAM);		
		if(beginParam != -1 && endParam != -1) {
			methodLine = methodLine.substring(beginParam, endParam);
			if(!methodLine.equals("")) {
				ParameterModel parameter;
				String type;
				String value;
				String[] parameterSections = methodLine.split("" + TraceFunctionCall.SEP_PARAM);
				
				for(String parameterSection: parameterSections){
					parameter= new ParameterModel();
					int beginType = 0;
					int endType = parameterSection.indexOf(TraceFunctionCall.SEP_TYPE_VALUE);
					int beginValue = parameterSection.indexOf(TraceFunctionCall.SEP_TYPE_VALUE) + 1;
					if(beginType != -1 && endType != -1 && beginValue != -1) {
						type = parameterSection.substring(beginType, endType);
						value = parameterSection.substring(beginValue);
						if(!value.equals(""));{
							parameter.setValue(value);
						}
						if(!type.equals("")){
							parameter.setType(type);
						}
						parameters.add(parameter);
					}
				}
			}
		}
		return parameters;
	}

	/**
	 * Find the edge which match with functionCall stack and the methodCalled in
	 * allEdge
	 * 
	 * @param functionCallStack
	 *            which contains the stack of all the called method
	 * @param methodCalled
	 * @return the edge which matching otherwise null
	 */
	private Edge<MethodModel> matchEdge(Stack<MethodModel> functionCallStack, MethodModel methodCalled) {
		// callingMethod, which call the method called
		boolean isMatching = false;
		MethodModel callingMethod = null;
		Edge<MethodModel> edge = null;
		Edge<MethodModel> aEdge = null;
		Node<MethodModel> firstNode;
		Node<MethodModel> secondNode;
		if (!functionCallStack.isEmpty()) {
			callingMethod = functionCallStack.peek();
		}
		int i = 0;
		while(i < allEdges.size() && !isMatching) {
			aEdge = allEdges.get(i);
			MethodModel firstMethod = aEdge.getFirstNode().getValue();
			MethodModel secondMethod = aEdge.getSecondNode().getValue();
			
			if (callingMethod.equals(firstMethod) && methodCalled.equals(secondMethod)) {
				firstNode = new Node<>(callingMethod);
				secondNode = new Node<>(methodCalled);
				edge = new Edge<>(firstNode, secondNode, true);
				functionCallStack.add(methodCalled);
				isMatching = true;
			}
			i++;
		}

		return edge;
	}

}
