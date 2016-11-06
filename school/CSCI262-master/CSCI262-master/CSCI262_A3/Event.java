
public class Event {

	private String name;
	private double weight;
	private double average;
	private double std_deviation;
	
	public Event(String name, double weight) {
		this.name = name;
		this.weight = weight;
		this.average = 0;
		this.std_deviation = 0;
	}
	
	public String get_name() {
		return this.name;
	}
	
	public void set_name(String name) {
		this.name = name;
	}
	
	public double get_weight() {
		return this.weight;
	}
	
	public void set_weight(double weight) {
		this.weight = weight;
	}
	
	public double get_average() {
		return this.average;
	}
	
	public void set_average(double average) {
		this.average = average;
	}
	
	public double get_std_deviation() {
		return this.std_deviation;
	}
	
	public void set_std_deviation(double std_deviation) {
		this.std_deviation = std_deviation;
	}
	
	public String toString() {
		return (String.format("%20s | %10s  |  %10s  |  %5s", name, Double.toString(average), Double.toString(std_deviation), Double.toString(weight)));
	}
}
