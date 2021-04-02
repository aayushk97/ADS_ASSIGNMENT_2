import java.util.*;
public class Main{
	static int N; //number of nodes
	static Edge[][] G;
	static Vector<Node> allNodes;
	static Vector<List<Message>> allMessageList;

	static Vector<List<Integer>> neighborsOfAll;
	

	public static void main(String[] args){
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

		}else{
			System.out.println("Expected 2 command line arguments(file names)");
		}		
	}
}