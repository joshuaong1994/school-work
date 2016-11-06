import java.util.*;
import java.io.*;
import java.net.*;
import java.security.*;

public class Client {

	private static final int BIT160 = 40;
	private static final int BYTE_SIZE = 4096;
	private static final int PORT_NUMBER = 6666;

	private static DatagramSocket client_socket;
	private static DatagramPacket incoming_packet;
	private static DatagramPacket outgoing_packet;

	private static byte[] incoming_buffer;
	private static byte[] outgoing_buffer;
	// private static byte[] message;

	private static String shared_sess_key;
	
	public static void main(String[] args) {
		System.out.print("\033[H\033[2J");
		Scanner file = null;

		String client_NB = "";
		String host_NA = "";

		String password = "";
		String hashed_password = "";

		String confirmation = "";

		byte[] tmp_enc_pw;

		// read password from pw.dat
		try {
			file = new Scanner(new File("vault/pw.dat"));
			password = file.nextLine();
			file.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to detect password for user.\nTerminating Session");
			System.exit(1);
		}

		// capture client's hashed password
		hashed_password = hash(password);
		RC4 rc4 = new RC4(hashed_password.getBytes());

		try {
			System.out.println("Requesting connection to Host...");

			String request_display = "Requesting Connection";
			
			client_socket = new DatagramSocket();
			InetAddress ip_address = InetAddress.getByName("localhost");

			// Client sends a connection request to host
			outgoing_buffer = request_display.getBytes();
			outgoing_packet = new DatagramPacket(outgoing_buffer, 0, outgoing_buffer.length, ip_address, PORT_NUMBER);
			client_socket.send(outgoing_packet);

			// Client will listen to the port and get ready to receive
			// packet from host
			incoming_packet = new DatagramPacket(new byte[BYTE_SIZE], BYTE_SIZE);
			client_socket.receive(incoming_packet);
			incoming_buffer = incoming_packet.getData();

			// capture and decrypt host's NA and generate client's own NB
			host_NA = new String(rc4.decrypt(incoming_buffer), 0, incoming_packet.getLength());
			client_NB = randStringGen();
			
			// encrypt client's NB and send over to host
			tmp_enc_pw = rc4.encrypt(client_NB.getBytes());
			outgoing_packet = new DatagramPacket(tmp_enc_pw, 0, tmp_enc_pw.length, ip_address, PORT_NUMBER);
			client_socket.send(outgoing_packet);

			// compute the shared key K = H(NA||NB)
			shared_sess_key = computeSharedKey(client_NB, host_NA);

			// check if connection is established
			// host will send a confirmation
			client_socket.receive(incoming_packet);
			incoming_buffer = incoming_packet.getData();

			confirmation = new String(rc4.decrypt(incoming_buffer), 0, incoming_packet.getLength());

			if (confirmation.equalsIgnoreCase("established")) {
				System.out.println(confirmation + " Connection" + "\n");

				while (true) {
					String reply = "";
					String message = "";

					// send out packet
					String input_message = Console.readString("Client B: ");
					outgoing_buffer = message_encryption(input_message, shared_sess_key, rc4);
					outgoing_packet = new DatagramPacket(outgoing_buffer, 0, outgoing_buffer.length, ip_address, PORT_NUMBER);
					client_socket.send(outgoing_packet);

					if (input_message.equalsIgnoreCase("exit")) {
						client_socket.close();
						System.out.println("Terminating System.");
						System.exit(1);
					}

					// receive packet
					client_socket.receive(incoming_packet);
					reply = new String(rc4.decrypt(incoming_packet.getData()), 0, incoming_packet.getLength());
					message = message_decryption(reply, shared_sess_key);

					if (message.equalsIgnoreCase("exit")) {
						client_socket.close();
						System.out.println("Host has terminated conversation.\tTerminating now.");
						System.exit(1);
					}

					else
						System.out.println("Host A: " + message + "\n");
				}
			}

			else {
				client_socket.close();
				System.out.println("Unable to establish connection with Host.\nTerminating System.");
				System.exit(1);
			}

		} catch (Exception ex) {
			System.out.println("Exception occured: " + ex.toString());
			ex.printStackTrace();
			System.out.println("Terminating System.");
			System.exit(1);
		}
	}

	// SHA-1 -- H(string_input)
	public static String hash(String pw_string) {
		// initialized digest and buffer
		MessageDigest message_digest = null;
		StringBuffer buff = null;

		// catch error if message digest can't be initialized
		try {
			message_digest = MessageDigest.getInstance("SHA1");
			buff = new StringBuffer();
		} catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
		
		// capture input and store it byte array
		byte[] output = message_digest.digest(pw_string.getBytes());
		
		// store data in byte array into buffer
		for (int i = 0; i < output.length; i++)
			buff.append(Integer.toString((output[i] & 0xff) + 0x100, 16).substring(1));

		// return H(string_input) value
		return buff.toString();
	}
	
	// Bob will choose a random 160-bit random string (NB)
	// ** 160-bits long = 40 digits long
	private static String randStringGen() {
		StringBuilder builder = new StringBuilder();
		Random rand = new Random();
		
		// possible characters in the random string
		String candidate = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
		
		// store random string into builder
		for (int i = 0; i < BIT160; i++)
			builder.append(candidate.charAt(rand.nextInt(candidate.length())));
		
		return builder.toString();
	}

	public static String computeSharedKey(String client_NB, String host_NA) {
		String shared_sess_key = "";

		shared_sess_key = host_NA + client_NB;

		return hash(shared_sess_key);
	}

	// encrypt the messages within the conversation
	private static byte[] message_encryption(String input_message, String shared_sess_key, RC4 rc4_obj) {
		String hash_message = "";
		String delimiter = "-";
		String enc_msg_tmp;
		byte[] enc_message;

		hash_message = shared_sess_key + input_message + shared_sess_key;
		hash_message = hash(hash_message);

		enc_msg_tmp = input_message + delimiter + hash_message;

		enc_message = rc4_obj.encrypt(enc_msg_tmp.getBytes());

		return enc_message;
	}

	private static String message_decryption(String replied_message, String shared_sess_key) {
		String enc_message = replied_message;
		String hash_message = "";
		String plaintext_message = "";
		String hash_prime = "";

		String[] portions = enc_message.split("-");
		plaintext_message = portions[0];
		hash_message = portions[1];

		// compute h' to check against
		hash_prime = shared_sess_key + plaintext_message + shared_sess_key;
		hash_prime = hash(hash_prime);

		if (hash_message.equals(hash_prime)) 
			return plaintext_message;

		else
			return "Packet Unaccepted";
	}
}
