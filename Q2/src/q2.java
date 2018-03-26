import java.util.ArrayList;
import java.util.concurrent.atomic.*;



class Node{
	int Color = 0;
	public int id;
	public Node(int id) {
		this.id = id;
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
		this.Color = color;
	}
	
	
}

class Graph{
	private int[] AdjList;
	private ArrayList<Node> Nodes = new ArrayList<Node>();
	
	public ArrayList<Node> GetNodes(){
		return Nodes;
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
	
	
	public void AddNodes(ArrayList<Node> Nodes) {
		this.Nodes.clear();
		this.Nodes.addAll(Nodes);
	}
	

}


class Assign extends Thread{
	//private Graph Conflicting;
	ArrayList<Node> Nodes;
	int ID;	
	int allthread;
	//i and t are use for multi thread identify which part of list it should run
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
	
//	ArrayList<Node> Nodes;
//	ArrayList<Node> ConflictsNodes;
	
	AtomicIntegerArray conflictArray;
	AtomicInteger arrayIndex;
	
	int ID;
	int allthread;
	
	public DetectConflicts( AtomicIntegerArray conflictArray, AtomicInteger arrayIndex, int i, int t) {
		//Nodes = nodes;
		this.conflictArray = conflictArray;
		this.arrayIndex = arrayIndex;
		ID = i;			//same as assign
		allthread = t;
	}
	@Override
	public void run() {
		int[] adjList = q2.Conflicting.getAdjList();
		int startIndex = (adjList.length/allthread)  *ID;
		
		
		for (int j = 0; j < adjList.length/allthread; j+=2) {
			if (q2.NodesList.get(adjList[startIndex]).Color == q2.NodesList.get(adjList[startIndex+1]).Color) {
				if (adjList[startIndex] > adjList[startIndex+1]) {
					//atomically add conflict node to array list 
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


public class q2 {
	public static Graph Conflicting;			//static graph for getting the adjacent list
	public static ArrayList<Node> NodesList; // a node list for get the node by id
	
	
	public static void main(String[] args) {
		//Graph graph = new Graph();
		if (args.length<3) {
			System.err.println("Please enter correct command!");
			return;
		}
		int n = 0,e = 0,t = 0;
		try {
			n = Integer.parseInt(args[0]);
			e = Integer.parseInt(args[1]);
			t = Integer.parseInt(args[2]);
			if (n<0||e<0||t<0) {
				System.err.println("Please enter correct command!");
				return;
			}
		} catch (NumberFormatException e1) {
			System.err.println("Please enter correct command!");
		}
		
		System.out.println(n+" "+ e+" "+t);
		ArrayList<Node> Nodes = new ArrayList<Node>();
		for (int i = 0; i < n; i++) {//construct node 
			Nodes.add(new Node(i));
		}
		NodesList = Nodes;//initialize nodeslist
		
		Conflicting = new Graph();
		Conflicting.AddNodes(Nodes);
		Conflicting.createAdjList(e);
		
		while (e!=0) {//construct edge
			int randomNode1 = (int)(Math.random()*Nodes.size());
			Node Node = Nodes.get(randomNode1);
			int randomNodeId;
			do {
				randomNodeId = (int)(Math.random()*Nodes.size());
			}while (randomNodeId == randomNode1); // avoid edge toward node itself
			e--;
			Conflicting.AddAdjacency(Node, Nodes.get(randomNodeId), e);
		}
		//System.out.println("Edge time: " + (System.currentTimeMillis()-time));
		ArrayList<Thread> assignThread = new ArrayList<Thread>();
		AtomicIntegerArray conflictArray = new AtomicIntegerArray(n);
		ArrayList<Node> ConflictingNodes = new ArrayList<Node>(Conflicting.GetNodes());
		AtomicInteger arrayIndex  = new AtomicInteger(0);
		for (int i = 0; i < n; i++) {//initialize with -1 cause each data is a node id it contain 0
			conflictArray.set(i, -1);
		}
		long Time = System.currentTimeMillis();
		
		
		while (!ConflictingNodes.isEmpty()){
			for (int i = 0; i<t ; i++) {
				
				Thread assign = new Assign(ConflictingNodes,i,t);
				assign.start();
				assignThread.add(assign);
			}
			
			
			try {
				//System.out.println(conflictThread.size());
				for (Thread thread : assignThread) {
					thread.join(); //waiting for assign stop
					
				}
				for (int i = 0; i<t ; i++) {	//detect conflict
					Thread detectconflit = new DetectConflicts( conflictArray , arrayIndex, i, t);
					detectconflit.start();
					detectconflit.join();
				}
				
				
				
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			assignThread.clear();//reset assign thread list
			
			ArrayList<Node> newconflict = new ArrayList<Node>();
			
			int i = 0;
			for ( i= 0; i < n; i++) {
				int nodeid = conflictArray.get(i);
				if (nodeid == -1) {//break if there is no more conflict node
					//System.out.println("number of conflict: "+i);
					break;
				}
				conflictArray.set(i, -1);
				newconflict.add(Nodes.get(nodeid));
			}
			
			arrayIndex.set(0);//reset arrayIndex
			
			ConflictingNodes = newconflict;
			
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
