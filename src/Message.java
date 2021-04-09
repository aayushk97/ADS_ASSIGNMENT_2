
class Message {
	MessageType type;
	int receipent;
	int sender;  //can be nodeId.
	
	public Message(MessageType type, int sender, int receiver){
		this.type =type;
		this.sender = sender;
		this.receipent = receiver;
	}
	
	public void send(){
		//Need to acquire lock...

	}
}

class ConnectMessage extends Message{
	public int level;
	public ConnectMessage(int sender, int receiver, int level){
		super(MessageType.CONNECT, sender, receiver);

		this.level = level;
	}
	

}

class InitiateMessage extends Message{
	int level;
	int fragmentId;
	NodeState state;

	public InitiateMessage(int sender, int receiver, int level, int fragmentId, NodeState state){
		super(MessageType.INITIATE, sender, receiver);
		this.level = level;
		this.fragmentId = fragmentId;
		this.state = state;
	}
}

class TestMessage extends Message{
	int level;
	int fragmentId;

	public TestMessage(int sender, int receiver, int level, int fragmentId){
		super(MessageType.TEST, sender, receiver);
		this.level = level;
		this.fragmentId = fragmentId;
	}
}
