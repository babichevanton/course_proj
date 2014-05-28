package Blocking;

public class Attribute {
	/*--------------------------------Fields------------------------------------*/
	private String name;
	private String value;
	
	/*--------------------------------Methods-----------------------------------*/
	// Constructors
	public Attribute() {
		name = new String();
		value = new String();
	}
	public Attribute(String new_name, String new_value) {
		name = new_name;
		value = new_value;
	}
	
	// Shows attributes's fields
	public String getName() { return name;}	
	public String getValue() { return value;}
	
	// Prints one attribute "key value"
	public void print() {
		System.out.println(name.concat(" ").concat(value));
	}
}
