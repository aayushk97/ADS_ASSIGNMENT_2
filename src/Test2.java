// Java program to illustrate
// referring to a subclass
// base class
class Bicycle
{
	// the Bicycle class has two fields
	public int gear;
	public int speed;
		
	// the Bicycle class has one constructor
	public Bicycle(int gear, int speed)
	{
		this.gear = gear;
		this.speed = speed;
	}
		
	// the Bicycle class has three methods
	public void applyBrake(int decrement)
	{
		speed -= decrement;
	}
		
	public void speedUp(int increment)
	{
		speed += increment;
	}
	
	// toString() method to print info of Bicycle
	public String toString()
	{
		return("No of gears are "+gear
				+"\n"
				+ "speed of bicycle is "+speed);
	}
}

// derived class
class MountainBike extends Bicycle
{
	
	// the MountainBike subclass adds one more field
	public int seatHeight;

	// the MountainBike subclass has one constructor
	public MountainBike(int gear,int speed,
						int startHeight)
	{
		// invoking base-class(Bicycle) constructor
		super(gear, speed);
		seatHeight = startHeight;
	}
		
	// the MountainBike subclass adds one more method
	public void setHeight(int newValue)
	{
		seatHeight = newValue;
	}
	
	// overriding toString() method
	// of Bicycle to print more info
	@Override
	public String toString()
	{
		
		return (super.toString()+
				"\nseat height is "+seatHeight);
	}
	
}

// driver class
public class Test2
{
	public static void main(String args[])
	{
		// using superclass reference
		// first approach
		Bicycle mb2 = new MountainBike(4, 200, 20);
		
		// using subclass reference( )
		// second approach
		MountainBike mb1 = new MountainBike(3, 100, 25);
		
		System.out.println("seat height of first bicycle is "
											+ mb1.seatHeight + "  "+ ((MountainBike)mb2).seatHeight);
			
		// In case of overridden methods
		// always subclass
		// method will be executed
		System.out.println(mb1.toString());
		System.out.println(mb2.toString());

		/* The following statement is invalid because Bicycle
		does not define a seatHeight.
		// System.out.println("seat height of second bicycle is "
												+ mb2.seatHeight); */
					
		/* The following statement is invalid because Bicycle
		does not define setHeight() method.
		mb2.setHeight(21);*/

	}
}
