import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
public class Prims{
	
	private boolean unsettled[];
	private boolean settled[];
	private int numberofvertices;
	private int adjacencyMatrix[][];
	private int key[];
	public static final int INFINITE = 999;
	private int parent[];

	public Prims(int numberofvertices){
		this.numberofvertices = numberofvertices;
		unsettled = new boolean[numberofvertices];
		settled = new boolean[numberofvertices];
		adjacencyMatrix = new int[numberofvertices][numberofvertices];
		key = new int[numberofvertices];
		parent = new int[numberofvertices];
	
	}

	public int getUnsettledCount(boolean unsettled[]){
		int count = 0;
		for(int index = 0; index < unsettled.length; index++){
			if(unsettled[index]) count++;
		
		}
		return count;
	}

	public void primsAlgorithm(int adjacencyMatrix[][]){
		int evaluationVertex;
		for(int source = 0; source <numberofvertices; source++){
			for(int destination = 0; destination < numberofvertices; destination++){
				this.adjacencyMatrix[source][destination] = adjacencyMatrix[source][destination];
			}
		
		}

		for(int index = 0; index < numberofvertices; index++){
			key[index] = INFINITE;
		
		}

		key[0] = 0;
		unsettled[0] = true;
		parent[0] = 0;

		while(getUnsettledCount(unsettled) != 0){
			evaluationVertex = getMinimumKeyVertexFromUnsettled(unsettled);
			unsettled[evaluationVertex] = false;
			settled[evaluationVertex] = true;
			evaluateNeighbours(evaluationVertex);
		
		}
	
	}

	private int getMinimumKeyVertexFromUnsettled(boolean[] unsettled2){
		int min = Integer.MAX_VALUE;
		int node = 0;
		 for(int vertex = 0; vertex <  numberofvertices; vertex++){
		 	if(unsettled[vertex] == true && key[vertex] < min){
				node = vertex;
				min = key[vertex];
			}
		 
		 }

		 return node;
	
	}


	private void evaluateNeighbours(int evaluationVertex){
		for(int destinationvertex = 0; destinationvertex < numberofvertices; destinationvertex++){
			if(settled[destinationvertex] == false){
				if(adjacencyMatrix[evaluationVertex][destinationvertex] != INFINITE){
				if(adjacencyMatrix[evaluationVertex][destinationvertex] < key[destinationvertex]){
					key[destinationvertex] = adjacencyMatrix[evaluationVertex][destinationvertex];
					parent[destinationvertex] = evaluationVertex;

				
				}
				unsettled[destinationvertex] = true;
				}
			
			}
		
		}
	
	}

	public void printMST(){
		for(int vertex = 1; vertex < numberofvertices; vertex++){
			System.out.println("("+parent[vertex]+" , "+vertex+", "+adjacencyMatrix[vertex][parent[vertex]]+")");
		}
	
	}

	public static void main(String[] args){
	
		if(args.length == 2){
			System.out.println("Input file: "+args[0]);
			System.out.println("Output file: "+args[1]);
			
			String inputfile = args[0];
			String outputfile = args[1];
			
			int adjacency_matrix[][];
			
			int number_of_vertices;
			File fileObj = new File(inputfile);
			

			try{
			
			Scanner fileStream = new Scanner(fileObj);	
			number_of_vertices = fileStream.nextInt();
			fileStream.nextLine();
			adjacency_matrix = new int[number_of_vertices][number_of_vertices];
			for(int i = 0; i < number_of_vertices; i++){
				for(int j = 0; j < number_of_vertices; j++){
					adjacency_matrix[i][j] = INFINITE;
				}
			
			}
			while(fileStream.hasNextLine()){
				String line = fileStream.nextLine();
				if(line.length() > 5){
					line = line.replace(",", " ").replace("(", "").replace(")", "");
					String[] x = line.split("\\s+");
					int p = Integer.parseInt(x[0]);
					int q = Integer.parseInt(x[1]);
					int w = Integer.parseInt(x[2]);

					adjacency_matrix[p][q] = w;
					adjacency_matrix[q][p] = w;
					//System.out.println("P: "+p+" q:"+q+"w:"+w);
				}
			
			}
			
			Prims prims = new Prims(number_of_vertices);
			prims.primsAlgorithm(adjacency_matrix);
			prims.printMST();
		
		}catch(FileNotFoundException e){
			System.out.println("FileNotFoundException");
		}catch(NumberFormatException e){
			System.out.println("NumberFormatException");
		}catch(ArrayIndexOutOfBoundsException exp){
			System.out.println("NodeIds are not in range of 0 to N-1");
		}
			
		//scan.close();
		}
	}
}
