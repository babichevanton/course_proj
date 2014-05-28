import java.util.ArrayList;

import Metrics.Levenstein;

public class Post {
	/*--------------------------------Fields------------------------------------*/
	private String content;
	
	/*--------------------------------Methods-----------------------------------*/
	// Constructors
	public Post() {
		content = new String();
	}
	public Post(String input) {
		content = new String(input);	
	}	
	
	// Returns post's content
	public String content() {
		return content;	
	}

	// Returns post's tokens
	public String[] tokens() {
		return content.split(" ");	
	}
	
	// Finds attribute "attribute" in post
	public String find_attr(String attribute) {
		String result = "BUG";
		int min = Integer.MAX_VALUE; 
		int size = attribute.split(" ").length;
		String[] tokens = tokens(); 
		
		// window's sliding
		for (int i = 0; i < tokens.length - size; i++) {
			// hole's sliding
			for (int j = size; j >= 0; j--) {
				// forming candidate
				String candidate = "";
				for (int k = 0; k < size + 1; k++) {
					if (k != j) {
						candidate = candidate.concat(tokens[i + k]).concat(" ");
					}
				}
				candidate = candidate.trim();
				int tmp = Levenstein.editdist(candidate, attribute);
				if (tmp < min) {
					min = tmp;
					result = candidate;
				}
			}
		}
		
		return result;
	}

	// Print post
	public void print() {
		System.out.println(content);
	}
}
