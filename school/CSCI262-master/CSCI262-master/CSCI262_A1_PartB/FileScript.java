
/*
 *  FileScript.java
 *
 *  Created on: 06 Jul 2016
 *      Author: L
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Vector;

import Utility.Keyboard;
import Utility.Message;

public class FileScript{
	
	private final static String file_store = "Files.store";
	private static Vector<Files> file_list = new Vector<Files>();
	private static Vector<Files> tmp_file_list = new Vector<Files>();
	
	public FileScript() {
		load_file();
	}
	
	protected static void create_file(Shadow user) {
		boolean create_state = false;
		String filename = Keyboard.readString("Filename: ");
		int sec_level;
		
		if (!file_exist(filename)) {
			do {
				sec_level = Keyboard.readInt("Security Level (0 or 1 or 2 or 3): ");
				
				if (check_level(sec_level))
					create_state = true;
				
				else
					Message.error(3);
			} while (!create_state);
			
			if (create_state) {
				boolean save_changes = false;
				Files tmp = new Files(filename, user.get_username(), sec_level);
				
				do {
					char save_option = Keyboard.readChar("Do you want to permanently save file creation? (Y/N): ");
					
					if (Character.toLowerCase(save_option) == 'y') {
						file_list.add(tmp);
						update_changes(tmp);
						Message.success(5);
						save_changes = true;
					}
					
					else if (Character.toLowerCase(save_option) == 'n') {
						tmp_file_list.add(tmp);
						save_changes = true;
					}
				} while (!save_changes);
			}
		}
		
		else 
			Message.error(6);
	}
	
	protected static void read_file(Shadow user) {
		String filename = Keyboard.readString("File name to Read: ");
		
		if (file_exist(filename)) {
			System.out.println("File " + filename + " found!");
			System.out.println("Authenticating for Access Rights ...");
			
			if (check_access_read(filename, user))
				System.out.println("Read File");

			else
				Message.error(5);
		}
		
		else
			Message.error(7);
	}
	
	protected static void append_file(Shadow user) {
		String filename = Keyboard.readString("File name to Write: ");

		if (file_exist(filename)) {
			System.out.println("File " + filename + " found!");
			System.out.println("Authenticating for Access Rights ...");
			
			if (check_access_append(filename, user))
				System.out.println("Written to File");

			else
				Message.error(5);
		}
		
		else
			Message.error(7);
	}
	
	protected static void list_file(Shadow user) {
		Collections.sort(file_list);
		
		for (Files file : file_list)
			System.out.println(file.toString());
	}
	
	protected static void save_file(Shadow user) {
		if (tmp_file_list.size() != 0) {
			Collections.copy(file_list, tmp_file_list);
			Collections.sort(file_list);
			update_all_changes(file_list);

			tmp_file_list.clear();
		}

		else
			System.out.println("*** There is nothing to save! ***\n\n");
	}
	
	protected static void exit_acc(Shadow user) {
		if (tmp_file_list.size() != 0) {
			char save_option = Keyboard.readChar("You have unsaved data. Do you want to save it? (Y/N): ");

			if (Character.toLowerCase(save_option) == 'y') {
				file_list.addAll(tmp_file_list);

				Collections.sort(file_list);
				update_all_changes(file_list);

				tmp_file_list.clear();
			}

			else
				System.exit(0);
		}

		else
			System.out.println("*** Exiting... ***\n\n");
	}
	
	// load file data onto vector
	private static void load_file() {
		try {
	    	File file = new File(file_store);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				String[] part = line.split(":");
				file_list.add(new Files(part[0], part[1], Integer.parseInt(part[2])));
			}

			fileReader.close();
		} catch (IOException e) { Message.file_error(3); }
	}
	
	// check if filename already exist
	private static boolean file_exist(String filename) {
		for (Files file : file_list) {
			if (filename.equals(file.get_filename()))
				return true;
		}
		
		return false;
	}
	
	// check for user input for file level
	private static boolean check_level(int sec_level) {
		if (sec_level >= 0 && sec_level <= 3)
			return true;
		
		return false;
	}
	
	// check for file security level and user access rights (clearance)
	private static boolean check_access_read(String filename, Shadow user) {
		if (get_file_obj(filename).get_security_level() <= Integer.parseInt(user.get_clearance()))
			return true;
		
		else
			return false;
	}
	
	// check for file security level and user access rights (clearance)
	private static boolean check_access_append(String filename, Shadow user) {
		if (get_file_obj(filename).get_security_level() >= Integer.parseInt(user.get_clearance()))
			return true;
		
		else
			return false;
	}

	// get file object
	private static Files get_file_obj(String filename) {
		for (Files file : file_list) {
			if (filename.equals(file.get_filename()))
				return file;
		}
		
		return null;
	}
	
	// update file when a change is done
	private static void update_changes(Files file_obj) {
		try {
			PrintWriter output_file = new PrintWriter(new BufferedWriter(new FileWriter(file_store, true)));
			output_file.println(file_obj.toFileString());
			output_file.write("\n");
			output_file.close();
		} catch (IOException e) { Message.file_error(4); }	
	}
	
	// update file when user wants to save
	private static void update_all_changes(Vector<Files> file_vector) {
		try {
			PrintWriter output_file = new PrintWriter(new BufferedWriter(new FileWriter(file_store, false)));

			for (Files file : file_vector) {
				output_file.write(file.toFileString());
				output_file.write("\n");
			}
			
			output_file.close();
		} catch (IOException e) { Message.file_error(4); }
	}
}
