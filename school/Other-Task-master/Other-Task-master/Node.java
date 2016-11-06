
import java.util.TreeMap;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Iterator;


public class Node {
	private TreeMap<Long, Long> finger_table;
	private HashMap<String, Long> document_table;
	
	private Long node_id;
	private Long finger_table_size;
	
	private Long N;
	
	private Long predecessor;
	
	Node(Long node_id, Long finger_table_size) {
		// initialize empty finger table and also document table
		finger_table = new TreeMap<Long, Long>();
		document_table = new HashMap<String, Long>();
		
		this.node_id = node_id;
		this.finger_table_size = finger_table_size;
	}
	
	Long getPredecessor() {
		return predecessor;
	}

	void setPredecessor(Long predecessor) {
		this.predecessor = predecessor;
	}

	HashMap<String, Long> getDocumentTable() {
		return document_table;
	}

	Long getNodeID() {
		return node_id;
	}
	
	Long getFingerTable(Long node_id) {	
		if (node_id == this.node_id) {
			return this.node_id;
		}
		
		if (node_id < this.node_id) {
			if (node_id > predecessor) {
				return this.node_id;
			}
			
			else if (node_id == predecessor) {
				return predecessor;
			}
		}
		
		Iterator<Entry<Long, Long>> ft_it = finger_table.entrySet().iterator();
		Long lastValue = finger_table.lastEntry().getValue();
		
		while (ft_it.hasNext()) {
			Entry<Long, Long> row = ft_it.next();
			
			if (row.getKey() == node_id) {
				return row.getValue();
			}
			
			else if (row.getValue() == node_id) {
				return row.getValue();
			}
			
			else if (row.getKey() > node_id) {
				return lastValue;
			}
			
			lastValue = row.getValue();
			ft_it.remove();
		}
		
		return lastValue;
	}
	
	void insertDocument(Long id, String data) {
		document_table.put(data, id);
	}
	
	void deleteDocument(String data) {
		document_table.remove(data);
	}
	
	void printFingerTable(BufferedWriter output) throws IOException {
		Iterator<Entry<Long, Long>> ft_it = finger_table.entrySet().iterator();
		
		output.write("FINGER TABLE OF NODE " + node_id + ": \n");

		while (ft_it.hasNext()) {
			Entry<Long, Long> row = ft_it.next();
			output.write(row.getValue() + " ");
		}
	}

	void printDocumentTable(BufferedWriter output) throws IOException {
		Iterator<Entry<String, Long>> dt_it = document_table.entrySet().iterator();
		
		output.write("\nDATA AT INDEX NODE " + node_id + ": \n");
		while (dt_it.hasNext()) {
			Entry<String, Long> row = dt_it.next();
			output.write(row.getKey() + "\n");
		}
	}
	
	Long findSuccessor(Long key, TreeMap<Long, Node> Ring) {
		if (Ring.higherKey(key) == null) {
			return Ring.firstKey();
		}
		
		else if (Ring.containsKey(key)) {
			return key;
		}
		
		else {
			return Ring.higherKey(key);
		}
	}
	
	void stabilizeFingerTable(TreeMap<Long, Node> Ring, Long N) {
		this.N = N;
		
		for (int i = 0; i < finger_table_size; i++) {
			Long key = node_id + bitHash(i);
			
			if (key >= N) {
				key = (key & (N - 1));
			}
			
			Long this_successor = findSuccessor(key, Ring);
			finger_table.put(key, this_successor);
		}
	}
	
	boolean keyInRange(long key, Node node) {
		Long node_id = node.getNodeID();
		
		if (node_id < this.node_id) {
			node_id += N;
		}
		
		if ((node_id >= key) && (this.node_id < key)) {
			return true;
		}
		
		else {
			return false;
		}
	}
	
	int bitHash(int index) {
		return (1 << index);
	}
}
