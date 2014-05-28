package Blocking;

import java.util.ArrayList;

public class Conjunction {
	/*--------------------------------Fields------------------------------------*/
	private ArrayList<Attribute> attr_names;

	/*--------------------------------Methods-----------------------------------*/
	// Constructors
	public Conjunction() {
		attr_names = new ArrayList<Attribute>();
	}
	public Conjunction(Conjunction conj) {
		attr_names = new ArrayList<Attribute>(conj.getAttr());
	}
	public Conjunction(ArrayList<Attribute> conj) {
		attr_names = new ArrayList<Attribute>();		
		for (int i = 0; i < conj.size(); i++) {
			attr_names.add(conj.get(i));
		}
	}
	
	// Copying conj' content
	@SuppressWarnings("unchecked")
	public void copy(Conjunction conj) {
		attr_names = (ArrayList<Attribute>) conj.getAttr().clone();
	}
	
	// Printing conjunction
	public void print() {
		for (int i = 0; i < attr_names.size(); i++) {
			attr_names.get(i).print();
		}
	}
	
	public ArrayList<Attribute> getAttr() { return attr_names;}
	public void setAttr(ArrayList<Attribute> attr_names) {
		this.attr_names = attr_names;
	}
}
