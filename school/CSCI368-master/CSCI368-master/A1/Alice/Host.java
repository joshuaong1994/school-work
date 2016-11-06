import java.util.*;
import java.io.*;
import java.net.*;
import java.security.*;

public class Host {

	private static final int BIT160 = 40;
	private static final int BYTE_SIZE = 4096;
	private static final int PORT_NUMBER = 6666;

	private static DatagramSocket host_socket;
	private static DatagramPacket incoming_packet;
	private static DatagramPacket outgoing_packet;

	// private static byte[] incoming_buffer;
	private static byte[] outgoing_buffer;
	// private static byte[] message;

	private static String shared_sess_key;

	public static void main(String[] args) {
		System.out.print("\033[H\033[2J");
		Scanner file = null;

		String host_NA = "";
		String client_NB = "";

		String password = "";
		String hashed_password = "";

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

		// capture host's hashed password
		hashed_password = hash(password);
		RC4 rc4 = new RC4(hashed_password.getBytes());

		while (true) {
			System.out.println("Waiting for new connection...");

			try {
				host_socket = new DatagramSocket(PORT_NUMBER);
				incoming_packet = new DatagramPacket(new byte[BYTE_SIZE], BYTE_SIZE);

				// host receives a connection request from client
				host_socket.receive(incoming_packet);
				String temp_message = new String(incoming_packet.getData(), 0, incoming_packet.getLength());
				System.out.println(incoming_packet.getAddress() + ", " + incoming_packet.getPort() + " - Client: " + temp_message);

				// generate NA and send it over to client
				host_NA = randStringGen();
				tmp_enc_pw = rc4.encrypt(host_NA.getBytes());
				outgoing_packet = new DatagramPacket(tmp_enc_pw, 0, tmp_enc_pw.length, incoming_packet.getAddress(), incoming_packet.getPort());
				host_socket.send(outgoing_packet);

				// get ready to get client's NB and compute the shared key
				host_socket.receive(incoming_packet);
				client_NB = new String(rc4.decrypt(incoming_packet.getData()), 0, incoming_packet.getLength());

				// compute shared session key for the connection
				shared_sess_key = computeSharedKey(client_NB, host_NA);

				outgoing_buffer = rc4.encrypt(new String("Established").getBytes());
				outgoing_packet = new DatagramPacket(outgoing_buffer, 0, outgoing_buffer.length, incoming_packet.getAddress(), incoming_packet.getPort());
				host_socket.send(outgoing_packet);

				System.out.println("Established connection with Client - IP: " + incoming_packet.getAddress() + ", Port: " + incoming_packet.getPort() + "\n");

				while (true) {
					String reply = "";
					String message = "";

					// send out packet
					String input_message = Console.readString("Host A: ");
					outgoing_buffer = message_encryption(input_message, shared_sess_key, rc4);
					outgoing_packet = new DatagramPacket(outgoing_buffer, 0, outgoing_buffer.length, incoming_packet.getAddress(), incoming_packet.getPort());
					host_socket.send(outgoing_packet);

					if (reply.equalsIgnoreCase("exit")) {
						host_socket.close();
						System.out.println("Client has exited.\nTerminating System.");
						System.exit(1);
					}

					// receive packet
					host_socket.receive(incoming_packet);
					reply = new String(rc4.decrypt(incoming_packet.getData()), 0, incoming_packet.getLength());
					message = message_decryption(reply, shared_sess_key);

					if (message.equalsIgnoreCase("exit")) {
						host_socket.close();
						System.out.println("Client has terminated conversation.\tTerminating now.");
						System.exit(1);
					}

					else
						System.out.println("Client B: " + message + "\n");					
				}
			} catch (Exception ex) {
				System.out.println("Exception occured: " + ex.toString());
				ex.printStackTrace();
				System.out.println("Terminating System.");
				System.exit(1);
			}
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

	// Alice will choose a random 160-bit random string (NB)
	// ** 160-bits long = 40 digits long
	private static String randStringGen() {
		StringBuilder builder = new StringBuilder();
		Random rand = new Random();
		
		// possible characters in the random string
		String candidate = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
		
		// store random string into builder
		for (int i = 0; i < BIT160; i++)
			builder.append(candidate.charAt(rand.nextInt(candidate.length())));
		
		// return random 160-bit string
		return builder.toString();
	}

	// comppute shared session key
	private static String computeSharedKey(String client_NB, String host_NA) {
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

	// decrypt the messages within the conversation
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
