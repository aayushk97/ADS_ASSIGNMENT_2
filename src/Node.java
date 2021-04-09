//https://web.mit.edu/6.005/www/fa14/classes/20-queues-locks/message-passing/
import java.util.*;
class Node implements Runnable{
	private Thread tId;
	private int nodeId;
	private NodeState state;
	public int fragmentId;
	public int level;
	public int numOfNeighbors;
	private int[][] neighbors;
	private HashMap<Integer, Integer> neighborsIndex;  //Because index in neighbor array and nodeId are not same
														// so to get the index of neighbour for a NodeId
	private Vector<Integer> children;
	private int parent;	
	private int rec;

	private int flag;
	private int waitingCount;
	private Queue<Message> messageList;
	private Vector<Message> waitingMessage;

	private int bestNode;
	private int bestWt;
	private int testNode;

	public Node(int nodeId, List<Integer> listOfNeighbors, Queue<Message> messageList){


		this.state = NodeState.SLEEP;
		this.fragmentId = nodeId; 			
										
		this.level = Integer.MAX_VALUE; 		// to say it's currently in sleep mode itself. 
		this.nodeId = nodeId;
		this.numOfNeighbors = listOfNeighbors.size();

		this.neighborsIndex = new HashMap<>();	
		this.neighbors = new int[listOfNeighbors.size()][5];  // let first column be for: nodeId of nighbor, second: weight, third: status
		prepareNeighborList(listOfNeighbors);		     // fourth: whether its child(0) or parent(1) or neither -1.
		
		this.children = new Vector<>();
		// System.out.println("Printing from node: " + nodeId);

		// for(int i =0 ; i < listOfNeighbors.size(); i++){
		// 	System.out.println(neighbors[i][0] + " " + neighbors[i][1] + " " + neighbors[i][2]);
		// }

		this.flag = 0;
		this.waitingCount = 0;
		
		this.messageList = messageList;
		this.waitingMessage = new Vector<Message>();
		

	}

	public void run() {
		//will implement the logic
		//Edit 2: Currently, Code in this method only for checking if thread are running properly.
		// Random rn = new Random();
		// int i = 0;

		initialization();
		System.out.println("Initialization done! Reading Message...");
		while(true){
			synchronized(messageList){
				try{
					if(isMessageQEmpty()){
						messageList.wait();
					}
				}catch(InterruptedException e){
					System.out.println("InterruptedException");
				}
			}
		
			System.out.println(nodeId + " Came Here??");
			Message msg = getMessage();
			if(msg!= null){
				if(msg.type == MessageType.CONNECT){
					System.out.println(nodeId + " proceesing Connect Message");
					int flag = processConnectMsg(msg);
					if(flag == 0 ){

					}
				}else if (msg.type == MessageType.INITIATE){
					System.out.println("Process Initiate Message...");
					processInitiateMsg(msg);

				}else if(msg.type == MessageType.TEST){

				}else if(msg.type == MessageType.REJECT){
					
				}
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

		System.out.println("Node: " + this.nodeId + " Min Index: " + q);
		if(q == -1) {
			System.out.println("Graph is disconnected or Edges or not initialize properly");

		}else{

			neighbors[q][2] = Status.BRANCH.ordinal();   
			state = NodeState.FOUND;    //set the state of node to be found           

			level = 0;	  //set the level to 0 as it is only node in fragment
			rec = 0;			
			
			sendConnectMessage(neighbors[q][0], level);   
		}
	}

	private int findMinimumWeightEdge(){

		int wt = Integer.MAX_VALUE;
		int indexMin = -1;
		for(int i =0; i < neighbors.length; i++){         

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
		
		Message msg = new ConnectMessage(this.nodeId, qNodeid, myLevel);
		sendMessage(msg);
	}

	private void sendInitiateMessage(int qNodeid, int level, int fragmentId, NodeState state){

		Message msg = new InitiateMessage(this.nodeId, level,  qNodeid, fragmentId, state);
		sendMessage(msg);
	}

	public int processConnectMsg(Message msg){
		// proceesing connect message received from other nearest node.
		// Read Connect Message from your msg object

		int qID = neighborsIndex.get(msg.sender);  //here qID is actually index of neighbor array 

		if(level < ((ConnectMessage)msg).level){
			System.out.println(nodeId + "Processing Connect in rule LT");
			neighbors[qID][2] = Status.BRANCH.ordinal();
			neighbors[qID][3] = 0; //making qId child
			sendInitiateMessage(msg.sender, this.level, this.fragmentId, this.state);
			return 1;

		}else if(neighbors[qID][2] == Status.BASIC.ordinal()){
			
			System.out.println(nodeId + "waitManager call...");
			waitManager(msg);  // need to push back message else it will be lost... //EDIT 1: QUEUE? Edit 2: may be..

		}else{
			System.out.println(nodeId + "Processing Connect in rule ET");
			neighbors[qID][3] = 0;
			sendInitiateMessage(msg.sender, this.level + 1, neighbors[qID][1], NodeState.FIND);
			return 1;
		}
		return 0;

	}
	
	private void processInitiateMsg(Message msg){
		System.out.println("In processInitiateMsg method");

		this.level = ((InitiateMessage)msg).level;
		this.fragmentId = ((InitiateMessage)msg).fragmentName;
		this.state = ((InitiateMessage)msg).state;
		this.parent = msg.sender;

		bestNode = -1;
		bestWt = Integer.MAX_VALUE;  // Need to check the limit of weight...
		testNode = -1;



	}
	private void waitManager(Message msg){
		System.out.println("In waitManager");
		flag = 1;
		waitingCount++;
		waitingMessage.add(msg);

	}

	private void sendMessage(Message msg){
		int receipient = msg.receipent;
		Queue<Message> recvQ = Main.allMessageList.get(receipient);
		System.out.println("Sending message from " + nodeId + " to "+ msg.receipent);
		synchronized(recvQ){
			recvQ.add(msg);
			recvQ.notify();
		}
	}

	private boolean isMessageQEmpty(){
		synchronized(messageList){
			if(messageList.isEmpty()){
				return true;
			}
		}
		return false;
	}

	private Message getMessage(){
		
		Message msg;
		synchronized(messageList){
			//try{
				if(messageList.isEmpty()){
					System.out.println("Message Queue for " + nodeId + " is empty.");
					//messageList.wait(); 
					return null;
				}
			// }catch(InterruptedException e){

			// }
			//messageList.add(msg);
			msg = messageList.remove();
			
			messageList.notify();
			return msg;
		}
		//System.out.println("node: " + nodeId + " Message: " + "sender: " + msg.sender + " " + msg.type);
	}
	
	private void processTestMessage(TestMessage msg){
		if(level > msg.level){
			//wait
			//place message at end of queue
		}else if(fraagmentId == msg.fragmentName){
			//node is in same fragment so to prevent cycles this edge has to be rejected
			if(neighbours[i][2] == Status.BASIC.ordinal()){
				neighbours[i][2] = Status.BASIC.ordinal();
			}
			
			
		
		}else{
		
		
		}
	
	}

	private void prepareNeighborList(List<Integer> listOfNeighbors){
	
		int i = 0;
		System.out.println("Node: " + this.nodeId);
		for(Integer q : listOfNeighbors){
			//System.out.println(q + " " + Main.G[q][this.nodeId].getWeight());
			neighbors[i][0] = q;									//NodeId of neighbor connected to this edge
			neighbors[i][1] = Main.G[q][this.nodeId].getWeight();	//Weight of edge
			neighbors[i][2] = Status.BASIC.ordinal();				//Status of Edge
			neighbors[i][3] = -1;									// child or parent 0: child and parent: 1
			neighbors[i][4] = 0;  //For counting report messase  0: not received, 1: received need to reset when required
			neighborsIndex.put(q, i);
			i++;
		}

	}

}
//EDIT 1: Can we have a class for edge so that it would be easier for us to connect to 2 nodes. 
//Just connect one end of edge to one node and other end to other instead of specifying on both 
//nodes what are the nodes it is connected to?
//https://www.geeksforgeeks.org/kruskals-minimum-spanning-tree-algorithm-greedy-algo-2/


