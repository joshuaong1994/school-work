
/*
 *  FileSystem.java
 *
 *  Created on: 06 Jul 2016
 *      Author: L
 */

import Utility.*;

public class FileSystem {

	private final static String test = "This is a test";

	public static void main(String[] args) {
		UserScript us = new UserScript();

		try {
			System.out.println("\n\nMD5('This is a test') = " + MD5.hash(test) + "\n\n");
		} catch (Exception ex) { System.out.println("*** Something went wrong ***"); }
		
		if (args.length == 0) {
			String username = Keyboard.readString("Username: ");
			String password = Keyboard.readString("Password: ");
			
			if (us.login_user(username, password)) {
				Message.success(4);
				UserPath path = new UserPath(us.get_user(username));
				path.init();
			}
			
			else {
				Message.error(4);
				return;
			}
		}

		else {
			if (args[0].equals("instruction"))	// show instruction
				Message.instruction();
			
			else if (args[0].equals("-i")) {	// create user
				if (us.create_user())
					Message.success(1);
			}

			else
				Message.error(0);
		}
	}
}
