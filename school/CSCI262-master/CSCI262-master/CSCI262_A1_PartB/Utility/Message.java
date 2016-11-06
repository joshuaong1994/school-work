package Utility;

public class Message {
	
	public static void instruction() {
		System.out.println("\n\nWelcome to CSCI262 A1 FileSystem!\n\n" + 
				"To use this program, here are the following things to do:\n" +
				"\t1. Create New Account: FileSystem -i" +
				"\n\t2. Login to Account  : FileSystem \n\n");
	}
	
	public static void file_error(int type) {
		String message = null;
		
		if (type == 0)	// shadow file
			message = "*** Unable to load system file (Shadow) ***\n\n";
		
		else if (type == 1)	// salt file
			message = "*** Unable to load client file (Salt) ***\n\n";
		
		else if (type == 2) // Unable to update file
			message = "*** Unable to update new user to data file! ***\n\n";
		
		else if (type == 3) // can't read file.store 
			message = "*** Unable to read file! ***";
		
		else if (type == 4) // can't update file.store 
			message = "*** Unable to update file! ***";

		else if (type == 5)
			message = "*** Unable to read file! ***";
		
		System.out.println(message);
	}
	
	public static void error(int type) {
		String message = null;
		
		if (type == 0)	// start up arguments
			message = "*** Arguments are invalid! ***\n\n";
		
		else if (type == 1)	// create user: username already exist
			message = "*** Username already exist! ***\n\n";
		
		else if (type == 2)	// create user: password does not meet requirement
			message = "*** Password Requirements are as follows: 8> characters, 1 Alpha, 1 Number, 1 Special Character ***\n\n";
		
		else if (type == 3)	// create user: user number not valid
			message = "*** Select valid user numbers! ***\n\n";
		
		else if (type == 4)	// create user: password does not match
			message = "*** Passwords does not match! ***\n\n";

		else if (type == 5) // login user: char option not valid
			message = "*** Option is not valid! ***\n\n";
		
		else if (type == 6)
			message = "*** File name already exists! ***";
		
		else if (type == 7)
			message = "*** File name does not exists! ***";

		else if (type == 9) 
			message = "*** Something went wrong! ***\n\n";
		
		System.out.println(message);
	}
	
	public static void success(int type) {
		String message = null;
		
		if (type == 1)	// create user: successful
			message = "*** User successfully created! ***\n\n";
		
		else if (type == 4)	// login user: successful
			message = "*** User log in successful!  ***\n\n";

		else if (type == 5)	//file successfully created
			message = "*** File has been successfully created  ***\n\n";
		
		System.out.println(message);
	}
}
