package Blocking;

import java.util.ArrayList;

public class Candidate {
	/*--------------------------------Fields------------------------------------*/
	private ArrayList<Attribute> attributes;
	
	/*--------------------------------Methods-----------------------------------*/
	// Constructors
	public Candidate() {
		attributes = new ArrayList<Attribute>();
	}
	
	// Initializing candidate
	public void init(ArrayList<Attribute> initial) {
		for (int i = 0; i < initial.size(); i++) {
			attributes.add(initial.get(i));
		}
	}
	
	// Return list of attributes
	public ArrayList<Attribute> attributes() {
		return attributes;
	}

	// Return value of the attribute with name "name" or 'null' if find nothing
	public String search(String name) {
		for (int i = 0; i < attributes.size(); i++) {
			if (attributes.get(i).getName().compareTo(name) == 0) {
				return attributes.get(i).getValue();
			}
		}
		
		return null;
	}

	// Print candidate's attributes
	public void print() {
		for (int i = 0; i < attributes.size(); i++) {
			attributes.get(i).print();
		}
	}
}
