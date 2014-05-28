package Blocking;

import java.util.ArrayList;

public class AttributeList {
	/*--------------------------------Fields------------------------------------*/
	private ArrayList<String> names;
	
	/*--------------------------------Methods-----------------------------------*/
	// Constructors
	public AttributeList() {
		names = new ArrayList<String>();
	}
	public AttributeList(ArrayList<Candidate> reference_set) {		
		ArrayList<Integer> values = new ArrayList<Integer>();
		names = new ArrayList<String>();
		for (int i = 0; i < reference_set.size(); i++) {
			// for each attribute in candidate
			for (int j = 0; j < reference_set.get(i).attributes().size(); j++) {
				boolean found = false;
				int lokation = 0;
				String name = reference_set.get(i).attributes().get(j).getName();
				// for each attribute in list of all attributes
				for (int k = 0; k < names.size(); k++) {
					if (name.compareTo(names.get(k)) == 0) {
						found = true;
						lokation = k;
						break;
					}
				}
				if (!found) {
					names.add(name);
					values.add(1);
				} else {
					values.set(lokation, values.get(lokation) + 1);
				}
			}
		}
		/*
		// sorting by frequency
		for (int i = 0; i < values.size() - 1; i++) {
			for (int j = i + 1; j < values.size(); j++) {
				if (values.get(j) > values.get(i)) {
					Integer tmp = values.get(j);
					values.set(j, values.get(i));
					values.set(i, tmp);
					String buf = names.get(j);
					names.set(j, names.get(i));
					names.set(i, buf);
				}
			}
		}
		//*/
	}

	// Returns list of attributes' names
	public ArrayList<String> getNames() {
		return names;
	}
}
