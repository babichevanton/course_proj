package Data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

import org.json.simple.*;

import Blocking.Attribute;
import Blocking.Candidate;

public class DataStore {
	/*--------------------------------Fields------------------------------------*/
	private ArrayList<Item> base_items;
	//private ArrayList<String> test_items;
	
	/*--------------------------------Methods-----------------------------------*/
	// Constructors
	public DataStore() {
		base_items = new ArrayList<Item>();
		//test_items = new ArrayList<String>();
	}

	// Initializing base_items from file "filename"
	public void init_base(String filename, int numofitems) {
		Reader reader = null;
		try {
			reader = new FileReader(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object obj = JSONValue.parse(reader);
		JSONArray array=(JSONArray) obj;
		int itemcount = 0;
		if (numofitems < array.size()) {
			itemcount = numofitems;
		} else {
			itemcount = array.size();
		}
		for (int i = 0; i < itemcount; i++) {
			Item one_item = new Item();
			JSONObject item = (JSONObject) array.get(i);
			String name = ((String) item.get("name")).trim();
			JSONArray attrs = (JSONArray) item.get("attr");
			ArrayList<Attribute> attributes = new ArrayList<Attribute>();
			for (int j = 0; j < attrs.size(); j++) {
				JSONArray attr = (JSONArray) attrs.get(j);
				String s = ((String) attr.get(0)).trim();
				
				if ((s.compareTo("Color") == 0) || 
						(s.compareTo("Screen Size") == 0) ||
						(s.compareTo("RAM") == 0) ||
						(s.compareTo("Hard Drive") == 0) ||
						(s.compareTo("Brand Name") == 0) ||
						(s.compareTo("Series") == 0)) 
				{
					Attribute one_attr = new Attribute(
							((String) attr.get(0)).trim(),
							((String) attr.get(1)).trim());
					attributes.add(one_attr);
				}
			}
			if (attributes.size() > 4) {
				one_item.init(name, attributes);
				base_items.add(one_item);
			}
		}
		//System.out.println(base_items);
	}
	
	// Returns candidates from base_items for reference set
	public ArrayList<Candidate> getBase() {
		ArrayList<Candidate> result = new ArrayList<Candidate>();
		for (int i = 0; i < base_items.size(); i++) {
			Candidate one_cand = new Candidate();
			one_cand.init(base_items.get(i).getAttributes());
			result.add(one_cand);
		}		
		return result;
	}

	// Returns names of items from base_items
	public ArrayList<String> getNames() {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < base_items.size(); i++) {
			String one_name = new String();
			one_name = base_items.get(i).getName();
			result.add(one_name);
		}		
		return result;
	}

	// Printing data store
	public void print_base() {
		for (int i = 0; i < base_items.size(); i++) {
			base_items.get(i).print();
		}
	}
	
}
