
/*
 *  Salt.java
 *
 *  Created on: 06 Jul 2016
 *      Author: L
 */

public class Salt {
	
	private String username;
	private int salt;

	Salt(String username, int salt) {
		this.username = username;
		this.salt = salt;
	}
	
	public String get_username() {
		return this.username;
	}
	
	public int get_salt() {
		return this.salt;
	}
	
	public void set_username(String username) {
		this.username = username;
	}
	
	public void set_salt(int salt) {
		this.salt = salt;
	}
	
	public String toString() {
		return get_username() + ":" + get_salt();
	}
}
