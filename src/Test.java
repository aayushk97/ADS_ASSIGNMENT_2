class Test{
	public static void main(String[] args){
		Status e = Status.BASIC;
		MessageType1 m = MessageType1.INITIATE;
		if(e == Status.BRANCH)
			System.out.println("Branch");
		else if(e == Status.BASIC)
			System.out.println("Basic");
		if(m == MessageType1.INITIATE)
			System.out.println("Intitate");
		else if(m == MessageType1.CONNECT)
			System.out.println("Connect");
		else
			System.out.println("Nothing");

		System.out.println("value: " +  e.ordinal());
		System.out.println("value: " +  m.ordinal());
	}
}