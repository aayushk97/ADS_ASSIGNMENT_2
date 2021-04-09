
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
	int level;
	public ConnectMessage(int sender, int receiver, int level){
		super(MessageType.CONNECT, sender, receiver);

		this.level = level;
	}
}

class InitiateMessage extends Message{
	int level;
	int fragmentName;
	int state;

	public InitiateMessage(int sender, int receiver, int level, int fragmentName, int state){
		super(MessageType.INITIATE, sender, receiver);
		this.level = level;
		this.fragmentName = fragmentName;
		this.state = state;
	}
}

class TestMessage extends Message{
	int level;
	int fragmentName;

	public TestMessage(int sender, int receiver, int level, int fragmentName){
		super(MessageType.TEST, sender, receiver);
		this.level = level;
		this.fragmentName = fragmentName;
	}
}
