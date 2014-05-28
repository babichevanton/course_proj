package Data;

import java.util.ArrayList;

import Blocking.Attribute;

public class Item {
	/*--------------------------------Fields------------------------------------*/
	private String name;
	private ArrayList<Attribute> attributes;
	
	/*--------------------------------Methods-----------------------------------*/
	// Constructors
	public Item() {
		name = "";
		attributes = new ArrayList<Attribute>();
	}
	
	// Initialising
	public void init(String newname, ArrayList<Attribute> newattributes) {
		name = newname;
		attributes = new ArrayList<Attribute>();
		for (int i = 0; i < newattributes.size(); i++) {
			attributes.add(newattributes.get(i));
		}
	}
	
	// Printing item
	public void print() {
		System.out.println(name);
		for (int i = 0; i < attributes.size(); i++) {
			attributes.get(i).print();
		}
	}
	
	public String getName() {
		return name;
	}
	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}
}
