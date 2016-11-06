
public class Message {

	public static void error(int type) {
		if (type == 0)
			System.out.println("*** File Extension not Recognized! ***");
		
		else if (type == 1)
			System.out.println("*** Program is unable to process more than 20 Events! ***");
		
		else if (type == 2)
			System.out.println("*** Number of monitored events do not tally with number of Events! ***");

		else if (type == 3)
			System.out.println("*** Number of monitored events do not tally with number of Days! ***");
		
		else if (type == 4)
			System.out.println("*** Number of monitored events do not tally with number of Test Events! ***");
		
		else if (type == 5)
			System.out.println("*** Loading files went wrong! ***");

		else if (type == 9)
			System.out.println("*** Please input the appropriate arguments! (Events.txt Base-Data.txt Test-Events.txt) ***");
	}
	
	public static void file_error(int type) {
		if (type == 0)
			System.out.println("*** Unable to Open File! ***");
	}
	
}
