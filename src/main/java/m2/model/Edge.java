package m2.model;

public class Edge {

	private Node firstNode;
	private Node secondNode;
	private boolean isOriented;
	public Edge(Node firstNode, Node secondNode, boolean isOriented) {
		super();
		this.firstNode = firstNode;
		this.secondNode = secondNode;
		this.isOriented = isOriented;
	}
	public Node getFirstNode() {
		return firstNode;
	}
	public void setFirstNode(Node firstNode) {
		this.firstNode = firstNode;
	}
	public Node getSecondNode() {
		return secondNode;
	}
	public void setSecondNode(Node secondNode) {
		this.secondNode = secondNode;
	}
	public boolean isOriented() {
		return isOriented;
	}
	public void setOriented(boolean isOriented) {
		this.isOriented = isOriented;
	}
	
	
	
}
