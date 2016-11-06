package Utility;

import java.security.MessageDigest;

public class MD5 {
	public static String hash(String password) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(password.getBytes());
		
		byte data[] = digest.digest();
		
		StringBuffer hex_buffer = new StringBuffer();
		
		for (int i = 0; i < data.length; i++) {
			String hex_string = Integer.toHexString(0xff & data[i]);
			
			if (hex_string.length() == 1)
				hex_buffer.append('0');
			
			hex_buffer.append(hex_string);
		}
		
		return hex_buffer.toString();
	}	
}
