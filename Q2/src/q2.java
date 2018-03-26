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
		
		int[] adjList = q2.Conflicting.getAdjList();
		for (int i = 0; i < adjList.length; i++) {
			if (adjList[i] == this.id) {
				if (i%2 == 0) {
					if (color == q2.NodesList.get(adjList[i+1]).Color) {
						color++;
						i = -1;
					}
				} else {
					if (color == q2.NodesList.get(adjList[i-1]).Color) {
						color++;
						i = -1;
					}
				}
			}
		}
//		if (this.id == adjList[0]) {
//			System.out.println(adjList);
//		}
		this.Color = color;
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
	private int[] AdjList;
	private ArrayList<Node> Nodes = new ArrayList<Node>();
	
	public ArrayList<Node> GetNodes(){
		return Nodes;
	}
	
	public ArrayList<Adjacency> GetEdge(){
		return Adjacencies;
	}
	
	public void AddAdjacency(Node a, Node b, int index) {
		AdjList[index*2] = a.id;
		AdjList[(index*2)+1] = b.id;
	}
	
	public void createAdjList(int numOfEdge) {
		AdjList = new int[numOfEdge*2];
	}
	
	public int[] getAdjList() {
		return AdjList;
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
		//int startNode = (Nodes.size()/allthread)  *ID;
		int[] adjList = q2.Conflicting.getAdjList();
		int startIndex = (Nodes.size()/allthread)  *ID;
		
		int i = 0;
		//System.out.println("startindex: "+ startIndex);
		
		for (int j = 0; j < adjList.length/allthread; j+=2) {
			if (q2.NodesList.get(adjList[startIndex]).Color == q2.NodesList.get(adjList[startIndex+1]).Color) {
				if (adjList[startIndex] > adjList[startIndex+1]) {
					while (!conflictArray.compareAndSet(arrayIndex.getAndIncrement(), -1, adjList[startIndex]));
				} else {
					while (!conflictArray.compareAndSet(arrayIndex.getAndIncrement(), -1, adjList[startIndex+1]));
				}
			}
			startIndex+=2;
		}
		//System.out.println("startindex: "+ startIndex);
		
		
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
	public static Graph Conflicting;
	public static ArrayList<Node> NodesList;
	
	
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
		NodesList = Nodes;
		System.out.println("node time: "+ (System.currentTimeMillis() - time));
		//Node test = Nodes.get(0);
		
		//ArrayList<Node> Nodes = graph.GetNodes();
		
		Conflicting = new Graph();
		Conflicting.AddNodes(Nodes);
		Conflicting.createAdjList(e);
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
			//Graph.AddEage(Node, Nodes.get(randomNodeId));
			e--;
			Conflicting.AddAdjacency(Node, Nodes.get(randomNodeId), e);
		}
		System.out.println("Edge time: " + (System.currentTimeMillis()-time));
		ArrayList<Thread> conflictThread = new ArrayList<Thread>();
		AtomicIntegerArray conflictArray = new AtomicIntegerArray(n);
		ArrayList<Node> ConflictingNodes = new ArrayList<Node>(Conflicting.GetNodes());
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
			//hard code a conflict
			
			
			try {
				//System.out.println(conflictThread.size());
				for (Thread thread : conflictThread) {
					thread.join();
					
				}
				
				assigntime = System.currentTimeMillis() - assigntime;
				System.out.println("assigntime : "+assigntime);
				
				
//				int[] adjList = Conflicting.getAdjList();
//				System.out.println(adjList[0]+":"+adjList[1]);
//				NodesList.get(adjList[0]).Color = 1;
//				NodesList.get(adjList[1]).Color = 1;
				
				assigntime = System.currentTimeMillis();
				for (int i = 0; i<t ; i++) {
					Thread detectconflit = new DetectConflicts(ConflictingNodes, conflictArray , arrayIndex, i, t);
					detectconflit.start();
					detectconflit.join();
				}
				
				
				
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
					System.out.println("number of conflict: "+i);
					break;
				}
				conflictArray.set(i, -1);
				newconflict.add(Nodes.get(nodeid));
				//System.out.println("nodeid: "+ nodeid);
			}
			
			arrayIndex.set(0);//reset arrayIndex
			
			if (ConflictingNodes.size()<n) {
				System.out.println(i+" "+n);
			}
			ConflictingNodes = newconflict;
			
			assigntime = System.currentTimeMillis() - assigntime;
			System.out.println("detect time : "+assigntime);
//			for (Node node : newconflict) {
//				System.out.println("conflict id: "+node.id);
//			}
			
		}
		
		Time = System.currentTimeMillis() - Time;
		System.out.println("Total: "+Time);
//		int[] adjList = Conflicting.getAdjList();
//		
//		for (int i = 0; i < adjList.length; i+=2) {
//			System.out.println("nodeid: "+NodesList.get(adjList[i]).id+" color: "+NodesList.get(adjList[i]).Color+
//							" nodeid: "+NodesList.get(adjList[i+1]).id+" color: "+NodesList.get(adjList[i+1]).Color);
//		}
		
		//Graph NewConflicts = new Graph();
		
		//graph.PrintGraph();
		
		
	}
}
