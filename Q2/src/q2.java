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
			//System.out.println(1);
			return;
		}
		int i = 0;
		for ( i= 0; i < Nodes.size()/allthread; i++) {
			Nodes.get(startNode).ColorNode();
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
	ArrayList<Adjacency> edges;
	AtomicInteger arrayIndex;
	
	int ID;
	int allthread;
	
	public DetectConflicts(ArrayList<Node> nodes, AtomicIntegerArray conflictArray, AtomicInteger arrayIndex, int i, int t) {
		Nodes = nodes;
		this.conflictArray = conflictArray;
		this.arrayIndex = arrayIndex;
		ID = i;
		allthread = t;
	}
//	public DetectConflicts(Graph Conflicting, Graph NewConflicts) {
//		this.Conflicting = Conflicting;
//		this.NewConflicts = NewConflicts;
//	}
	
	@Override
	public void run() {
		//AtomicIntegerArray array = new AtomicIntegerArray(100);
		 //= new AtomicIntegerArray(100);
//		edges = Conflicting.GetEdge();
//		for (Adjacency edge : edges) {
//			if (edge.first.Color == edge.second.Color) {
//				if (edge.first.id>edge.second.id) {
//					int old = 0;
//					conflictArray.compareAndSet(edge.first.id, old, 1);
//				}
//				else {
//					int old = 0;
//					conflictArray.compareAndSet(edge.second.id, old, 1);
//				}
//			}
//		}
//		try {
//			sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//int conflict = 0;
		for (Node node : Nodes) {//写一个while循环寻找新的-1值 使用CAS 然后
			for (Node childnode : node.ChildNode) {
				if (node.Color == childnode.Color && node.id > childnode.id) {
					while (!conflictArray.compareAndSet(arrayIndex.getAndIncrement(), -1, node.id));
					//conflictArray.set(conflict, node.id);
					//conflict++;
					break;
				}
			}
		}
		
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
		long time = System.currentTimeMillis();
		ArrayList<Node> Nodes = new ArrayList<Node>();
		for (int i = 0; i < n; i++) {
			Nodes.add(new Node(i));
		}
		System.out.println("node time: "+ (System.currentTimeMillis() - time));
		//Node test = Nodes.get(0);
		
		//ArrayList<Node> Nodes = graph.GetNodes();
		
		Graph Conflicting = new Graph();
		Conflicting.AddNodes(Nodes);
		time = System.currentTimeMillis();
		while (e!=0) {
			int randomNode1 = (int)(Math.random()*Nodes.size());
			Node Node = Nodes.get(randomNode1);
			//for (Node Node : Nodes) {
			int randomNodeId;
			do {
				randomNodeId = (int)(Math.random()*Nodes.size());
			}while (randomNodeId == randomNode1);
//			Conflicting.AddAdjacency(new Adjacency(Node, Nodes.get(randomNodeId)));
//			Conflicting.AddAdjacency(new Adjacency(Nodes.get(randomNodeId), Node));
			Graph.AddEage(Node, Nodes.get(randomNodeId));
			e--;
		}
		System.out.println("Edge time: " + (System.currentTimeMillis()-time));
		ArrayList<Thread> conflictThread = new ArrayList<Thread>();
		AtomicIntegerArray conflictArray = new AtomicIntegerArray(n);
		ArrayList<Node> ConflictingNodes = Conflicting.GetNodes();
		//ArrayList<Adjacency> edges = Conflicting.GetAdjacencies();
		//ConflictingNodes.addAll();
		AtomicInteger arrayIndex  = new AtomicInteger(0);
		for (int i = 0; i < n; i++) {
			conflictArray.set(i, -1);
		}
		long Time = System.currentTimeMillis();
		
		
		while (!ConflictingNodes.isEmpty()){
			long assigntime = System.currentTimeMillis();
			for (int i = 0; i<t ; i++) {
				
				Thread assign = new Assign(ConflictingNodes,i,t);
				assign.start();
				conflictThread.add(assign);
			}
			//System.out.println(1);
			
			
			try {
				//System.out.println(conflictThread.size());
				for (Thread thread : conflictThread) {
					thread.join();
					
				}
				assigntime = System.currentTimeMillis() - assigntime;
				System.out.println("assigntime : "+assigntime);
				assigntime = System.currentTimeMillis();
				int i = 0;
				Thread detectconflit = new DetectConflicts(ConflictingNodes, conflictArray , arrayIndex, i, t);
				detectconflit.start();
				detectconflit.join();
				
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			conflictThread.clear();
			//ConflictingNodes.clear();
			ArrayList<Node> newconflict = new ArrayList<Node>();
			//ArrayList<Adjacency> conflictedge = new ArrayList<Adjacency>();
			int i = 0;
			//System.out.println(Nodes.size());
			for ( i= 0; i < n; i++) {
				int nodeid = conflictArray.get(i);
				if (nodeid == -1) {
					
					break;
				}
				conflictArray.set(i, -1);
				newconflict.add(Nodes.get(nodeid));
//				if (conflictArray.compareAndSet(i, 1, 0)) {
//					//System.out.println(1);
//					newconflict.add(Nodes.get(i));
//				}
			}
			if (ConflictingNodes.size()<n) {
				System.out.println(i+" "+n);
			}
			ConflictingNodes = newconflict;
			assigntime = System.currentTimeMillis() - assigntime;
			System.out.println("detect time : "+assigntime);
			//System.out.println(ConflictingNodes.size());
			
		}
		
		Time = System.currentTimeMillis() - Time;
		System.out.println("Total: "+Time);
		
		
		//Graph NewConflicts = new Graph();
		
		//graph.PrintGraph();
		
		
	}
}
