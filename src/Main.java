import java.util.*;
public class Main{
	static int N; //number of nodes
	static Edge[][] G;
	static Vector<Node> allNodes;
	//static Vector<List<Message>> allMessageList;
	static Vector<Queue<Message>> allMessageList;

            
	static Vector<List<Integer>> neighborsOfAll;
	
	static volatile boolean areWeDone;

	static Vector<Edge> result;

	public static void main(String[] args){
		areWeDone = false;
		result = new Vector<>();
		//Read the command line file.
		if(args.length==2){

			System.out.println("Input file: " + args[0]);
			System.out.println("Output file: " + args[1]);
			String inputfile = args[0];
			String outputFile = args[1];
			Utility.readInput(inputfile);
			//Utility.printGraph(G);
			Utility.createNodes();
			Utility.startNodeThreads();
			for(int i =0; i < N; i++){
				try{
					allNodes.get(i).tId.join();
				}catch(InterruptedException e){
					System.out.println("InterruptedException");
				}
				
			}

			for(int i = 0; i < G.length; i++){
				for (int j =0; j < G[i].length; j++){
					if(G[i][j] != null && i < j){
						// System.out.println(G[i][j].getStart() + ", " + G[i][j].getEnd() + ": " 
						// 	+ G[i][j].getWeight() + "::" + G[i][j].getStatus());
						if(G[i][j].getStatus() == Status.BRANCH){
							result.add(G[i][j]);
						}
					}
				}
			}
			Utility.sortByWeight(result);
			for(int i = 0; i< result.size(); i++){
				System.out.println("(" + result.get(i).getStart() + " , " + result.get(i).getEnd() + ", "
				 + result.get(i).getWeight() + ")");
			}
			System.out.println("Main: Finished?");
		}else{
			System.out.println("Expected 2 command line arguments(file names)");
		}		
	}
}

