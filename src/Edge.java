class Edge{
	//https://stackoverflow.com/questions/26421239/locking-cells-of-an-array
		
	private int start;
	private int end;
	private int weight;
	private int status;
	
	final int BASIC = 0;
	final int BRANCH = 1;
	final int REJECT = 2;
	

	public Edge(int u, int v, int w, int st){

		weight = w;
		status = st;
		start = u;
		end = v;
	}

	public int getWeight(){ return weight; }
	public int getStatus(){ return status; }
	public int getStart(){ return start; }
	public int getEnd(){ return end; }

	public void setStatus(int st){ status = st; }
}
