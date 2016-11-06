import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

public class IDSE {

	private static final int FAIL = 1;
	private static final int SUCCESS = 0;
	
	public static void main(String[] args) {
		
		if (args.length == 0)		// empty arguments
			Message.error(9);
		
		else if (args.length != 3)	// insufficient arguments
			Message.error(9);

		else {
			System.out.println("\n\n");
			String event_file = args[0];
			String base_file = args[1];
			String test_events_file = args[2];
			
			init_system(event_file, base_file, test_events_file);
			System.out.println("\n\n");
		}
	}

	private static void init_system (String event_file, String base_file, String test_events_file) {
		
		ArrayList<Event> event_list = new ArrayList<Event>();
		ArrayList<ArrayList<Integer>> day_list = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> test_list = new ArrayList<ArrayList<Integer>>();

		double threshold = 0;
		int loading_result = load_file(event_file, base_file, test_events_file, event_list, day_list, test_list);
		
		if (loading_result == SUCCESS) {
			calculate_average(event_list, day_list);
			calculate_deviation(event_list, day_list);
			
			System.out.println(String.format("%20s | %10s  |  %10s  |  %5s", "Event", "Avg.", "Std. Dev.", "Weight"));
			System.out.println("   ---------------------------------------------------------");
			
			for (Event event : event_list)
				System.out.println(event.toString());
			
			for (int i = 0; i < event_list.size(); i++)
				threshold += event_list.get(i).get_weight() * 2;
			
			System.out.println("\nThreshold: \t" + threshold + "\n");
			
			for (int i = 0; i < test_list.size(); i++) {
				DecimalFormat df = new DecimalFormat("#.##");
				
				double distance = 0;
				System.out.print("Line " + (i + 1) + " -- ");
				
				for (int j = 0; j < test_list.get(i).size(); j++) {
					System.out.print(test_list.get(i).get(j) + ":");
					
					double weight = ((event_list.get(j).get_average() - test_list.get(i).get(j)) / event_list.get(j).get_std_deviation()) * event_list.get(j).get_weight();
				
					if (weight < 0)
						distance += (weight * -1);
					
					else
						distance += weight;
				}
				
				if (distance < 0)
					distance *= -1;
						
				if (distance >= threshold)
					System.out.print(String.format("%15s%5s%15s", "Distance: ", Double.valueOf(df.format(distance)), "Alarm: Yes\n"));
				
				else
					System.out.print(String.format("%15s%5s%15s", "Distance: ", Double.valueOf(df.format(distance)), "Alarm: No\n"));
			}
		}
	}

	private static int load_file(String event_file, String base_file, String test_events_file, ArrayList<Event> event_list, ArrayList<ArrayList<Integer>> day_list, ArrayList<ArrayList<Integer>> test_list) {    	
		int monitored_events = 0;

		/********* EVENTS FILE *********/
		try {
			File file = new File(event_file);
	    	FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line;
			int counter = 0;
			
			if (check_file_extension(file)) {
				while ((line = bufferedReader.readLine()) != null) {
					if (counter == 0) {	// read the first line for number of monitored events
						monitored_events = Integer.parseInt(line);
						
						if (monitored_events > 20) {
							Message.error(1);
							fileReader.close();
							bufferedReader.close();
							return FAIL;
						}
					}
					
					else {	// read subsequent line for events
						String[] part = line.split(":");
											
						for (int i = 0; i < part.length; i++)
							event_list.add(new Event(part[i], Double.parseDouble(part[++i])));
					}
					
					counter++;
				}

				if (monitored_events != event_list.size()) { // check if results tally
					Message.error(2);
					fileReader.close();
					bufferedReader.close();
					return FAIL;
				}
				
				bufferedReader.close();
				fileReader.close();
			}
			
			else {
				Message.error(0);
				return FAIL;
			}
			
		} catch (IOException e) {
			Message.file_error(0);
			return FAIL;
		}
		/********* EVENTS FILE *********/
		
		
		
		
		/********* BASE DATA FILE *********/
		try {
			File file = new File(base_file);
	    	FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line;
			ArrayList<Integer> day = null;
			
			if (check_file_extension(file)) {
				while ((line = bufferedReader.readLine()) != null) {
					day = new ArrayList<Integer>();
					
					String[] part = line.split(":");
					
					for (int i = 0; i < monitored_events; i++)
						day.add(Integer.parseInt(part[i]));
					
					day_list.add(day);
				}
				
				if (monitored_events != day.size()) { // check if results tally
					Message.error(3);
					fileReader.close();
					bufferedReader.close();
					return FAIL;
				}
				
				bufferedReader.close();
				fileReader.close();
			}
			
			else {
				Message.error(0);
				return FAIL;
			}

		} catch (IOException e) {
			Message.file_error(0);
			return FAIL;
		}
		/********* BASE DATA FILE *********/
		
		
		
		
		/********* TEST EVENT FILE *********/
		try {
			File file = new File(test_events_file);
	    	FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line;
			ArrayList<Integer> day = null;
			
			if (check_file_extension(file)) {
				while ((line = bufferedReader.readLine()) != null) {
					day = new ArrayList<Integer>();
					
					String[] part = line.split(":");

					for (int i = 0; i < monitored_events; i++)
						day.add(Integer.parseInt(part[i]));
					
					test_list.add(day);
				}
				
				if (monitored_events != day.size()) { // check if results tally
					Message.error(4);
					fileReader.close();
					bufferedReader.close();
					return FAIL;
				}
				
				bufferedReader.close();
				fileReader.close();
			}
			
			else {
				Message.error(0);
				return FAIL;
			}

		} catch (IOException e) {
			Message.file_error(0);
			return FAIL;
		}
		/********* TEST EVENT FILE *********/

		// System.out.println("Passed? : " + );
	
		return SUCCESS;
	}

	private static void calculate_average(ArrayList<Event> event_list, ArrayList<ArrayList<Integer>> day_list) {
		for (int i = 0; i < event_list.size(); i++) {
			double average = 0.0;
			
			for (int j = 0; j < day_list.size(); j++)
				average += day_list.get(j).get(i);
			
			DecimalFormat df = new DecimalFormat("#.##");
			event_list.get(i).set_average(Double.valueOf(df.format(average / day_list.size())));
		}
	}
	
	private static void calculate_deviation(ArrayList<Event> event_list, ArrayList<ArrayList<Integer>> day_list) {
		for (int i = 0; i < event_list.size(); i++) {
			double dev = 0;
			
			for (int j = 0; j < day_list.size(); j++)
				dev += ((day_list.get(j).get(i) - event_list.get(i).get_average()) * (day_list.get(j).get(i) - event_list.get(i).get_average()));
			
			DecimalFormat df = new DecimalFormat("#.##");
			event_list.get(i).set_std_deviation(Double.valueOf(df.format(Math.sqrt(dev / (day_list.size())))));
		}
	}
	
	private static boolean check_file_extension(File file) {
		String file_name = file.getName();
		int index = file_name.lastIndexOf('.');
		
		if (index > 0) {
			if (file_name.substring(index + 1).equals("txt"))
				return true;	// is valid file
		}

		return false;	// not a valid file
	}
}
