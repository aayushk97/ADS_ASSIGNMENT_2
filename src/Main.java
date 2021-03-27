public class Main{
	static int N; //number of nodes
	public static void main(String[] args){
		//Read the command line file.
		if(args.length==2){
			//System.out.println("Input file: " + args[0]);
			//System.out.println("Output file: " + args[1]);
			String inputfile = args[0];
			String ouutputFile = args[1];
			Utility.readInput(inputfile);
		}else{
			System.out.println("Expected 2 command line arguments(file names)");
		}
		
	}


}

class Edge{
	//https://stackoverflow.com/questions/26421239/locking-cells-of-an-array
	int weight;
	int status;
}