
/*
 *  UserPath.java
 *
 *  Created on: 06 Jul 2016
 *      Author: L
 */

import Utility.Keyboard;
import Utility.Message;

public class UserPath {

	private Shadow user;
	
	public UserPath(Shadow user) {
		this.user = user;
	}
	
	public void init() {
		char option;
		FileScript fs = new FileScript();

		do {
			option = Keyboard.readChar("Options: (C)reate, (R)ead, (W)rite, (L)ist, (S)ave, (E)xit: ");

			switch (Character.toLowerCase(option)) {
			case 'c':
				System.out.println("\n*** (C)reate Option ***\n---------------------------");
				fs.create_file(user);
				break;

			case 'r':
				System.out.println("\n*** (R)ead Option ***\n---------------------------");
				fs.read_file(user);
				break;
				
			case 'a':
				System.out.println("\n*** (A)ppend Option ***\n---------------------------");
				fs.append_file(user);
				break;
				
			case 'l':
				System.out.println("\n*** (L)ist Option ***\n---------------------------");
				fs.list_file(user);
				break;
				
			case 's':
				System.out.println("\n*** (S)ave Option ***\n---------------------------");
				fs.save_file(user);
				break;
				
			case 'e':
				System.out.println("\n*** (E)xit Option ***\n---------------------------");
				fs.exit_acc(user);
				break;

			default:
				Message.error(5);
				break;
			}
		} while (Character.toLowerCase(option) != 'e' );
	}
}
