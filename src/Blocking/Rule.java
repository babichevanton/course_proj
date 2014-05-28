package Blocking;

import java.util.ArrayList;

public class Rule {
	/*--------------------------------Fields------------------------------------*/
	private ArrayList<Conjunction> conjunctions;
	
	/*--------------------------------Methods-----------------------------------*/
	// Constructors
	public Rule() {
		conjunctions = new ArrayList<Conjunction>();
	}
	public Rule(ArrayList<Conjunction> conj) {
		conjunctions = new ArrayList<Conjunction>();
		for (int i = 0; i < conj.size(); i++) {
			conjunctions.add(conj.get(i));
		}
	}
	
	public ArrayList<Conjunction> getConjunctions() { return conjunctions;}
	public void setConjunctions(ArrayList<Conjunction> conjunctions) {
		this.conjunctions = conjunctions;
	}

	// Printing rule
	public void print() {
		for (int i = 0; i < conjunctions.size(); i++) {
			System.out.println((i + 1) + " conjunction:");
			conjunctions.get(i).print();
		}
	}
}
