//https://web.mit.edu/6.005/www/fa14/classes/20-queues-locks/message-passing/
class Node implements Runnable{
	private Thread tId;
	private nodeId;
	private int state; // let 0 indicate sleep, 1: find, 2 found.
	public int fragmentId;
	public int level;
	private int[][] neighbors;	//list of all neighbours with thier edge status ( basic:0/branch:1/reject:2) and edge weights
				// a list of children: we can try to  use neighborlist as children also only 
				//can specify parent seperately
	private int parent;	// parent pointer
				//message queue with sender id , type of message and message.should be  public I guess.
				// but how other thread will put message on this this?call this object would make program sequential?
	private int rec;

	final int BASIC = 0
	final int BRANCH = 1;
	final int REJECT = 2;

	final int SLEEP = 0;
	final int FIND = 1;
	final int FOUND = 2;

	public Node(int nodeId, int[] listOfNeighbors){
		//Constructor to initialize a node
		state = SLEEP;
		fragmentId = nodeId; 			// can we use nodeId as fragement id?
		level = Integer.MAX_VALUE; 		// to say it's currently in sleep mode itself.
		this.nodeId = nodeId;
		int len = listOfNeighbors.length;
		neighbors = new int[len][4];  // let first column be for: nodeId of nighbor, second: weight, third: status
										// fourth: whether its child(0) or parent(1) or neither -1.
		for()


	}

	public void run(){
		//will implement the logic
	}

	public void start(){
		//will start the thread and call initialization method.
	}

	private initialization(){
		//findMinimum weight edge
		int q = findMinimumWeightEdge();
		if(q == -1) {
			System.out.println("Graph is disconnected or Edges or not initialize properly");

		}else{
			neighbors[q][2] = BRANCH;
			state = FOUND;
			level = 0;
			rec = 0;
			//send <connect,0> to q how? Can we invoke other thread how do I accept other 
			//threads message after  sending this message?
			sendConnectMessage(q);
		}
	}

	private int findMinimumWeightEdge(){
		int wt = Integer.MAX_VALUE;
		int indexMin = -1;
		for(int i =0; i < neighbors.lenght; i++){
			if(neighbors[i][2] == BASIC){

				if(wt > neighbors[i][1]){
					indexMin = i;
				}
			}
		}
		return indexMin;
	}

	private void sendConnectMessage(int qNodeid){
		//What I think we can't invoke method in other node..what do you think? We need to 
		// somehow give a msg to other node? Can we receive a message incide this object?
		// Or some global array/vector/queue need to be initialize..


	}

	public processConnectMsg(int qID, int qLevel){
		// proceesing connect message received from other nearest node.
		//Read Connect Message from your msg object

		if(level < qLevel){
			neighbors[qID][2] = BRANCH;
			// send Initiate msg. 
		}else if(neighbors[qID][2] == BASIC){
			//how to implement wait? // Can we call initialize or find... we need to invoke a method to
			// continue execution. I think we can make a manager method.
			waitManager();  // need to push back message else it will be lost...

		}else{

		}

	}

}