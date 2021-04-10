//https://web.mit.edu/6.005/www/fa14/classes/20-queues-locks/message-passing/
import java.util.*;
class Node implements Runnable{
	public Thread tId;
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
	
	

	// private int flag;
	// private int waitingCount;
	private Queue<Message> messageList;

	private Vector<Queue<Message>> waitingMessage;
	private int[][] waitingStats;
	

	private int rec;

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

		this.messageList = messageList;
		//this.waitingMessage = new Vector<Message>();
		this.waitingMessage = new Vector<>();
		this.waitingStats = new int[3][2];
		for(int i =0; i< 3; i++){
			waitingMessage.add(new LinkedList<>());
			waitingStats[i][0] = 0;	 //will store how manymessage are in wait 0: connect, 1: test , 2: report
			waitingStats[i][1] = -1;  //will store minimum level in wait
		}

	}

	public void run() {
		
		initialization();

		if(Parameters.DEBUG3)
			System.out.println("Initialization done! Reading Message...");

		while(true){
			if(this.state == NodeState.FOUND){
				//need check whether algo completed
				if(Main.areWeDone){
					System.out.println(nodeId + " We are Done");
					break;
				}
			}
			 
			Message msg = checkWaitinglist();
			if(msg==null){
				if(Parameters.DEBUG2)
					System.out.println("\n"+ nodeId + " Returned Null from checkWaitinglist.\n");
			
			}else{
				if(Parameters.DEBUG2)
					System.out.println("\n"+ nodeId + " Returned " +msg.type +" mesage from checkWaitinglist.\n");
			}
			if(msg==null){
				//System.out.println(nodeId + " Now checking receive queue\n" );
				// synchronized(messageList){
				// 	try{
				// 		if(isMessageQEmpty()){
				// 			messageList.wait();
				// 		}
				// 	}catch(InterruptedException e){
				// 		System.out.println("InterruptedException");
				// 	}
				// }

				//System.out.println(nodeId + " Came Here??");
				msg = getMessage();

			}
			
			if(msg!= null){
				if(msg.type == MessageType.CONNECT){
					System.out.println(nodeId + " proceesing Connect Message: " + System.identityHashCode(msg) + "\n");
					int flag = processConnectMsg(msg);		//wait managing
					
				}else if (msg.type == MessageType.INITIATE){
					System.out.println(nodeId + " Process Initiate Message...");
					processInitiateMsg(msg);

				}else if(msg.type == MessageType.TEST){
					processTestMessage(msg);				//wait managing

				}else if(msg.type == MessageType.REJECT){
					processRejectMsg(msg);
					
				}else if(msg.type == MessageType.ACCEPT){
					processAcceptMsg(msg);

				}else if(msg.type == MessageType.REPORT){
					processsReportMsg(msg);

				}else{
					//ChangeRoot message
					processChangeRootMsg(msg);				//wait managing
				}
			}else{
				//System.out.println("\nmessage null...problem??\n");
			}
		}

		updateEdges();
		System.out.println(nodeId + " is finished");
		
	}

	private void updateEdges(){
		for(int i =0; i< numOfNeighbors; i++){
			if(neighbors[i][2] == Status.BASIC.ordinal()){
				System.out.println("Something wrong");
			}else{
				synchronized(Main.G[nodeId][neighbors[i][0]]){
					Main.G[nodeId][neighbors[i][0]].setStatus(Status.values()[neighbors[i][2]]);
					//notify();
				}
			}
		}
	}
	private Message checkWaitinglist(){
		int i, nextMin, l;
		boolean flag = false;
		Message msg = null;

		if(waitingStats[0][0] > 0  && waitingStats[0][1] <= this.level){

			Queue<Message> queue = waitingMessage.get(0);
			
			if(Parameters.DEBUG2)
				System.out.println(nodeId + " Dequeing Connect: " + queue.size() +" "+ waitingStats[0][0]);

			i = 0;
			nextMin = Integer.MAX_VALUE;

			while(i < waitingStats[0][0]){
				Message m = waitingMessage.get(0).remove();
				
				System.out.println( m==null);
				l = ((ConnectMessage)m).level;

				if(Parameters.DEBUG2)
					System.out.println("\n"+ nodeId + " Is this m is null: " + waitingStats[0][1] + " =? " 
						+ l + " my level: " + this.level + " "+ flag);

				int qIndex = neighborsIndex.get(m.sender);

				boolean x = (waitingStats[0][1] == l) && (l == this.level) && (neighbors[qIndex][2] == Status.BRANCH.ordinal());
				boolean y = l < this.level;

				if(!flag && (x || y)){
					
					msg = m;
					flag = true;
					
				}else{
					if(nextMin > l){
						nextMin = l;
					}
					waitingMessage.get(0).add(m);
				}
				i++;
			}

			if(flag){
				waitingStats[0][0]--;
				
				if(waitingStats[0][0] == 0){
					waitingStats[0][1] = -1;
				}else{
					waitingStats[0][1] = nextMin;
				}
				if(Parameters.DEBUG2)
					System.out.println(nodeId + " Dequeing Connect: newSizes: " 
						+ waitingMessage.get(0).size() +" "+ waitingStats[0][0]);

				return msg;
			}
		}

		if(!flag && waitingStats[1][0] > 0  && waitingStats[1][1] <= this.level){

			if(Parameters.DEBUG2)
				System.out.println(nodeId + " Dequeing Test message from wait " 
					+ waitingMessage.get(1).size() +" " +  waitingStats[1][0]);
			
			i = 0;
			nextMin = Integer.MAX_VALUE;

			while(i < waitingStats[1][0]){

				Message m = waitingMessage.get(1).remove();
				l = ((TestMessage)m).level;

				if(!flag && waitingStats[1][1] == l){
					msg = m;
					flag = true;
					
				}else{
					if(nextMin > l){
						nextMin = l;
					}
					waitingMessage.get(1).add(m);
				}
				i++;
			}

			if(flag){
				waitingStats[1][0]--;
		

				if(waitingStats[1][0] == 0){
					waitingStats[1][1] = -1;
				}else{
					waitingStats[1][1] = nextMin;
				}

				return msg;
			}
		}
		if(!flag && waitingStats[2][0] > 0  && this.state == NodeState.FOUND){
			System.out.println(nodeId + " Dequeing Report message from wait size: " 
				+ waitingMessage.get(0).size()+" " +  waitingStats[2][0]);

			msg = waitingMessage.get(2).remove();
			System.out.println(nodeId + " report null? : " + (msg==null));
			waitingStats[2][0]--;
			flag = true;
			return msg;
		}	
		
		return msg;
		
	}
	private void waitManager(Message msg){

		System.out.println("In waitManager ");

		if(msg.type == MessageType.CONNECT) {

			System.out.println(nodeId + " Enqueue ConnectMessage " + waitingStats[0][0] + " "
				+ waitingMessage.get(0).size() +"\n");

			int l = ((ConnectMessage)msg).level;

			waitingStats[0][0]++;

			if(waitingStats[0][1] == -1)
			{
				waitingStats[0][1] = l; 
			}
			else if (l < waitingStats[0][1])
				waitingStats[0][1] = l; 

			waitingMessage.get(0).add(msg);
			System.out.println(nodeId + " Enqueued ConnectMessage " +  waitingStats[0][0] + " " 
				+ waitingMessage.get(0).size() +"\n");

		}else if(msg.type == MessageType.TEST){
			System.out.println(nodeId + " Enqueue Test " + waitingStats[1][0] + " " + waitingMessage.get(2).size());
			int l = ((TestMessage)msg).level;

			waitingStats[1][0]++;

			if(waitingStats[1][1] == -1)
			{
				waitingStats[1][1] = l; 
			}
			else if (l < waitingStats[0][1])
				waitingStats[1][1] = l; 

			waitingMessage.get(1).add(msg);

			System.out.println(nodeId + " Enqueued Test " + waitingStats[1][0] + " " + waitingMessage.get(2).size()+"\n");
			
		}else{
			System.out.println(nodeId + " Enqueue report t: " + msg.type + " size: " + waitingMessage.get(2).size());
			waitingStats[2][0]++;
			waitingMessage.get(2).add(msg);
			System.out.println(nodeId + " Enqueued report t:"+ msg.type + " newSize: " + waitingMessage.get(2).size()+"\n");
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

		if(Parameters.DEBUG1)
			System.out.println("Node " + nodeId + ": Initialization method..."+ " Min Index: " + q);

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
		for(int i =0; i < numOfNeighbors; i++){         

			if(neighbors[i][2] == Status.BASIC.ordinal()){
				//System.out.println("Weight: " + wt + " :" + neighbors[i][1]);

				if(wt > neighbors[i][1]){
					indexMin = i;
					wt = neighbors[i][1];
				}
			}
		}
		
		return indexMin;
	}

	

	public int processConnectMsg(Message msg){
		// proceesing connect message received from other nearest node.
		// Read Connect Message from your msg object
		if(Parameters.DEBUG1)
			System.out.println("Node " + nodeId + ": processConnectMsg method..." + System.identityHashCode(msg) );

		int qID = neighborsIndex.get(msg.sender);  //here qID is actually index of neighbor array 

		if(((ConnectMessage)msg).level < this.level){

			System.out.println(nodeId + "Processing Connect in rule LT");

			neighbors[qID][2] = Status.BRANCH.ordinal();

			neighbors[qID][3] = 0; //making qId child

			sendInitiateMessage(msg.sender, this.level, this.fragmentId, this.state);

			System.out.println(nodeId + " LT: Processed: + " +  System.identityHashCode(msg));

			return 1;

		}else if(neighbors[qID][2] == Status.BASIC.ordinal()){
			
			System.out.println(nodeId + " waitManager call...");
			waitManager(msg);  // need to push back message else it will be lost... //EDIT 1: QUEUE? Edit 2: may be..
			System.out.println(nodeId + " WaiT: + " +  System.identityHashCode(msg));
		}else{
			System.out.println(nodeId + "Processing Connect in rule ET");
			neighbors[qID][3] = 0;
			sendInitiateMessage(msg.sender, this.level + 1, neighbors[qID][1], NodeState.FIND);
			System.out.println(nodeId + " ET: Processed: + " +  System.identityHashCode(msg));
			return 1;
		}
		return 0;

	}
	
	private void processInitiateMsg(Message msg){
		if(Parameters.DEBUG1)
			System.out.println("Node " + nodeId + ": In processInitiateMsg method");

		this.level = ((InitiateMessage)msg).level;
		this.fragmentId = ((InitiateMessage)msg).fragmentId;
		this.state = ((InitiateMessage)msg).state;
		this.parent = msg.sender;

		bestNode = -1;
		bestWt = Integer.MAX_VALUE;  // Need to check the limit of weight...
		testNode = -1;
		//System.out.println("Exception at line 225??");
		for(int i = 0; i < numOfNeighbors; i++){
			if(neighbors[i][2] == Status.BRANCH.ordinal() && neighbors[i][0] != msg.sender){
				sendInitiateMessage(neighbors[i][0], this.level, this.fragmentId, this.state);
			}
		}

		if (this.state == NodeState.FIND){
			this.rec = 0;
			findMin();
		}

	}


	private void findMin(){

		if(Parameters.DEBUG1)
			System.out.println("Node " + nodeId + ": findMin method...");

		int minIndex = findMinimumWeightEdge();
		if(minIndex>=0 && minIndex < numOfNeighbors){
			testNode = neighbors[minIndex][0];
			sendTestMessage(testNode, this.level, this.fragmentId);
		}else{
			testNode = -1;
			report();
		}
	}

	private void processTestMessage(Message msg){

		if(Parameters.DEBUG1)
			System.out.println("Node " + nodeId + ": processTestMessage method...");

		int q = msg.sender;
		int qIndex = neighborsIndex.get(q);

		if( ((TestMessage) msg).level > this.level){
			//wait
			//place message at end of queue
			waitManager(msg);
		}else if( this.fragmentId == ((TestMessage) msg).fragmentId ){
			//node is in same fragment so to prevent cycles this edge has to be rejected
			if(neighbors[qIndex][2] == Status.BASIC.ordinal()){
				neighbors[qIndex][2] = Status.REJECT.ordinal();
			}
			
			if(msg.sender != testNode){
				//send reject message to q
				sendRejectMessage(msg.sender);
				
			}else{
				//if q == testNode then q will mark its edge to me as reject so we need not worry
				//about it
				findMin();
				
			}
			
		}else{
			//LEQ rule holds so we send ACCEPT Message to q
			sendAcceptMessage(msg.sender);
			
		}
	
	}

	private void processAcceptMsg(Message msg){

		if(Parameters.DEBUG1){
			System.out.println("Node " + nodeId + ": processAcceptMsg method...");
		}

		this.testNode = -1; 
		int q = msg.sender;
		int qIndex = neighborsIndex.get(q);

		if(neighbors[qIndex][1] < bestWt){
			bestWt = neighbors[qIndex][1];
			bestNode = q;
		}

		report();
	}

	private void processRejectMsg(Message msg){

		if(Parameters.DEBUG1){
			System.out.println("Node " + nodeId + ": processRejectMsg method...");
		}

		int q = msg.sender;
		int qIndex = neighborsIndex.get(q);

		if(neighbors[qIndex][2] == Status.BASIC.ordinal()){
			neighbors[qIndex][2] = Status.REJECT.ordinal();
		}

		findMin();
	}

	private void report(){

		if(Parameters.DEBUG1){
			System.out.println("Node " + nodeId + ": report method...");
		}

		if(testNode == -1 ){
			if(rec == allChild()){
				this.state = NodeState.FOUND;
				sendReportMessage(this.parent, this.bestWt);
			}
		}

	}
	

	private void processsReportMsg(Message msg){

		if(Parameters.DEBUG1){
			System.out.println("Node " + nodeId + ": processsReportMsg method...");
		}

		int q = msg.sender;
		int qIndex = neighborsIndex.get(q);

		int w = ((ReportMessage)msg).bestWt;

		if( q != parent){
			if(w < this.bestWt){
				bestWt = w;
				bestNode = q;
			}
			rec = rec + 1;
			report();
		}else{
			if(this.state == NodeState.FIND){
				
				waitManager(msg);

			}else if (w > bestWt){
				System.out.println("node: "+ nodeId + " bestWt: " + bestWt + " w: " + w + " bestNode : " + bestNode);
				changeRoot();
			}else if(w == Integer.MAX_VALUE && bestWt == Integer.MAX_VALUE){
				stop();
			}
		}

	}

	private void changeRoot(){

		if(Parameters.DEBUG1){
			System.out.println("Node " + nodeId + ": changeRoot method...");
		}
		int bestNodeIndex;
		try{
			bestNodeIndex = neighborsIndex.get(bestNode);
			if (neighbors[bestNodeIndex][2] == Status.BRANCH.ordinal()){
			sendChangeRootMessage(bestNode);
			}else{
				neighbors[bestNodeIndex][2] = Status.BRANCH.ordinal();
				sendConnectMessage(bestNode, this.level);
			}
		}catch(Exception e){
			System.out.println("node: "+ nodeId + " Exception: " + nodeId + " bestNode : " + bestNode);
		}
		
		
	}


	private void processChangeRootMsg(Message msg){

		if(Parameters.DEBUG1){
			System.out.println("Node " + nodeId + ": processChangeRootMsg method...");
		}

		changeRoot();
	}


	private int allChild(){
		int childs = 0;  //branched non-parent node
		for(int i =0; i < numOfNeighbors; i++ ){
			if (neighbors[i][2] == Status.BRANCH.ordinal()  && neighbors[i][0] != parent){
				childs++;
			}
		}
		return childs;
	}

	public void stop(){
		System.out.println(nodeId + " Completed Algorithm. Now stopping");
		Main.areWeDone = true;
		System.out.println(nodeId + " We are Done set to True.");
		//notifyAll();
	}
	

	private void sendConnectMessage(int qNodeid, int myLevel){
		
		Message msg = new ConnectMessage(this.nodeId, qNodeid, myLevel);
		sendMessage(msg);
	}

	private void sendInitiateMessage(int qNodeid, int level, int fragmentId, NodeState state){

		Message msg = new InitiateMessage(this.nodeId,  qNodeid, level, fragmentId, state);
		sendMessage(msg);
	}

	private void sendTestMessage(int qNodeid, int level, int fragmentId){
		Message msg = new TestMessage(this.nodeId, qNodeid, level, fragmentId);
		sendMessage(msg);
	}

	private void sendReportMessage(int qNodeid, int bestWt){
		Message msg = new ReportMessage(this.nodeId, qNodeid, bestWt);
		sendMessage(msg);
	}
	private void sendAcceptMessage(int qNodeid){
		Message msg = new AcceptMessage(this.nodeId, qNodeid);
		sendMessage(msg);
	}
	private void sendRejectMessage(int qNodeid){
		Message msg = new RejectMessage(this.nodeId, qNodeid);
		sendMessage(msg);
	}

	private void sendChangeRootMessage(int qNodeid){
		Message msg = new ChangeRootMessage(this.nodeId, qNodeid);
		sendMessage(msg);
	}


	private void sendMessage(Message msg){
		int receipient = msg.receipent;
		//System.out.println("Sending Message of " + nodeId + " "+ receipient);
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
					//System.out.println("Message Queue of Node " + nodeId + " is empty.");
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


//To Do: append the find min wieght function to include report if it doesn;t find the least weight edge 
//2. Write in detail the test class
