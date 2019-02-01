package m2.model;

import java.util.ArrayList;
import java.util.List;

public class Graph {
	
	private String name;
	private List<Edge> edges;

	public Graph() {
		super();
		edges = new ArrayList<>();
		name = "untitled";
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addEdge(Edge edge) {
		this.edges.add(edge);
	}
	
	
	public String toDot() {
		String result = "digraph ";
		result += this.name;
		result += "{\n";
		for(Edge edge : edges) {
			result +=  '"' + edge.getFirstNode().getName() + '"';
			if(edge.isOriented()) {
				result += " -> ";
			}else {
				result += " -- ";
			}
			result += '"' + edge.getSecondNode().getName() + '"';
			result += ";\n";
		}
		result += "\n}";
		return result;
	}
}
