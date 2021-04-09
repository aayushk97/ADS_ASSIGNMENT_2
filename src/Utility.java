import java.util.Scanner;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

class Utility{
	//read Input 
	
	public static void readInput(String fileName){

		File fileObj = new File(fileName);

		try{
			
			Scanner fileStream = new Scanner(fileObj);
			Main.N = fileStream.nextInt();
			fileStream.nextLine();
			System.out.println("Number of nodes: " + Main.N);
			Main.G = new Edge[Main.N][Main.N];

			declareAllLists();

			while(fileStream.hasNextLine()){
				//System.out.println("Hey:");
				String line = fileStream.nextLine();
				if(line.length()>5){
					line = line.replace(",", " ").replace("(", "").replace(")", "");
					String[] x = line.split("\\s+");
					
					int p = Integer.parseInt(x[0]);
					int q = Integer.parseInt(x[1]);
					int w = Integer.parseInt(x[2]);

					Edge e = new Edge(p, q, w, Status.BRANCH);
					System.out.println("p: " + p + " q: " + q + " w: " + w);
					
					Main.neighborsOfAll.get(p).add(q);
					Main.neighborsOfAll.get(q).add(p);

					if(Main.G[p][q]==null && Main.G[q][p] == null){
						Main.G[p][q] = e;
						Main.G[q][p] = e;
					}else{
						System.out.println("There seems directed or duplicate edges");
					}
					
				}
			}
		}catch(FileNotFoundException e){
			System.out.println("FileNotFoundException");
		}catch(NumberFormatException e){
			System.out.println("NumberFormatException");
		}catch(ArrayIndexOutOfBoundsException exp){
			System.out.println("NodeIds are not in range of 0 to N-1");
		}
	}

	public static void printGraph(Edge[][] G){
		System.out.println("Printing Graph: All edges");
		for(int i = 0; i < Main.N; i++){
			for(int j = 0; j < Main.N; j++){
				if(G[i][j] != null && i<=j ){
					Edge e = G[i][j];
					System.out.println(e.getStart() + " " + e.getEnd() + " " + e.getWeight());
				}
			}
		}
	}

	public static void createNodes(){
		for(int i =0; i < Main.N; i++){
			Node node = new Node(i, Main.neighborsOfAll.get(i), Main.allMessageList.get(i));
			Main.allNodes.set(i, node);
		}
	}

	public static void startNodeThreads(){
		for(int i =0; i < Main.N; i++){
			Main.allNodes.get(i).start();
		}
	}
	private static void declareAllLists(){
		Main.allMessageList = new Vector<>();
		Main.neighborsOfAll = new Vector<>();
		Main.allNodes = new Vector<>();

		for(int i = 0; i < Main.N; i++){
			Main.allNodes.add(null);
			Main.neighborsOfAll.add(new ArrayList<>());
			Main.allMessageList.add(new ArrayList<>());
		}
	}
}
