
/*
 *  Shadow.java
 *
 *  Created on: 06 Jul 2016
 *      Author: L
 */

public class Shadow {
	private String username;
	private String password;
	private int clearance;
	
	Shadow(String username, String password, int clearance) {
		this.username = username;
		this.password = password;
		this.clearance = clearance;
	}
	
	public String get_username() {
		return this.username;
	}
	
	public String get_password() {
		return this.password;
	}
	
	public String get_clearance() {
		return Integer.toString(this.clearance);
	}

	public void set_username(String username) {
		this.username = username;
	}
	
	public void set_password(String password) {
		this.password = password;
	}
	
	public void set_clearance(int clearance) {
		this.clearance = clearance;
	}
	
	public String toString() {
		return get_username() + ":" + get_password() + ":" + get_clearance();
	}
}
