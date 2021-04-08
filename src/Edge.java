class Edge{
	//https://stackoverflow.com/questions/26421239/locking-cells-of-an-array
		
	private int start;
	private int end;
	private int weight;
	private EDGESTATUS status;
	
	

	public Edge(int u, int v, int w, EDGESTATUS st){

		weight = w;
		status = st;
		start = u;
		end = v;
	}
	public int getWeight(){ return weight; }
	public EDGESTATUS getStatus(){ return status; }
	public int getStart(){ return start; }
	public int getEnd(){ return end; }

	public void setStatus(EDGESTATUS st){ status = st; }
}
