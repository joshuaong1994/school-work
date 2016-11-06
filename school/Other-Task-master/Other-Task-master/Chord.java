import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class Chord {
	// buffered writer for small writes (efficiency)
	private static BufferedWriter output;

	// sort nodes within ring automatically (node 0 -> node 1 -> ... node m);
	// reduces number of code lines for treemap (auto sort & more readable)
	private static TreeMap<Long, Node> Ring;

	// initialize finger table size. Size can be null at first;
	private static Long finger_table_size;

	// N will store ring size where N is 2^N
	private static long N;

	// for initialization/comparative purposes on Long objects
	private static long NULL = 0L;
	
	public static void main(String[] args) {
		String file_name = "";
		
		// get input from command line
		try {
			if (args.length != 1) {		// check if there are any arguments entered
				// System.out.println("ERROR: Missing Filename\n\n");
			}	
			
			else
				file_name = args[0];// store first argument as filename if there is an argument
			
			// establish output buffer writer
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out), "ASCII"), 512);

			output.write("\n\n");
			read(file_name);		// read the file once everything is done
			output.write("\n\n");

			output.close();			// close buffer after it is done
		} catch (Exception ex) {	// capture any error from the buffer writer
			// System.out.println("ERROR: Unable to Initialize BufferedWriter\n\n");
		}
	}
	
	private static void read(String file_name) {
		String str_instruction = "";
		int int_instruction = 0;

		try {
			// establish buffer reader for file to read each lines in file
			BufferedReader file = new BufferedReader(new FileReader(new File(file_name)));
			
			// capture each line and individual words within the line
			String line = file.readLine();
			String[] data_portion;			
			
			// check if line in file exist still
			while (line != null) {
				data_portion = line.split(" ");

				// avoid any white spaces in data_portion if there are any
				str_instruction = data_portion[0].replaceAll("\\s+","");

				// store str_instruction in int form (running from java 1.7 and below)
				if (str_instruction.equals("initchord"))
					int_instruction = 1;

				else if (str_instruction.equals("addpeer"))
					int_instruction = 2;

				else if (str_instruction.equals("removepeer"))
					int_instruction = 3;

				else if (str_instruction.equals("insert"))
					int_instruction = 4;

				else if (str_instruction.equals("delete"))
					int_instruction = 5;

				else
					int_instruction = 6;

				// go to appropriate function according to the instructions
				switch (int_instruction) {
					case 1: initChord(Long.parseLong(data_portion[1]));
						break;
						
					case 2: addPeer(Long.parseLong(data_portion[1]));
						break;
					
					case 3: removePeer(Long.parseLong(data_portion[1]));
						break;
					
					case 4:
						stabilizeRing();
						String tmpI = "";
						for (int i = 2; i < data_portion.length; i++) {
							tmpI += data_portion[i] + " ";
						}
						
						insert(Long.parseLong(data_portion[1]), tmpI);
						break;
						
					case 5:
						stabilizeRing();
						String tmpD = "";
						for (int i = 2; i < data_portion.length; i++) {
							tmpD += data_portion[i] + " ";
						}
						
						delete(Long.parseLong(data_portion[1]), tmpD);
						break;
						
					case 6: print(Long.parseLong(data_portion[1]));
						break;
						
					default:
						break;
				}
				
				// go on to the next line
				line = file.readLine();
			}

			file.close();			// close file after all instructions are done; finish reading file
		} catch (Exception ex) {	// capture any errors from the outputs from functions
			// System.out.println("ERROR: Unable to Read File\n\n");
		}
	}
	
	private static void initChord(long n) throws IOException {
		// create ring upon initializing the chord
		Ring = new TreeMap<Long, Node>();

		// set finger table size
		finger_table_size = getFingerTableSize(n);
		N = n;
		
		// create empty node first and set it's predessor as NULL
		Node node = new Node(NULL, finger_table_size);
		node.setPredecessor(NULL);

		// store node into ring
		Ring.put(NULL, node);
	}
	
	private static void addPeer(Long key) throws IOException {

		// add node only if ring does not contin the key
		if (!Ring.containsKey(key)) {

			// initialize node and store it inside the ring
			// Ring -> (Key, Node Obj)
			Node node = new Node(key, finger_table_size);
			Ring.put(key, node);

			findKey(key, key);

			// and the node's successor(node next to it) is not empty
			// if the node's predecessor(node before it) is not empty
			// add node into ring and update the entire ring

			// if the nodes infront of it and behind it are no empty
			if (Ring.higherEntry(key) != null && Ring.lowerEntry(key).getValue() != null) {

				// create document table for the node (to store data in a form of string etc)
				HashMap<String, Long> document_table;

				// get node's corresponding front and back node
				Node predecessor = Ring.lowerEntry(key).getValue();
				Node successor = Ring.higherEntry(key).getValue();
				
				// retrieve successor data table
				// this is because if the successor's document table is not empty,
				// and that the ring is not thoroughly empty
				// take the key list within successors document table and
				// and update tables on both side (node itself, and successor node)
				document_table = successor.getDocumentTable();
				
				if (!Ring.isEmpty() && !document_table.isEmpty() && successor != Ring.get(key)) {

					// update ring upon each join or leave if there are any 
					// for efficiency performance
					stabilizeRing();
					
					LinkedList<String> keyList = new LinkedList<String>();
					
					// update the node itself with successor's document table data
					for (Map.Entry<String, Long> entry : document_table.entrySet()) {
						if (predecessor.keyInRange(entry.getValue(), Ring.get(key))) {
							insert(key, entry.getKey());
							keyList.add(entry.getKey());
						}
					}
					
					// update successor's docment table
					for (int i = 0; i < keyList.size(); i++) {
						Ring.get(successor.getNodeID()).deleteDocument(keyList.get(i));
					}
				}
			}

			output.write("\nPEER " + key + " INSERTED");
		}

		else
			return;
	}
	
	private static void removePeer(Long key) throws IOException {

		// only if ring contains the key
		// it is allowed to remove the node with the given key
		if (Ring.containsKey(key)) {

			// collect the key's document data
			HashMap<String, Long> document_table;
			document_table = Ring.get(key).getDocumentTable();
			
			findKey(key, key);

			Ring.remove(key);
				
			// if the ring size and document table are not empty
			// update the ring after the deletion of node key
			if (Ring.size() != 0 && document_table.size() != 0) {

				// update ring upon each join or leave if there are any 
				// for efficiency performance
				stabilizeRing();

				// iterate through the deleted node's document table
				Iterator<Entry<String, Long>> dt_it = document_table.entrySet().iterator();

				while (dt_it.hasNext()) {
					
					// capture each row of the deleted key's document table data
					Entry<String, Long> row = dt_it.next();

					// if successor is empty, store the entry of document table
					// into the first node within the ring
					if (Ring.higherKey(key) == null) {
						Ring.firstEntry().getValue().insertDocument(row.getValue(), row.getKey());
					}

					// if successor is not empty, insert the entry of document table
					// into the successor itself
					else {
						Ring.get(Ring.higherKey(key)).insertDocument(row.getValue(), row.getKey());
					}

					// remove the object through the iterator
					dt_it.remove();
				}
			}
			
			if (Ring.size() != 0) {
				output.write("\nPEER " + key + " REMOVED");
			}
		
			else {
				return;
			}
		}

		else {
			return;
		}
	}
	
	private static void insert(Long node_id, String data) throws IOException {

		// insert a document ONLY if ring contains nodes
		if (Ring.size() != 0) {

			// capture hashed key for data document (consistent hashing)
			// and also capture the node's key
			Long hashed_key = (Hash(data) & (N));
			Long node_key = findKey(node_id, hashed_key);

			output.write("\nINSERT " + data + "(key=" + hashed_key + ") AT " + node_id);

			// insert the document into the node itself via the
			// captured node key from findKey() function
			Ring.get(node_key).insertDocument(hashed_key, data);
		}

		else {
			return;
		}
	}
	
	private static void delete(Long node_id, String data) throws IOException {
		
		// remove a document ONLY if ring contains nodes
		if (Ring.size() != 0) {

			// capture hashed key for data document (consistent hashing)
			// and also capture the node's key
			Long hashed_key = (Hash(data) & (N));
			Long node_key = findKey(node_id, hashed_key);

			output.write("\nREMOVED " + data + "(key=" + hashed_key + ") FROM " + node_id);

			// remove the document from the node itself via the
			// captured node key from findKey() function
			Ring.get(node_key).deleteDocument(data);
		}

		else {
			return;
		}	
	}
	
	private static void print(Long key) throws IOException {
		
		// capture the very first key and also the node key itself
		Long first_key = Ring.firstKey();
		Long node_key = findKey(first_key, key);

		// if the node key returns a value other than NULL
		// check if the ring contains the key and print
		// document data and finger table accordinly
		if (node_key != NULL) {
			if (Ring.containsKey(key)) {
				Ring.get(key).printDocumentTable(output);
				Ring.get(key).printFingerTable(output);
			}
		}
	}
	
	private static Long findKey(Long node_key, Long node_id) throws IOException {
		boolean found = false;
		Long node_successor;

		// if the ring doesnt contain the key set it as NULL
		if (!Ring.containsKey(node_key))
			node_key = NULL;

		//print first node route
		output.write("\n" + node_key);

		// if both key and id are the same, return the key
		if (node_key == node_id) {
			return node_key;
		}

		else {

			// capture the successor finger table and get the node obj
			// from the given node key
			Node curr_node = Ring.get(node_key);
			node_successor = curr_node.getFingerTable(node_id);

			do {

				// if node's id == successer
				if (node_id == node_successor) {
					output.write(">" + node_successor);

					return node_successor;
				}

				if (curr_node.getNodeID() == node_successor) {
					if (curr_node.getNodeID() == node_key) {
						return NULL;
					}

					output.write(">" + curr_node.getNodeID());

					return curr_node.getNodeID();
				}

				output.write(">" + node_successor);

				// check if node is in range
				if (curr_node.keyInRange(node_id, Ring.get(node_successor))) {
					return node_successor;
				}

				// update 
				curr_node = Ring.get(node_successor);
				node_successor = curr_node.getFingerTable(node_id);
			} while (!found);

			return curr_node.getNodeID();
		}
	}
	
	private static Long Hash(String data) {

		// psuedo code from assignment requirement
		long key = 0;
		
		for (int i = 0; i < data.length(); i++) {
			key = ((key << 5) + key) ^ data.charAt(i);
		}
		
		return key;
	}
	
	private static Long getFingerTableSize(long n) {
		
		// capture the maximum size first
		Long max = 2147483648L; // 2^31

		for (Long size = NULL; ; size++) {
			Long tmp = 1L << size;
			
			if (tmp >= max) {
				return tmp;
			}

			else if (tmp >= n) {
				return size;
			}
		}
	}
 
	private static void stabilizeRing() {
		
		// update the ring only if there are nodes
		// on the ring
		if (Ring.size() != 0) {
			
			// capture the keys (nodes) from the ring and
			// store it in a tmp area first
			SortedSet<Long> keys = new TreeSet<Long>(Ring.keySet());
			
			// loop through the keys inside the ring
			// and update the predecessors and finger table as well
			for (Long key : keys) {				
				Ring.get(key).setPredecessor(Ring.lowerKey(key));
				Ring.get(key).stabilizeFingerTable(Ring, N);	
			}
		}
	}

}
