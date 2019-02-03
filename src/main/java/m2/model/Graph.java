package m2.model;

import java.util.ArrayList;
import java.util.List;

public class Graph<T> {
	
	private String name;
	private List<Edge<T>> edges;

	public Graph() {
		super();
		edges = new ArrayList<>();
		name = "untitled";
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addEdge(Edge<T> edge) {
		this.edges.add(edge);
	}
	
	
	public String toDot() {
		String result = "digraph ";
		result += this.name;
		result += "{\n";
		for(Edge edge : edges) {
			result +=  '"' + edge.getFirstNode().getValue().toString() + '"';
			if(edge.isOriented()) {
				result += " -> ";
			}else {
				result += " -- ";
			}
			result += '"' + edge.getSecondNode().getValue().toString() + '"';
			result += ";\n";
		}
		result += "\n}";
		return result;
	}
}
