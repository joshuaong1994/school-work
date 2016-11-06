
/*
 *  UserScript.java
 *
 *  Created on: 06 Jul 2016
 *      Author: L
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.FileReader;

import Utility.*;

public class UserScript {

	private static final String shadow_file = "shadow.txt";
	private static final String salt_file = "salt.txt";
	
	private static final int size = 8;
	private static Random rnd = new Random();
	
	private static Vector<Shadow> system_vec = new Vector<Shadow>();
	private static Vector<Salt> user_vec = new Vector<Salt>();
	
	// preload required data
	protected UserScript() {
		load_shadow();
		load_salt();
	}

	// create user
	protected static boolean create_user() {
		boolean create_state = false;
		String username = Keyboard.readString("Username: ");
		String password = "";
		int clearance;

		if (!username_exist(username)) {
			do {
				password = Keyboard.readString("Password: ");
				String confirm = Keyboard.readString("Confirm Password: ");

				if (password.equals(confirm)) {
					if (check_password(confirm))
						create_state = true;

					else
						Message.error(2);
				}

				else
					Message.error(4);
			} while (!create_state);

			if (create_state) {
				boolean clearance_pass = false;
				
				do {
					clearance = Keyboard.readInt("\nUser Clearance (0 or 1 or 2 or 3): ");

					if (check_clearance(clearance))
						clearance_pass = true;

					else
						Message.error(3);
				} while (!clearance_pass);

				String salt = generate_salt();
				
				try {
					password = MD5.hash(password + salt);
				} catch (Exception e) { Message.error(9); }
				
				Shadow new_shadow = new Shadow(username, password, clearance);
				Salt new_salt = new Salt(username, Integer.parseInt(salt));
				
				system_vec.add(new_shadow);
				user_vec.add(new_salt);
				
				update_files(new_shadow, new_salt);
			}
		}
	
		else
			Message.error(1);

		return create_state;
	}

	// log into the system 
	protected static boolean login_user(String username, String password) {
		boolean login_state = false;
		
		for (Salt user : user_vec) {
			if (username.equals(user.get_username())) {
				System.out.println("\n" + username + " found in " + salt_file);
				System.out.println("salt retrieved: " + user.get_salt());
				
				try {
					System.out.println("\nhashing...");									
					password = MD5.hash(password + user.get_salt());
					System.out.println("hash value: " + password);	
				} catch (Exception e) { Message.error(9); }
				
				break;
			}
		}
		
		for (Shadow user : system_vec) {
			if (username.equals(user.get_username())) {
				if (password.equals(user.get_password())) {
					login_state = true;
					System.out.println("\nAuthentication for user " + username + " complete");
					System.out.println("Clearance for " + username + " is " + user.get_clearance());
				}
			}
		}
		
		return login_state;
	}
	
	// check if username already exist or not
	private static boolean username_exist(String username) {
		for (Salt user : user_vec) {
			if (username.equals(user.get_username()))
				return true;
		}
		
		return false;
	}

	// check user input upon acc creation for password	
	private static boolean check_password(String password) {
		boolean pass = false;
		
		if (password.length() >= 8) {
			boolean passUpper = false;
			boolean passLower = false;
			boolean passDigit = false;
			
			for (int i = 0; i < password.length(); i++) {
				if (Character.isUpperCase(password.charAt(i)) == true)
					passUpper = true;
				
				else if (Character.isLowerCase(password.charAt(i)) == true)
					passLower = true;
				
				else if (Character.isDigit(password.charAt(i)) == true)
					passDigit = true;
			}
			
			if (passUpper == true && passLower == true && passDigit == true)
				pass = true;
		}

		return pass;
	}
	
	// check user input upon acc creation for clearance
	private static boolean check_clearance(int clearance) {
		boolean pass = false;

		if (clearance >= 0 && clearance <= 3)
			pass = true;

		return pass;
	}

	// generate random number for password
	private static String generate_salt() {
	    StringBuilder builder = new StringBuilder(size);
	    for(int i = 0; i < size; i++)
	    	builder.append((char)('0' + rnd.nextInt(10)));
	    return builder.toString();
	}

	// get user object
	protected static Shadow get_user(String username) {
		for (Shadow user : system_vec) {
			if (username.equals(user.get_username()))
				return user;
		}
		
		return null;
	}
	
	// load shadow file data onto vector
	private void load_shadow() {
	    try {
	    	File file = new File(shadow_file);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				String[] part = line.split(":");
	    		system_vec.add(new Shadow(part[0], part[1], Integer.parseInt(part[2])));
			}

			fileReader.close();
		} catch (IOException e) { Message.file_error(0); }
	}
	
	// load salt file data onto vector
	private void load_salt() {
		try {
	    	File file = new File(salt_file);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				String[] part = line.split(":");
		    	user_vec.add(new Salt(part[0], Integer.parseInt(part[1])));
			}

			fileReader.close();
		} catch (IOException e) { Message.file_error(1); }
	}

	// update both shadow and salt file when user is created
	private static void update_files(Shadow new_shadow, Salt new_salt) {
		try {
			PrintWriter output_shadow = new PrintWriter(new BufferedWriter(new FileWriter(shadow_file, true)));
			output_shadow.println(new_shadow.toString());
			output_shadow.close();
			
			PrintWriter output_salt = new PrintWriter(new BufferedWriter(new FileWriter(salt_file, true)));
			output_salt.println(new_salt.toString());
			output_salt.close();
		} catch (IOException e) { Message.file_error(2); }	
	}
}
