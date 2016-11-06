
/*
 * For partial collision in a 32-bit form, the required number of "possible test"
 * is 77163 where there will be => 50% chance where a collision occurs. 
 * (Birthday paradox)
 * 
 * Source: http://preshing.com/20110504/hash-collision-probabilities/
 */

import java.security.*;
import java.util.*;
import java.util.Map.Entry;

public class CollisionFinder {
	private static HashMap<String, String> collision_map;
	
	private static final int BYTE_SIZE = 4;
	private static final int EXPECTED_TEST = 77163;
	private static final String LOGIN_UN = "llw661";
	
	private static long time_end = 0;
	private static long time_start = 0;
	
	private static Random baseRandom = new Random(0);
	
	public static void main(String[] args) {
		collision_map = new HashMap<String, String>();	/* store all possible hash values  */
		boolean collision_found = false;				/* when collision is found, boolean will turn true */
		
		time_start = System.currentTimeMillis();		/* start timer */
		
		for (;;) {										/* loop until a collision is found */
			if (capture_collision(collision_map)) {
				collision_found = true;					/* found a duplicate! YAY! */
				break;
			}
		}
		
		time_end = System.currentTimeMillis();					/* end timer */
		System.out.println("");
		print_results(collision_found, time_start, time_end);	/* print results */
	}
	
	/* normal SHA-1 */
	public static byte[] hash_SHA1(String text) {
		MessageDigest message_digest = null;
		try {
			message_digest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) { e.printStackTrace(); }

		byte[] digest_bytes = message_digest.digest(text.getBytes());

		digest_bytes = hash_SSHA1(digest_bytes);	/* ssha1 will modify the bytes to return only the first 4 bytes */
		return digest_bytes;
	}
	
	/* simplified SHA-1; SSHA-1 */
	public static byte[] hash_SSHA1(byte[] digest_bytes) {
		byte[] result = new byte[BYTE_SIZE];

		for (int i = 0; i < BYTE_SIZE; i++) {
			int remainder = i % BYTE_SIZE;		/* allocate the bytes accordingly */

			if (remainder == 0)
				result[0] = (byte) (result[0] ^ digest_bytes[i]);
			
			else if (remainder == 1)
				result[1] = (byte) (result[1] ^ digest_bytes[i]);
			
			else if (remainder == 2)
				result[2] = (byte) (result[2] ^ digest_bytes[i]);
				
			else
				result[3] = (byte) (result[3] ^ digest_bytes[i]);
		}

		return result;
	}
	
	/* prints result of collision */
	public static void print_results(boolean collision_found, long time_start, long time_end) {
		if (collision_found) {	/* if collision is found */
			System.out.println("*** Collision Found ***\n");
			System.out.println("Time Taken: " + (time_end - time_start) + " ms.");
		}

		else
			System.out.println("*** Collision Not Found ***\n");
	}

	/* produce random strings and capture potential collisions */
	public static boolean capture_collision(HashMap<String, String> collision_map) {
		collision_map.clear();	/* clear heap space for efficiency */
		
		String tmp_message = "";
		Random r = new Random(baseRandom.nextInt());
		int[] possible_integers = new int[EXPECTED_TEST];
		
		/* generate the possible values and store into collision_map */
		for (int i = 0; i < possible_integers.length; i++) {
			String rand_str = Integer.toString(r.nextInt());
			tmp_message = LOGIN_UN;
			tmp_message = tmp_message.concat(rand_str);
			
			collision_map.put(tmp_message, byte_toString(hash_SHA1(tmp_message)));
		}
		
		/* check if the sizes match, if it doesn't, there is 1 duplicate (proving the 50-50% chance of a collision) */
		Collection<String> valuesList = collision_map.values();
		Set<String> valuesSet = new HashSet<String>(collision_map.values());
		
		if (valuesList.size() != valuesSet.size()) {
			List<String> all_values = new LinkedList<String>();	/* capture all the values and place into list */
			all_values.addAll(valuesList);
			
			print_resulting_duplicates(all_values);	/* reason for not using collections is for efficiency purposes */
			
			return true;
		}
	
		return false;
	}
	
	/* capture duplicates and print it out as result */
	public static void print_resulting_duplicates(List<String> original_set) {
		Set<String> capture_duplicate = new HashSet<String>();
		Set<String> tmp_set = new HashSet<String>();
		
		for (String curr_str : original_set) {	/* capture duplicates and store */
			if (!tmp_set.add(curr_str))
				capture_duplicate.add(curr_str);
		}
		
		for (String get_value : capture_duplicate) {	/* values -> keys: many to one relationship*/
			int counter = 1;
			
			for (Entry<String, String> entry : collision_map.entrySet()) {	/* loop through hashmap and look for keys with the same value */
				if (entry.getValue().equals(get_value)) {
					System.out.println("Hash Value -> " + get_value + " | " + "Message " + counter + " -> " + entry.getKey());
					counter ++;
				}
			}
		}
	}
	

	/* change bytes into strings where necessary */
	public static String byte_toString(byte[] digest) {
		StringBuffer buff = new StringBuffer();;
		
		// store data in byte array into buffer
		for (int i = 0; i < digest.length; i++)
			buff.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));

		return buff.toString();
	}
}
