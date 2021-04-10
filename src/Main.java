import java.util.*;
public class Main{
	static int N; //number of nodes
	static Edge[][] G;
	static Vector<Node> allNodes;
	//static Vector<List<Message>> allMessageList;
	static Vector<Queue<Message>> allMessageList;

            
	static Vector<List<Integer>> neighborsOfAll;
	
	static volatile boolean areWeDone;

	public static void main(String[] args){
		areWeDone = false;
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
						System.out.println(G[i][j].getStart() + ", " + G[i][j].getEnd() + ": " 
							+ G[i][j].getWeight() + "::" + G[i][j].getStatus());
					}
				}
			}
			System.out.println("Main: Finished?");
		}else{
			System.out.println("Expected 2 command line arguments(file names)");
		}		
	}
}

class StopClass{
	boolean areWeDone;

}
// class MessageComparator implements Comparator<Message> {
//     public int compare(Message msg1, Message msg1)
//     {
        
//         return ((ConnectMessage).msg1).level < ((ConnectMessage).msg2).level ;
//     }
// }