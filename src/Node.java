//https://web.mit.edu/6.005/www/fa14/classes/20-queues-locks/message-passing/
import java.util.*;
class Node implements Runnable{
	private Thread tId;
	private int nodeId;
	private int state;
	public int fragmentId;
	public int level;
	private int[][] neighbors;	//list of all neighbours with thier edge status ( basic:0/branch:1/reject:2) and edge weights
				// a list of children: we can try to  use neighborlist as children also only 
				//can specify parent seperately
	private int parent;	// parent pointer
				// message queue with sender id , type of message and message.should be  public I guess.
				// but how other thread will put message on this this?call this object would make program sequential?
	private int rec;

	private List<Message> messageList;

	//edge status
	final int BASIC = 0;
	final int BRANCH = 1;
	final int REJECT = 2;

	//states
	final int SLEEP = 0;
	final int FIND = 1;
	final int FOUND = 2;

	public Node(int nodeId, List<Integer> listOfNeighbors, List<Message> messageList){

		//Constructor to initialize a node
		// System.out.println("NodeId: " + nodeId);
		// for(Integer neighb : listOfNeighbors){
		// 	System.out.println(neighb + " ");
		// }
		prepareNeighborList(listOfNeighbors);
		state = SLEEP;
		fragmentId = nodeId; 			// can we use nodeId as fragement id? 
										//Edit 1: When it is initialized the value of fragment id will be node id. Edit 2: Ok
		level = Integer.MAX_VALUE; 		// to say it's currently in sleep mode itself. 
		this.nodeId = nodeId;
		//int len = listOfNeighbors.length;
		neighbors = new int[20][4];  // let first column be for: nodeId of nighbor, second: weight, third: status
										// fourth: whether its child(0) or parent(1) or neither -1.
		this.messageList = messageList;
		
	}

	public void run() {
		//will implement the logic
		//Edit 2: Currently, Code in this method only for checking if thread are running properly.
		Random rn = new Random();
		int i = 0;
		while(true){
			if(i==0){
				System.out.println("initialization");
				initialization();
				i++;
			}
			else{
				System.out.println("Running " + nodeId);
				int q = rn.nextInt(Main.N);

				Message msg = new ConnectMessage(this.nodeId, q, 3);
				sendMessage(msg);
				getMessage();
				
			}

			try{
				Thread.sleep(500);
			}catch(Exception e){

			}
		}
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
		System.out.println("Min Index: " + q);
		if(q == -1) {
			System.out.println("Graph is disconnected or Edges or not initialize properly");

		}else{
			neighbors[q][2] = BRANCH;   
			state = FOUND;               
			level = 0;
			rec = 0;
			//send <connect,0> to q how? Can we invoke other thread how do I accept other 
			//threads message after  sending this message?
			sendConnectMessage(q, level);   //EDIT 1: We send the level as parameter not the min weight?
									// Edit 2 : yes, But need to send sender identification also else howreceiver will
									//know from where message have come. q is not weight it index in neighour array
		}
	}

	private int findMinimumWeightEdge(){
		int wt = Integer.MAX_VALUE;
		int indexMin = -1;
		for(int i =0; i < neighbors.length; i++){         
			if(neighbors[i][2] == BASIC){
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
			neighbors[qID][2] = BRANCH;
			// send Initiate msg. 
		}else if(neighbors[qID][2] == BASIC){
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
		
	}

}
//EDIT 1: Can we have a class for edge so that it would be easier for us to connect to 2 nodes. 
//Just connect one end of edge to one node and other end to other instead of specifying on both 
//nodes what are the nodes it is connected to?
//https://www.geeksforgeeks.org/kruskals-minimum-spanning-tree-algorithm-greedy-algo-2/
