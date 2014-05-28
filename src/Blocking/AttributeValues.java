package Blocking;

import java.util.ArrayList;

public class AttributeValues {
	/*--------------------------------Fields------------------------------------*/
	private String name;
	private ArrayList<String> values;
	
	/*--------------------------------Methods-----------------------------------*/
	// Constructors
	public AttributeValues() {
		name = "";
		values = new ArrayList<String>();
	}
	public AttributeValues(String newname, ArrayList<String> newvalues) {
		name = newname;
		values = newvalues;
	}
	
	// Returns list of attribute's values
	public String getName() {
		return name;
	}
	
	// Returns name of attribute
	public ArrayList<String> getValues() {
		return values;
	}
	
	// Inserts a new value in list
	public void insetValue(String newvalue) {
		// for each value in list
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i).compareTo(newvalue) == 0) {
				return;
			}
		}
		values.add(newvalue);
	}

	// Prints attribute with list of it's values
	public void print() {
		System.out.println("Attribute : ".concat(name));
		for (int i = 0; i < values.size(); i++) {
			System.out.println("    ".concat(values.get(i)));
		}
		System.out.println("");		
	}
}
