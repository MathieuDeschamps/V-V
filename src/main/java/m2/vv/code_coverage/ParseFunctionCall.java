package m2.vv.code_coverage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import m2.model.Edge;
import m2.model.Graph;
import m2.model.MethodModel;
import m2.model.Node;

public class ParseFunctionCall {
	
	private List<Edge<MethodModel>> allEdges;
	private String trace;
	public ParseFunctionCall(List<Edge<MethodModel>> allEdges, String trace) {
		if(allEdges == null) {
			this.allEdges = new ArrayList<>();
		}else {
			this.allEdges = allEdges;
		}
		if(trace == null) {
			this.trace = "";
		}else {
			this.trace = trace;			
		}
	}
	
	public List<Graph<MethodModel>> process(){
		return parseTrace();
	}
	
	private List<Graph<MethodModel>> parseTrace() {
		List<Graph<MethodModel>> graphedTrace = new ArrayList<>();
		String[] testSections = trace.split("\\" + TraceFunctionCall.SEP_SECTION);
		Graph<MethodModel> newGraph;
		Stack<MethodModel> functionCallStack = new Stack<>();
		for (String testTrace : testSections) {
			String[] methodSections = testTrace.split(""+TraceFunctionCall.SEP_METHOD_CALL);
			newGraph = new Graph<>();
			for(String methodSection : methodSections) {
				if(methodSection.equals("")){
					// Do nothing
				}else if (methodSection.charAt(0) == TraceFunctionCall.PRE_TEST) {
					newGraph = parseTestMethod(newGraph, functionCallStack, methodSection);
					// if parseTestMethod not failed add the new graph
					if(!functionCallStack.isEmpty()) {
						graphedTrace.add(newGraph);						
					}
				} else if (methodSection.charAt(0) == TraceFunctionCall.PRE_CLASS) {
					Edge<MethodModel> newEdge = parseClassMethod(functionCallStack, methodSection);
					if(newEdge != null) {
						newGraph.addEdge(newEdge);
					}
				}
			}
			functionCallStack.clear();
		}
		return graphedTrace;
	}

	private Graph<MethodModel> parseTestMethod(Graph<MethodModel> graph, Stack<MethodModel> functionCallStack, String methodLine){
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
	
	private Edge<MethodModel> parseClassMethod(Stack<MethodModel> functionCallStack, String methodLine) {
		int beginClassName = 1;
		int endClassName = methodLine.indexOf(TraceFunctionCall.SEP_CLASS_METHOD);
		int beginMethodName = methodLine.indexOf(TraceFunctionCall.SEP_CLASS_METHOD) + 1;

		if (beginClassName != -1 && endClassName != -1 && beginMethodName != -1) {
			String className = methodLine.substring(beginClassName, endClassName);
			String methodName = methodLine.substring(beginMethodName);

			MethodModel classMethod = new MethodModel(methodName, className, null, null);
			
			return matchEdge(functionCallStack, classMethod);
		}else {
			return null;					
		}	
	}
	
	private Edge<MethodModel> matchEdge(Stack<MethodModel> functionCallStack, MethodModel methodCalled ){
		// callingMethod, which call the method called
		boolean isMatching =false;
		MethodModel callingMethod;
		Edge<MethodModel> edge = null;
		Node<MethodModel> firstNode;
		Node<MethodModel> secondNode;
		while(!functionCallStack.isEmpty() && !isMatching) {
			callingMethod = functionCallStack.pop();
			for(Edge<MethodModel> aEdge: allEdges) {
				MethodModel firstMethod = aEdge.getFirstNode().getValue();
				MethodModel secondMethod = aEdge.getSecondNode().getValue();	
				
				if(callingMethod.equals(firstMethod) && methodCalled.equals(secondMethod)) {
					isMatching = true;
					firstNode = new Node<>(callingMethod);
					secondNode = new Node<>(methodCalled);
					edge = new Edge<>(firstNode, secondNode, true);
					functionCallStack.add(callingMethod);
					functionCallStack.add(methodCalled);
				}
			}
		}				
		return edge;
	}
	
}
