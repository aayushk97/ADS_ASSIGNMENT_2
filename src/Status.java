 enum Status
 {
 	//For edge status
	BASIC, BRANCH, REJECT;
}

enum MessageType{
	//message type
	CONNECT, INITIATE, TEST, REJECT, ACCEPT, REPORT, CHANGEROOT;
}

enum NodeState{
	
	SLEEP, FIND, FOUND;
}
