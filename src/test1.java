import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.*;

public class test1{
	static int N; //number of nodes
	static Edge[][] G;
	static Vector<Node> allNodes;
	//static Vector<List<Message>> allMessageList;
	static Vector<Queue<Message>> allMessageList;

            
	static Vector<List<Integer>> neighborsOfAll;
	

	public static void main(String[] args){
		int numNodes;
			
		Scanner in = new Scanner(System.in);
  		
		System.out.println("Enter the no of nodes;\n");
		
        	numNodes = in.nextInt();
        	G = new Edge[numNodes][numNodes];
		
		for(int i = 0; i < numNodes; i++){
			for(int j = 0; j < numNodes; j++){
				test1.G[i][j] = null;
				
			}
		}	
		int num = makeGraph(numNodes, false);
		System.out.println("No of edges produced:"+num);
	
	}
	
	public static int makeGraph(int numNodes, boolean dense){
	
	
	int numEdges = 0;
	
	int finalNumEdges = numNodes*(numNodes-1)/2;
	System.out.println(""+numNodes);
	Vector<Integer> weights = new Vector<Integer>(finalNumEdges);
	Vector<Integer> graphNodes = new Vector<Integer>(numNodes);
	
	for(int i = 0; i < finalNumEdges; i++){
		weights.add(-1);
	}
	
	for(int i = 0; i < numNodes; i++){
		graphNodes.add(-1);
	}
	
	Random random = new Random();
	
		
	int j = 0, k = 0;
	boolean first = true; 
	while(numEdges < finalNumEdges){
		
		int p = random.nextInt(numNodes);
		int q = random.nextInt(numNodes);
		
		
		//if there are self edges and repeat edges
		if(p == q || !(test1.G[p][q] == null) ) continue;
		
		boolean isNodepPresent = false;
		boolean isNodeqPresent = false;
		for(int i = 0; i < numNodes; i++){
			if(graphNodes.get(i) == p){
			 	isNodepPresent = true;
			 	break;
			 }	
			
		}
		for(int i = 0; i < numNodes; i++){
			if(graphNodes.get(i) == q){ 
				isNodeqPresent = true;
				break;
			}
			
		}
		
		
		if(!isNodepPresent && !isNodeqPresent && !first) continue;
		
		if(!isNodepPresent && first) {graphNodes.set(k, p); k++;}
		if(!isNodeqPresent && first) {graphNodes.set(k, q); k++;}
		//System.out.println("No :"+k);
		
		
		//else we assign an edge between them
		int w = -1;
		
		while(w < 1){
			w = random.nextInt(numNodes*numNodes);
			
			//System.out.println("w:"+w);
			boolean present = false;
			for(int i = 0; i < finalNumEdges; i++){
				if(weights.get(i) == w) present = true;
			}
			
			if(present == true) w = -1;
		
		}
		
		//boolean addEdge = false;
		if(dense){
			int prob = random.nextInt(10);
			
			//If number is 0-7 then we add it to graph otherwise we skip it
			if(prob >= 0 && prob <= 7){
				weights.set(j, w);
				if(!isNodepPresent && !first) {graphNodes.set(k, p); k++;}
				if(!isNodeqPresent && !first) {graphNodes.set(k, q); k++;}
				System.out.println("No :"+k);
				j++;
				Edge e = new Edge(p, q, w, Status.BRANCH);
				
				System.out.println("("+p+" ,"+q+" , "+w+")");
				test1.G[p][q] = e;
				test1.G[q][p] = e;
				
		
			}
			
			numEdges++; 
		}else{ //if graph is sparse
		
			int prob = random.nextInt(10);
			
			//If number is 0-2 then we add it to graph otherwise we skip it
			if(prob >= 0 && prob <= 2){
			
				weights.set(j, w);
				if(!isNodepPresent && !first) {graphNodes.set(k, p); k++;}
				if(!isNodeqPresent && !first) {graphNodes.set(k, q); k++;}
				//System.out.println("No :"+k);
				j++;
				Edge e = new Edge(p, q, w, Status.BRANCH);
				System.out.println("("+p+" ,"+q+" , "+w+")");
				test1.G[p][q] = e;
				test1.G[q][p] = e;
				
		
			}
			
			numEdges++;
		
		
		}
		
		first = false; 
	}
	
	return j;
	

	}
}

