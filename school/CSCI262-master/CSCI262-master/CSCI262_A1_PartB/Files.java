
/*
 *  Files.java
 *
 *  Created on: 06 Jul 2016
 *      Author: L
 */

public class Files implements Comparable<Files> {

	private String filename;
	private String username;
	private int security_level;
	
	public Files(String filename, String username, int security_level) {
		this.filename = filename;
		this.username = username;
		this.security_level = security_level;
	}
	
	public String get_filename() {
		return this.filename;
	}
	
	public String get_username() {
		return this.username;
	}
	
	public int get_security_level() {
		return this.security_level;
	}
	
	public void set_username(String username) {
		this.username = username;
	}
	
	public void set_filename(String filename) {
		this.filename = filename;
	}
	
	public void set_security_level(int security_level) {
		this.security_level = security_level;
	}
	
	public String toString() {
		return (filename + "\t" + username + "\t" + Integer.toString(security_level));
	}
	
	public String toFileString() {
		return (filename + ":" + username + ":" + Integer.toString(security_level));
	}
	
	 @Override
	 public int compareTo(Files other) {
		 int compare = this.filename.compareTo(other.filename);
		 return compare == 0 ? this.filename.compareTo(other.filename) : compare;
	 }
}
