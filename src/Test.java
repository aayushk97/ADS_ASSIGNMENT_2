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


public void sort(final String field, List<ItemLocation> itemLocationList) {
    Collections.sort(itemLocationList, new Comparator<ItemLocation>() {
        @Override
        public int compare(ItemLocation o1, ItemLocation o2) {
            if(field.equals("icon")) {
                return o1.icon.compareTo(o2.icon);
            } if(field.equals("title")) {
                return o1.title.compareTo(o2.title);
            } else if(field.equals("message")) {
                return o1.message.compareTo(o2.message);
            } 
            // .
            // . fill in the rest of the fields...
            // .
            else if(field.equals("locSeen")) {
                return o1.locSeen.compareTo(o2.locSeen);
            } 
        }           
    });
}