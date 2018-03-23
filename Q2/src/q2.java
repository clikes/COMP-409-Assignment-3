import java.util.ArrayList;
import java.util.concurrent.atomic.*;


class Node{
	//AtomicInteger Color = new AtomicInteger(0);
	int Color = 0;
	public int id;
	public Node(int id) {
		this.id = id;
	}
	ArrayList<Node> ChildNode = new ArrayList<Node>();
	
	public void AddChildNode(Node V) {
		ChildNode.add(V);
	}
	
	public void ColorNode() {
		int color = 1 ;
		for (int i = 0; i < ChildNode.size(); i++) {
			Node Node = ChildNode.get(i);
			if (color == Node.Color) {
				color++;
				i = -1;
			}
		}
		
		//this.Color.set(color);
		this.Color = color;
//		for (Node node : ChildNode) {
//			if (this.Color == node.Color) {
//				System.err.println(ChildNode.size()+"!!");
//			}
//		}
		
	}
	
	public ArrayList<Node> GetChildNodes() {
		return ChildNode;
	}
	
	public void PringChildNode() {
		for (Node Node : ChildNode) {
			
			System.out.println(Node.id+" "+Node.Color);
		}
		
	}
}

class Adjacency{
	public final Node first;
    public final Node second;
     
    public Adjacency(Node a, Node b) {
        this.first = a;
        this.second = b;
    }
}

class Graph{
	private ArrayList<Adjacency> Adjacencies = new ArrayList<Adjacency>();
	private ArrayList<Node> Nodes = new ArrayList<Node>();
	
	public ArrayList<Node> GetNodes(){
		return Nodes;
	}
	
	public ArrayList<Adjacency> GetEdge(){
		return Adjacencies;
	}
	
	public void AddNode(Node node) {
		Nodes.add(node);
	}
	
	public void AddNodes(ArrayList<Node> Nodes) {
		this.Nodes.clear();
		this.Nodes.addAll(Nodes);
	}
	
	public void AddAdjacency(Adjacency V) {
		Adjacencies.add(V);
	}
	
	public static void AddEage(Node V,Node V2) {
		V.AddChildNode(V2);
		V2.AddChildNode(V);
	}

	
	public void RemoveAdjacency(Adjacency adjacency) {
		Adjacencies.remove(adjacency);
	}
	
	public ArrayList<Adjacency> GetAdjacencies() {
		return Adjacencies;
	}
	
	public boolean IsEmpty() {
		return Nodes.isEmpty();
	}
	
//	public void PrintGraph() {
//		for (int i = 0; i < Adjacencies.size(); i++) {
//			System.out.print("Node "+i+": ");
//			Adjacencyies.get(i).PringChildNode();
//			System.out.println("");
//		}
//	}
}


class Assign extends Thread{
	//private Graph Conflicting;
	ArrayList<Node> Nodes;
	int ID;
	int allthread;
	public Assign(ArrayList<Node> Nodes, int i, int t) {
		//this.Conflicting = graph;
		this.Nodes = Nodes;
		ID = i;
		allthread = t;
	}
	@Override
	public void run() {
		//ArrayList<Node> Nodes = Conflicting.GetNodes();
		int startNode = (Nodes.size()/allthread)  *ID;
		
		//System.out.println(ID +" "+ allthread+" "+Nodes.size());
		if (ID == 0 && Nodes.size() <= allthread) {
			for (Node node : Nodes) {
				node.ColorNode();
			}
			System.out.println(1);
			return;
		}
		int i = 0;
		for ( i= 0; i < Nodes.size()/allthread; i++) {
//			if (Nodes.size() <900) {
//				
//				System.out.print("node"+Nodes.get(startNode).id+": "+Nodes.get(startNode).Color+" ");
//				
//				
//			}
			Nodes.get(startNode).ColorNode();
//			if (Nodes.size() <900) {
//				System.out.println(Nodes.get(startNode).Color);
//				
//				Nodes.get(startNode).PringChildNode();
//			}
			startNode++;
		}
		//System.out.println(i);
	}
}


class DetectConflicts extends Thread{
	Graph Conflicting;
	Graph NewConflicts;
	
	ArrayList<Node> Nodes;
	ArrayList<Node> ConflictsNodes;
	AtomicIntegerArray conflictArray;
	
	public DetectConflicts(Graph graph, AtomicIntegerArray conflictArray) {
		this.Conflicting = graph;
		this.conflictArray = conflictArray;
	}
//	public DetectConflicts(Graph Conflicting, Graph NewConflicts) {
//		this.Conflicting = Conflicting;
//		this.NewConflicts = NewConflicts;
//	}
	
	@Override
	public void run() {
		//AtomicIntegerArray array = new AtomicIntegerArray(100);
		 //= new AtomicIntegerArray(100);
		ArrayList<Adjacency> edges = Conflicting.GetEdge();
		for (Adjacency edge : edges) {
			if (edge.first.Color == edge.second.Color) {
				if (edge.first.id>edge.second.id) {
					int old = 0;
					conflictArray.compareAndSet(edge.first.id, old, 1);
				}
				else {
					int old = 0;
					conflictArray.compareAndSet(edge.second.id, old, 1);
				}
			}
		}
//		try {
//			sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		for (Node node : Nodes) {
//			for (Node childnode : node.ChildNode) {
//				if (node.Color == childnode.Color && node.id > childnode.id) {
//					//ConflictsNodes.add(node);
//					System.out.println(node.id);
//					int old = conflictArray.get(node.id);
//					conflictArray.compareAndSet(node.id, old, 1);
//					break;
//				}
//			}
//		}
		
		
		
		
		
//		ArrayList<Node> Nodes = Conflicting.GetNodes();
//		for (Node Node : Nodes) {
//			ArrayList<Node> ChildNodes = Node.GetChildNodes();
//			for (Node childnode : ChildNodes) {
////				if (Node.Color == childnode.Color) {
////					NewConflicts.AddNode(Node);
////					
////				}
////				
//				//atomic
//				do {
//					if (Node.Color == childnode.Color) {
//						NewConflicts.AddNode(Node);
//					}
//				} while (Node.Color != childnode.Color);
//				break;
//				
//				
//			}
//		}
	}
}


class ColorThread extends Thread{
//	Graph Conflicting;
//	Graph NewConflicts;
//	int id, end;
//	
//	ArrayList<Node> Nodes;
//	ArrayList<Node> ConflictsNodes;
	Thread assign;
	Thread detectconflit; 
//	
//	public ColorThread(Graph Conflicting, Graph NewConflicts, int id, int end) {
//		this.Conflicting = Conflicting;
//		this.NewConflicts = NewConflicts;
//		this.id = id;
//		this.end = end;
//	}
	
	public ColorThread(Thread assign, Thread detectconflit) {
		this.assign = assign;
		this.detectconflit = detectconflit;
	}
	@Override
	public void run() {
		assign.run();
		try {
			assign.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		detectconflit.start();
	}
}


public class q2 {
	
	public static void main(String[] args) {
		//Graph graph = new Graph();
		
		
		int n = Integer.parseInt(args[0]);
		int e = Integer.parseInt(args[1]);
		int t = Integer.parseInt(args[2]);
		System.out.println(n+" "+ e+" "+t);
		ArrayList<Node> Nodes = new ArrayList<Node>();
		for (int i = 0; i < n; i++) {
			Nodes.add(new Node(i));
		}
		//Node test = Nodes.get(0);
		
		//ArrayList<Node> Nodes = graph.GetNodes();
		
		Graph Conflicting = new Graph();
		Conflicting.AddNodes(Nodes);
		while (e>0) {
			for (Node Node : Nodes) {
				int randomNodeId;
				do {
					randomNodeId = (int)(Math.random()*Nodes.size());
				}while (randomNodeId == Node.id);
				Conflicting.AddAdjacency(new Adjacency(Node, Nodes.get(randomNodeId)));
				Graph.AddEage(Node, Nodes.get(randomNodeId));
				e--;
				if (e==0) break;
			}
		}
		
		ArrayList<Thread> conflictThread = new ArrayList<Thread>();
		AtomicIntegerArray conflictArray = new AtomicIntegerArray(n);
		ArrayList<Node> ConflictingNodes = new ArrayList<Node>();
		ConflictingNodes.addAll(Conflicting.GetNodes());
		
		for (int i = 0; i < n; i++) {
			conflictArray.set(i, 0);
		}
		long Time = System.currentTimeMillis();
		while (!ConflictingNodes.isEmpty()){
			
			for (int i = 0; i<t ; i++) {
				
				Thread assign = new Assign(ConflictingNodes,i,t);
				assign.start();
				conflictThread.add(assign);
			}
			//System.out.println(1);
			try {
				for (Thread thread : conflictThread) {
					thread.join();
				}
				Thread detectconflit = new DetectConflicts(Conflicting, conflictArray);
				detectconflit.start();
				detectconflit.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			conflictThread.clear();
			ConflictingNodes.clear();
			int i = 0;
			//System.out.println(Nodes.size());
			try {
				for ( i= 0; i < n; i++) {
					if (conflictArray.compareAndSet(i, 1, 0)) {
						//System.out.println(1);
						ConflictingNodes.add(Nodes.get(i));
					}
				}
				//Thread.currentThread().sleep(1000);
			} catch (IndexOutOfBoundsException e2) {
				e2.printStackTrace();
				System.err.println(i);
			} 
			
			//System.out.println(ConflictingNodes.size());
			
		}
		Time = System.currentTimeMillis() - Time;
		System.out.println(Time);
		
		
		//Graph NewConflicts = new Graph();
		
		//graph.PrintGraph();
		
		
	}
}
