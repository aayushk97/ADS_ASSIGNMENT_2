import java.util.Scanner;
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
			while(fileStream.hasNextLine()){
				//System.out.println("Hey:");
				String line = fileStream.nextLine();
				if(line.length()>5){
					line = line.replace(",", " ").replace("(", "").replace(")", "");
					String[] x = line.split("\\s+");
					
					int p = Integer.parseInt(x[0]);
					int q = Integer.parseInt(x[1]);
					int w = Integer.parseInt(x[2]);
					//System.out.println("p: " + p + " q: " + q + " w: " + w);
					//System.out.println();
				}
			}
		}catch(FileNotFoundException e){
			System.out.println("FileNotFoundException");
		}catch(NumberFormatException e){
			System.out.println("NumberFormatException");
		}
	}
}