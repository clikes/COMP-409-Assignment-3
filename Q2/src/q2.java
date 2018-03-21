import java.util.ArrayList;

class Vertex{
	int Color = 0;
	public int id;
	public Vertex(int id) {
		this.id = id;
	}
	ArrayList<Vertex> ChildNode = new ArrayList<Vertex>();
	
	public void AddChildNode(Vertex V) {
		ChildNode.add(V);
	}
	public void PringChildNode() {
		for (Vertex vertex : ChildNode) {
			
			System.out.print(vertex.id+" ");
		}
		
	}
}

class Graph{
	private ArrayList<Vertex> Vertexs = new ArrayList<Vertex>();
	
	public void AddVertex(Vertex V) {
		Vertexs.add(V);
	}
	
	public static void AddEage(Vertex V,Vertex V2) {
		V.AddChildNode(V2);
		V2.AddChildNode(V);
	}
	
	
	public ArrayList<Vertex> GetVertexs() {
		return Vertexs;
	}
	
	public void PrintGraph() {
		for (int i = 0; i < Vertexs.size(); i++) {
			System.out.print("Vertex "+i+": ");
			Vertexs.get(i).PringChildNode();
			System.out.println("");
		}
	}
}


public class q2 {
	static Graph graph = new Graph();
	
	
	
	public static void main(String[] args) {
		int n = Integer.parseInt(args[0]);
		int e = Integer.parseInt(args[1]);
		int t = Integer.parseInt(args[2]);
		System.out.println(n);
		for (int i = 0; i < n; i++) {
			graph.AddVertex(new Vertex(i));
		}
		ArrayList<Vertex> vertexs = graph.GetVertexs();
		
		while (e>0) {
			for (Vertex vertex : vertexs) {
				int randomVertexId;
				do {
					randomVertexId = (int)(Math.random()*vertexs.size());
				}while (randomVertexId == vertex.id);
				
				Graph.AddEage(vertex, vertexs.get(randomVertexId));
				e--;
				if (e==0) break;
			}
		}
		
		graph.PrintGraph();
		
		
	}
}
