class Test{
	public static void main(String[] args){
		Status e = Status.BASIC;
		MessageType m = MessageType.INITIATE;
		if(e == Status.BRANCH)
			System.out.println("Branch");
		else if(e == Status.BASIC)
			System.out.println("Basic");
		if(m == MessageType.INITIATE)
			System.out.println("Intitate");
		else if(m == MessageType.CONNECT)
			System.out.println("Connect");
		else
			System.out.println("Nothing");

		System.out.println("value: " +  e.ordinal());
		System.out.println("value: " +  m.ordinal());
	}
}