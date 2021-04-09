//https://web.mit.edu/6.005/www/fa14/classes/20-queues-locks/message-passing/
import java.util.*;
class Node implements Runnable{
	private Thread tId;
	private int nodeId;
	private NodeState state;
	public int fragmentId;
	public int level;
	
	private int[][] neighbors;	
	private int parent;	
	private int rec;

	private List<Message> messageList;

	public Node(int nodeId, List<Integer> listOfNeighbors, List<Message> messageList){


		state = NodeState.SLEEP;
		fragmentId = nodeId; 			// can we use nodeId as fragement id? 
										//Edit 1: When it is initialized the value of fragment id will be node id. Edit 2: Ok
		level = Integer.MAX_VALUE; 		// to say it's currently in sleep mode itself. 
		this.nodeId = nodeId;
		//int len = listOfNeighbors.length;

		neighbors = new int[4][listOfNeighbors.size()];  // let first column be for: nodeId of nighbor, second: weight, third: status
		prepareNeighborList(listOfNeighbors);		// fourth: whether its child(0) or parent(1) or neither -1.
		
		// System.out.println("Printing from node: " + nodeId);

		// for(int i =0 ; i < listOfNeighbors.size(); i++){
		// 	System.out.println(neighbors[i][0] + " " + neighbors[i][1] + " " + neighbors[i][2]);
		// }
		
		this.messageList = messageList;
		
	}

	public void run() {
		//will implement the logic
		//Edit 2: Currently, Code in this method only for checking if thread are running properly.
		Random rn = new Random();
		int i = 0;

		initialization();
		System.out.println("Reading Message.");
		getMessage();
		// while(true){
		// 	if(i==0){
		// 		System.out.println("initialization");
		// 		initialization();
		// 		i++;
		// 	}
		// 	else{
		// 		System.out.println("Running " + nodeId);
		// 		int q = rn.nextInt(Main.N);

		// 		Message msg = new ConnectMessage(this.nodeId, q, 3);
		// 		sendMessage(msg);
		// 		getMessage();


			
		
	}

	public void start(){
		//will start the thread and call initialization method.
		System.out.println("Starting Thread: " + nodeId);
		if(tId == null){
			tId = new Thread(this);
			tId.start();
		}
	}

	private void initialization(){
		//findMinimum weight edge
		int q = findMinimumWeightEdge();
		//q = neighbors[0][q];
		System.out.println("Node: " + this.nodeId + " Min Index: " + q);
		if(q == -1) {
			System.out.println("Graph is disconnected or Edges or not initialize properly");

		}else{

			// //q = neighbors[q][0];
			// neighbors[q][2] = BRANCH;   
			// state = FOUND;    //we set the state of node to be found           

			neighbors[q][2] = Status.BRANCH.ordinal();   
			state = NodeState.FOUND;    //we set the state of node to be found           

			level = 0;	  //set the level to 0 as it is only node in fragment
			rec = 0;			
			//send <connect,0> to q how? Can we invoke other thread how do I accept other 
			//threads message after  sending this message?
			sendConnectMessage(neighbors[q][0], level);   //EDIT 1: We send the level as parameter not the min weight?
									// Edit 2 : yes, But need to send sender identification also else howreceiver will
									//know from where message have come. q is not weight it index in neighour array
		}
	}

	private int findMinimumWeightEdge(){
		int wt = Integer.MAX_VALUE;
		int indexMin = -1;
		for(int i =0; i < neighbors.length; i++){         

			// if(neighbors[i][2] == BASIC){
			// 	System.out.println("Node:  "+ this.nodeId + " Weight: " + wt + " :" + neighbors[i][1]);

			if(neighbors[i][2] == Status.BASIC.ordinal()){
				System.out.println("Weight: " + wt + " :" + neighbors[i][1]);

				if(wt > neighbors[i][1]){
					indexMin = i;
					wt = neighbors[i][1];
				}
			}
		}
		return indexMin;
	}

	private void sendConnectMessage(int qNodeid, int myLevel){
		//What I think we can't invoke method in other node..what do you think? We need to 
		// somehow give a msg to other node? Can we receive a message incide this object?
		// Or some global array/vector/queue need to be initialize..
		
		//Edit 1: we can try using DataOutputStream/DataInputStream as in below link states
		//https://stackoverflow.com/questions/5680259/using-sockets-to-send-and-receive-data
		//Edit 2: But sockets we need if we need to communicate through network?
		//If some global data structure is used, we need to implement access control?

		//EDIT 1: Is it better to Create a message class so we don't have to write seperate function for sending each type of message?
		// Edit 2: Yes, we can make a message class with field of messageType, message parameter... but need to be 
		// careful about different type message parameters Or can we use subclasses for messages?
		Message msg = new ConnectMessage(this.nodeId, qNodeid, myLevel);
		sendMessage(msg);
	}

	public void processConnectMsg(int qID, int qLevel){
		// proceesing connect message received from other nearest node.
		//Read Connect Message from your msg object

		if(level < qLevel){
			neighbors[qID][2] = Status.BRANCH.ordinal();
			// send Initiate msg. 
		}else if(neighbors[qID][2] == Status.BASIC.ordinal()){
			//how to implement wait? // Can we call initialize or find... we need to invoke a method to
			// continue execution. I think we can make a manager method.
			//waitManager();  // need to push back message else it will be lost... //EDIT 1: QUEUE? Edit 2: may be..

		}else{

		}

	}

	private void sendMessage(Message msg){
		int receipient = msg.receipent;
		List<Message> recvQ = Main.allMessageList.get(receipient);
		System.out.println("Sending message from " + nodeId + " to "+ msg.receipent);
		synchronized(recvQ){
			recvQ.add(msg);
			recvQ.notify();
		}
	}

	private void getMessage(){
		//List<Message> recvQ = Main.allMessageList.get(nodeId);
		Message msg;
		synchronized(messageList){
			if(messageList.isEmpty()){
				System.out.println("Message Queue for " + nodeId + " is empty.");
				return;
			}
			//messageList.add(msg);
			msg = messageList.remove(0);
			
			messageList.notify();
		}
		System.out.println("node: " + nodeId + " Message: " + "sender: " + msg.sender + " " + msg.type);
	}

	private void prepareNeighborList(List<Integer> listOfNeighbors){
		//Need to fill neighbors[][] array
		// I am thinking to make it numberOfneighbors*4 size array (neighborId, weight, statusOfEdge, parent/child/none)

		int i = 0;
		System.out.println("Node: " + this.nodeId);
		for(Integer q : listOfNeighbors){
			//System.out.println(q + " " + Main.G[q][this.nodeId].getWeight());
			neighbors[i][0] = q;
			neighbors[i][1] = Main.G[q][this.nodeId].getWeight();
			neighbors[i][2] = Status.BASIC.ordinal();
			neighbors[i][3] = -1;
			i++;
		}

	}

}
//EDIT 1: Can we have a class for edge so that it would be easier for us to connect to 2 nodes. 
//Just connect one end of edge to one node and other end to other instead of specifying on both 
//nodes what are the nodes it is connected to?
//https://www.geeksforgeeks.org/kruskals-minimum-spanning-tree-algorithm-greedy-algo-2/
