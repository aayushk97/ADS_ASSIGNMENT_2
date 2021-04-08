class MsgType{
	static final int CONNECT = 0;
	static final int INITIATE = 1;
	static final int TEST = 2;
	static final int REJECT = 3;
	static final int ACCEPT = 4;
	static final int REPORT1 = 5;
	static final int REPORT2 = 6;
	static final int CHANGEROOT = 7;
}

class Message {
	int type;
	int receipent;
	int sender;  //can be nodeId.
	public Message(int type, int sender, int receiver){
		this.type =type;
		this.sender = sender;
		this.receipent = receiver;
	}
	
	// public void send(){
	// 	//Need to acquire lock...

	// }
}

class ConnectMessage extends Message{
	int level;
	public ConnectMessage(int sender, int receiver, int level){
		super(MsgType.CONNECT, sender, receiver);

		this.level = level;
	}
}

class InitiateMessage extends Message{
	int level;
	int fragmentName;
	int state;

	public InitiateMessage(int sender, int receiver, int level, int fragmentName, int state){
		super(MsgType.INITIATE, sender, receiver);
		this.level = level;
		this.fragmentName = fragmentName;
		this.state = state;
	}
}

class TestMessage extends Message{
	int level;
	int fragmentName;

	public TestMessage(int sender, int receiver, int level, int fragmentName){
		super(MsgType.TEST, sender, receiver);
		this.level = level;
		this.fragmentName = fragmentName;
	}
}