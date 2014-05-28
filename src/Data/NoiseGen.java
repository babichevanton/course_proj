package Data;

import Metrics.Soundex;

public class NoiseGen {
	/*--------------------------------Fields------------------------------------*/
	
	/*--------------------------------Methods-----------------------------------*/
	public static String makeNoise(String token) {
		int mode = (int) Math.round((Math.random() * 5));
		switch (mode) {
			case 0: {
				return phoneNoise(token);
			}
			case 2: {
				return editNoise(token);
			}
			case 5: {
				return divNoise(token);
			}
			default: {
				return token;
			}
		}
	}
	
	private static String editNoise(String token) {
		int num = (int) Math.round((Math.random() * 2));
		for (int i = 0; i < num; i++) {
			int operation = (int) Math.round((Math.random() * 2));
			switch (operation) {
				case 0: {
					int index = (int) Math.round((Math.random() * (token.length() - 1)));
					if (index == token.length() - 1) {
						token = token.substring(0, index);
					} else {
						token = token.substring(0, index) + token.substring(index + 1);
					}
					break;
				}
				case 1: {
					int index = (int) Math.round((Math.random() * (token.length() - 1)));
					if (index == token.length() - 1) {
						token = token.substring(0, index) + "]";
					} else {
						token = token.substring(0, index) + "[" + token.substring(index + 1);
					}
					break;
				}
				case 2: {
					int index = (int) Math.round((Math.random() * (token.length() - 1)));
					token = token.substring(0, index) + "@" +  token.substring(index);
					break;
				}	
			}
		}
		
		return token;
	}
	
	private static String phoneNoise(String token) {
		String result = new String("");
		
		boolean flag = false;
		String buffer = new String("");
		for (int i = 0; i < token.length(); i++) {
			if (Character.isLetter(token.charAt(i))) {
				flag = true;
				buffer += token.charAt(i);
			} else {
				if (flag) {
					result += onetoken_phNoise(buffer);
					buffer = "";
				}
				result += token.charAt(i);
				flag = false;
			}
		}
		if (flag) {
			result += onetoken_phNoise(buffer);
		}
		
		return result;
	}
	
	private static String onetoken_phNoise(String token) {
		String sound = Soundex.soundex(token);
		String ext = String.copyValueOf(sound.toCharArray());
		int count1 = (int) Math.round(Math.random());
		for (int i = 0; i < count1; i++) {
			int ind = 1 + (int) Math.round(Math.random() * (sound.length() - 2));
			ext = sound.substring(0, ind) + "0" + sound.substring(ind);
		}
		for (int i = 1; i < ext.length(); i++) {
			int num = (int) Math.round(Math.random() * 2);
			for (int j = 0; j < num; j++) {
				ext = ext.substring(0, i) + ext.charAt(i) + ext.substring(i);
				i++;
			}
		}
		for (int i = 0; i < ext.length(); i++) {
	        switch (ext.charAt(i)) {
	        	case '1': {
			    	char[] symb = {'b', 'f', 'p', 'v'};
			    	int index = ((int) Math.round(Math.random() * (symb.length - 1)));
			    	String tmp = "" + symb[index];
			    	ext = ext.replaceFirst("1", tmp); 
			    	break; 
			    }

		        case '2': {
			    	char[] symb = {'c', 'g', 'j', 'k', 'q', 's', 'x', 'z'};
			    	int index = ((int) Math.round(Math.random() * (symb.length - 1)));
			    	String tmp = "" + symb[index];
			    	ext = ext.replaceFirst("2", tmp); 
			    	break; 
		        }

		        case '3': {
			    	char[] symb = {'d', 't'};
			    	int index = ((int) Math.round(Math.random() * (symb.length - 1)));
			    	String tmp = "" + symb[index];
			    	ext = ext.replaceFirst("3", tmp); 
			    	break; 
		        }

		        case '4': {
		        	ext = ext.replaceFirst("4", "l"); 
		        	break; 
		        }

		        case '5': { 
			    	char[] symb = {'m', 'n'};
			    	int index = ((int) Math.round(Math.random() * (symb.length - 1)));
			    	String tmp = "" + symb[index];
			    	ext = ext.replaceFirst("5", tmp); 
			    	break; 
		        }

		        case '6': {
		        	ext = ext.replaceFirst("6", "r"); 
		        	break; 
		        }

		        default:  { 
			    	char[] symb = {'a', 'o', 'u', 'e', 'i', 'y', 
			    			'-', '\"', '.', '(', ')'};
			    	int index = ((int) Math.round(Math.random() * (symb.length - 1)));
			    	String tmp = "" + symb[index];
			    	ext = ext.replaceFirst("0", tmp); 
		        	break; 
		        }
		    }
		}
		
		return ext;
	}
	
	private static String divNoise(String token) {
		int index = 1 + (int) Math.round(Math.random() * (token.length() - 2));
		token = token.substring(0, index) + " " + token.substring(index);
		
		return token;
	}
}
