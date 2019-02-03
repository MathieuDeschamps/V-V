package m2.model;

public class Edge<T> {

	private Node<T> firstNode;
	private Node<T> secondNode;
	private boolean isOriented;
	public Edge(Node<T> firstNode, Node<T> secondNode, boolean isOriented) {
		super();
		this.firstNode = firstNode;
		this.secondNode = secondNode;
		this.isOriented = isOriented;
	}
	public Node<T> getFirstNode() {
		return firstNode;
	}
	public void setFirstNode(Node<T> firstNode) {
		this.firstNode = firstNode;
	}
	public Node<T> getSecondNode() {
		return secondNode;
	}
	public void setSecondNode(Node<T> secondNode) {
		this.secondNode = secondNode;
	}
	public boolean isOriented() {
		return isOriented;
	}
	public void setOriented(boolean isOriented) {
		this.isOriented = isOriented;
	}
	
	
	
}
