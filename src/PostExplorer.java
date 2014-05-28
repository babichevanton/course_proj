import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

//import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

//import com.wcohen.ss.DirichletJS;
//import com.wcohen.ss.JelinekMercerJS;
import com.wcohen.ss.Jaccard;
import com.wcohen.ss.JaroWinkler;
import com.wcohen.ss.SmithWaterman;

import Blocking.AttributeList;
import Blocking.Candidate;
import Data.DataStore;
import Metrics.Levenstein;
import Metrics.Soundex;
import Metrics.Stemmer;

public class PostExplorer {
	/*--------------------------------Fields------------------------------------*/
	private AttributeList attributes;
	private svm_model SVMmodel;
	private svm_model MultiSVMmodel;
	private int count;
	
	/*--------------------------------Methods-----------------------------------*/
	// Constructors
	public PostExplorer() {
		attributes = new AttributeList();
		SVMmodel = new svm_model();
		MultiSVMmodel = new svm_model();
	}
	public PostExplorer(
			ArrayList<Candidate> reference_set, 
			DataStore store,
			int numofattrs, 
			String SVM,
			int numsvm,
			String MultiSVM,
			int nummultisvm) {
		attributes = new AttributeList(reference_set);
		count = numofattrs;
		// Training SVM
		try {
			SVMmodel = class_loadmodel(SVM);
		} catch (IOException e) {
			svm_train(store, 1, numsvm);
			try {
				class_savemodel(SVMmodel, SVM);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// Training MultiSVM
		try {
			MultiSVMmodel = class_loadmodel(MultiSVM);
		} catch (IOException e) {
			multisvm_train(store, 1, nummultisvm);
			try {
				class_savemodel(MultiSVMmodel, MultiSVM);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	// Creates a RL_scores descriptor from single unit
	private ArrayList<Double> createRL_scores(String str1, String str2) {
		ArrayList<Double> result = new ArrayList<Double>();

		//DirichletJS dirich = new DirichletJS();
		//JelinekMercerJS jel_mer = new JelinekMercerJS();
		Jaccard jac = new Jaccard();
		JaroWinkler jar_win = new JaroWinkler();
		SmithWaterman sm_wtm = new SmithWaterman();
		Stemmer stemmer = new Stemmer();

		//result.add(dirich.score(str1, str2));
		//result.add(jel_mer.score(str1, str2));
		result.add(jac.score(str1, str2));
		result.add((double) -Levenstein.editdist(str1, str2));
		result.add(jar_win.score(str1, str2));
		result.add(sm_wtm.score(str1, str2));
		result.add((double) -Levenstein.editdist(Soundex.soundex(str1), Soundex.soundex(str2)));
		result.add((double) -Levenstein.editdist(stemmer.stemming(str1), stemmer.stemming(str2)));
		
		return result;
	}

	// Creates a IE_scores descriptor from single unit
	private ArrayList<Double> createIE_scores(String str1, String str2) {
		ArrayList<Double> result = new ArrayList<Double>();

		JaroWinkler jar_win = new JaroWinkler();
		SmithWaterman sm_wtm = new SmithWaterman();
		Stemmer stemmer = new Stemmer();

		result.add((double) -Levenstein.editdist(str1, str2));
		result.add(jar_win.score(str1, str2));
		result.add(sm_wtm.score(str1, str2));
		result.add((double) -Levenstein.editdist(Soundex.soundex(str1), Soundex.soundex(str2)));
		result.add((double) -Levenstein.editdist(stemmer.stemming(str1), stemmer.stemming(str2)));
		
		return result;
	}

	// Creates a V_rl descriptor of candidate
	private ArrayList<Double> createV_rl(String post, Candidate cand) {
		ArrayList<Double> result = new ArrayList<Double>();
		
		int numofattr = (count < attributes.getNames().size()) ? count : attributes.getNames().size();
		// for each attribute in list
		for (int i = 0; i < numofattr; i++) {
			boolean found = false;
			int ind = 0;
			// for each attribute of candidate
			for (int j = 0; j < cand.attributes().size(); j++) {
				if (attributes.getNames().get(i).compareTo(
						cand.attributes().get(j).getName()) == 0) {
					found = true;
					ind = j;
					break;
				}
			}
			if (found) {
				ArrayList<Double> unit = createRL_scores(
						post, 
						cand.attributes().get(ind).getValue());
				for (int j = 0; j < unit.size(); j++) {
					result.add(unit.get(j));
				}
			} else {
				for (int j = 0; j < 6; j++) {
					result.add(0.0);
				}
			}
			found = false;
		}
		String cand_str = new String();
		for (int i = 0; i < cand.attributes().size(); i++) {
			cand_str = cand_str.concat(cand.attributes().get(i).getValue()).concat(" ");
		}
		ArrayList<Double> unit = createRL_scores(
				post, 
				cand_str.trim());
		for (int i = 0; i < unit.size(); i++) {
			result.add(unit.get(i));
		}
		
		return result;
	}
	
	// Creates a V_ie descriptor of token
	private ArrayList<Double> createV_ie(String token, Candidate cand) {
		ArrayList<Double> result = new ArrayList<Double>();
		
		int numofattr = (count < attributes.getNames().size()) ? count : attributes.getNames().size();
		// for each attribute in list
		for (int i = 0; i < numofattr; i++) {
			boolean found = false;
			int ind = 0;
			// for each attribute of candidate
			for (int j = 0; j < cand.attributes().size(); j++) {
				if (attributes.getNames().get(i).compareTo(
						cand.attributes().get(j).getName()) == 0) {
					found = true;
					ind = j;
					break;
				}
			}
			if (found) {
				ArrayList<Double> unit = createIE_scores(
						token, 
						cand.attributes().get(ind).getValue());
				for (int j = 0; j < unit.size(); j++) {
					result.add(unit.get(j));
				}
			} else {
				for (int j = 0; j < 5; j++) {
					result.add(0.0);
				}
			}
			found = false;
		}
		
		return result;
	}
	
	// Provides vectors with binary rescoring operation
	// Example:
	// vect1 = (-7.11, 0.24,  0.00, ...  4,51) -> (0, 1, 1, ... 0)
	// vect2 = ( 3.60, 0.24, -1.51, ... 12,70) -> (1, 1, 0, ... 1)
	private int[] binary_rescoring(ArrayList<ArrayList<Double>> vectors) {
		int[] result = new int[vectors.size()];
		
		// for each element of vector
		for (int i = 0; i < vectors.get(0).size(); i++) {
			double max_elem = Double.MIN_VALUE;
			// for each vector in set
			for (int j = 0; j < vectors.size(); j++) {
				if (vectors.get(j).get(i) > max_elem) {
					max_elem = vectors.get(j).get(i);
				}
			}
			// for each vector in set
			// max_elements becomes 1, others become 0
			for (int j = 0; j < vectors.size(); j++) {
				if (vectors.get(j).get(i).equals(max_elem)) {
					result[j]++;
					vectors.get(j).remove(i);
					vectors.get(j).add(i, 1.0);
				} else {
					vectors.get(j).remove(i);
					vectors.get(j).add(i, 0.0);
				}
			}
		}
		
		return result;
	}
	
	// Training SVM classifier
	public void svm_train(DataStore store, int begin, int end) {
		ArrayList<ArrayList<Double>> vectors = new ArrayList<ArrayList<Double>>();
		
		// Constructing vectors for positive candidates
		// for each element in store's base
		for (int i = begin - 1; i < end; i++) {
			vectors.add(createV_rl(store.getNames().get(i), store.getBase().get(i)));
		}
		// Constructing vectors for negative candidates
		// for each element in store's base
		for (int i = begin - 1; i < end; i++) {
			vectors.add(createV_rl(store.getNames().get(begin - 2 + end - i), store.getBase().get(i)));
		}
		int[] counts = binary_rescoring(vectors);
		
		// Initializing SVM parameters
		svm_parameter param = new svm_parameter();
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.cache_size = 1024;
		param.eps = 1e-4;
		param.C = 1;
		param.probability = 1;
		
		// Initializing SVM problem
		svm_problem problem = new svm_problem();
		// number of vectors in training set
		problem.l = vectors.size();
		// array of labels
		problem.y = new double[vectors.size()];
		for (int i = 0; i < vectors.size() / 2; i++) {
			problem.y[i] = 1.0;
			problem.y[vectors.size() / 2 + i] = -1.0;
		}
		// array of svm_nodes
		problem.x = new svm_node[vectors.size()][];
		for (int i = 0; i < vectors.size(); i++) {
			problem.x[i] = new svm_node[counts[i]];
			int ind = 0;
			for (int j = 0; j < vectors.get(i).size(); j++) {
				if (vectors.get(i).get(j).equals(1.0)) {
					svm_node node = new svm_node();
					node.index = j + 1;
					node.value = 1.0;
					problem.x[i][ind] = node;
					ind++;
				}
			}
		}
		
		// Getting model
		SVMmodel = svm.svm_train(problem, param);
	}

	// Training MultiSVM classifier
	public void multisvm_train(DataStore store, int begin, int end) {
		ArrayList<ArrayList<Double>> vectors = new ArrayList<ArrayList<Double>>();
		ArrayList<Integer> targets = new ArrayList<Integer>();
		int numofattrs = (count < attributes.getNames().size()) ? count : attributes.getNames().size();
		
		// Constructing vectors for candidates' tokens
		// for each element in store's base
		for (int i = begin - 1; i < end; i++) {
			// for each candidate's attribute
			for (int j = 0; j < store.getBase().get(i).attributes().size(); j++) {
				// looking for attribute
				boolean found = false;
				int index = 0;
				Candidate cand = store.getBase().get(i);
				for (int k = 0; k < numofattrs; k++) {
					if (attributes.getNames().get(k).compareTo(
							cand.attributes().get(j).getName()) == 0) {
						found = true;
						index = k;
						break;
					}
				}
				if (found) {
					// parsing attribute's value
					String[] tokens = cand.attributes().get(j).getValue().split(" ");
					/*
					String buffer = new String("");
					// making some noise
					for (int k = 0; k < tokens.length; k++) {
						if (tokens[k].length() < 3) {
							buffer += tokens[k];
						} else {
							buffer += Data.NoiseGen.makeNoise(tokens[k]) + " ";
						}
					}
					// noisy tokens
					tokens = buffer.trim().split(" ");
					//*/
					for (int k = 0; k < tokens.length; k++) {
						vectors.add(createV_ie(tokens[k], cand));
						targets.add(index);
					}
				}
			}
		}
		
		// Initializing SVM parameters
		svm_parameter param = new svm_parameter();
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.cache_size = 1024;
		param.eps = 1e-4;
		param.C = 0.01;
		//param.probability = 1;
		
		// Initializing SVM problem
		svm_problem problem = new svm_problem();
		// number of vectors in training set
		problem.l = vectors.size();
		// array of labels
		problem.y = new double[vectors.size()];
		for (int i = 0; i < vectors.size(); i++) {
			problem.y[i] = targets.get(i);
		}
		// array of svm_nodes
		problem.x = new svm_node[vectors.size()][];
		int[] counts = new int[vectors.size()];
		for (int i = 0; i < vectors.size(); i++) {
			for (int j = 0; j < vectors.get(i).size(); j++) {
				if (!vectors.get(i).get(j).equals(0.0)) {
					counts[i]++;
				}
			}
		}
		for (int i = 0; i < vectors.size(); i++) {
			problem.x[i] = new svm_node[counts[i]];
			int ind = 0;
			for (int j = 0; j < vectors.get(i).size(); j++) {
				if (!vectors.get(i).get(j).equals(0.0)) {
					svm_node node = new svm_node();
					node.index = j + 1;
					node.value = vectors.get(i).get(j);
					problem.x[i][ind] = node;
					ind++;
				}
			}
		}
		
		// Getting model
		MultiSVMmodel = svm.svm_train(problem, param);
	}

	// Predicting the candidate for post
	public ArrayList<Double> svm_predict(String post, ArrayList<Candidate> candidates) {
		ArrayList<Double> result = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> vectors = new ArrayList<ArrayList<Double>>();
		
		// for each candidate
		for (int i = 0; i < candidates.size(); i++) {
			vectors.add(createV_rl(post, candidates.get(i)));
		}
		int[] counts = binary_rescoring(vectors);
		for (int i = 0; i < vectors.size(); i++) {
			svm_node[] problem = new svm_node[counts[i]];
			int ind = 0;
			for (int j = 0; j < vectors.get(i).size(); j++) {
				if (vectors.get(i).get(j).equals(1.0)) {
					svm_node node = new svm_node();
					node.index = j + 1;
					node.value = 1.0;
					problem[ind] = node;
					ind++;
				}
			}
			double[] prob_estimates = new double[2];
			svm.svm_predict_probability(SVMmodel, problem, prob_estimates);
			result.add(prob_estimates[0]);
		}
		
		return result;
	}
	
	// Predicting the attributes for post
	public ArrayList<Double> multisvm_predict(Post post, Candidate candidate) {
		ArrayList<Double> result = new ArrayList<Double>();
		ArrayList<ArrayList<Double>> vectors = new ArrayList<ArrayList<Double>>();
		
		// for each token in post
		String[] tokens = post.tokens();
		for (int i = 0; i < tokens.length; i++) {
			vectors.add(createV_ie(tokens[i], candidate));
		}
		int[] counts = new int[vectors.size()];
		for (int i = 0; i < vectors.size(); i++) {
			for (int j = 0; j < vectors.get(i).size(); j++) {
				if (!vectors.get(i).get(j).equals(0.0)) {
					counts[i]++;
				}
			}
		}
		for (int i = 0; i < vectors.size(); i++) {
			svm_node[] problem = new svm_node[counts[i]];
			int ind = 0;
			for (int j = 0; j < vectors.get(i).size(); j++) {
				if (!vectors.get(i).get(j).equals(0.0)) {
					svm_node node = new svm_node();
					node.index = j + 1;
					node.value = vectors.get(i).get(j);
					problem[ind] = node;
					ind++;
				}
			}

			double prediction = svm.svm_predict(MultiSVMmodel, problem);
			result.add(prediction);
		}
		
		return result;
	}
	
	// Provides attribute's value with cleaning noisy tokens
	private void clean_attribute(String[] tokens, ArrayList<Double[]> tok_indices, String attribute) {
		if (attribute == null) {
			for (int i = 0; i < tok_indices.size(); i++) {
				tok_indices.get(i)[1] = -1.0;				
			}
			return;
		}
		boolean[] removed = new boolean[tok_indices.size()];
		int removing_candidate = -1;
		Jaccard jac = new Jaccard();
		JaroWinkler jar_win = new JaroWinkler();
		String buffer = new String("");
		for (int i = 0; i < tok_indices.size(); i++) {
			buffer += tokens[tok_indices.get(i)[0].intValue()] + " ";
			removed[i] = true;
		}
		String[] attrtokens = buffer.trim().split(" ");
		double jac_base = jac.score(buffer.trim(), attribute);
		double jarwin_base = jar_win.score(buffer.trim(), attribute);
		boolean processing = true; 
		while (processing) {
			processing = false;
			// for each token in attribute
			for (int i = 0; i < attrtokens.length; i++) {
				// deleting one token
				String str = new String("");
				for (int j = 0; j < attrtokens.length; j++) {
					if (removed[j] && j != i) {
						str += attrtokens[j] + " ";
					}
				}
				// computing new metrics
				double cur_jac = jac.score(str.trim(), attribute);
				double cur_jarwin = jar_win.score(str.trim(), attribute);
				if ((cur_jac > jac_base) && (cur_jarwin > jarwin_base)) {
					removing_candidate = i;
					jac_base = cur_jac;
					jarwin_base = cur_jarwin;
				} 
			}
			if (removing_candidate != -1) {
				processing = true;
				removed[removing_candidate] = false;
				tok_indices.get(removing_candidate)[1] = -1.0;
				removing_candidate = -1;
			}
		}
		//*
		if ((jac_base < 0.25) && (jarwin_base < 0.25)) {
			for (int i = 0; i < tok_indices.size(); i++) {
				tok_indices.get(i)[1] = -1.0;
			}
		}
		//*/
	}
	
	// Prints post by tokens with labels of attributes
	public void results(Post post, ArrayList<Double> labels, Candidate cand) {
		FileWriter fw = null;
		try {
			fw = new FileWriter("res.txt", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] tokens = post.tokens();
		// for each attribute in attribute list
		for (int i = 0; i < count; i++) {
			ArrayList<Double[]> oneattr_indices = new ArrayList<Double[]>();
			// for each attribute in labels
			for (int j = 0; j < labels.size(); j++) {
				if (labels.get(j).equals((double) i)) {
					Double[] oneattr = new Double[2];
					oneattr[0] = (double) j;
					oneattr[1] = labels.get(j);
					oneattr_indices.add(oneattr);
				}
			}
			if (oneattr_indices.size() > 0) {
				//System.out.println(i);
				clean_attribute(tokens, oneattr_indices, cand.search(attributes.getNames().get(i)));
				for (int j = 0; j < oneattr_indices.size(); j++) {
					labels.set(oneattr_indices.get(j)[0].intValue(), oneattr_indices.get(j)[1]);
				}
			}	
		}
		for (int i = 0; i < labels.size(); i++) {
			String attr_name = new String("");
			if (labels.get(i).equals(-1.0)) {
				attr_name = "junk";
			} else {
				//System.out.println(labels.get(i).intValue());
				attr_name = attributes.getNames().get(labels.get(i).intValue());
			}
			
			try {
				fw.write(tokens[i] + " " + attr_name + "\n");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			fw.write("\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		System.out.println("");
		for (int i = 0; i < attributes.getNames().size(); i++) {
			System.out.println(i + "  :  " + attributes.getNames().get(i));
		}
		//*/
	}
	
	// Loads model from file "modelFile"
	// If file doesn't exist throws IOExeption
	public svm_model class_loadmodel(String modelFile) throws IOException {
		return svm.svm_load_model(modelFile);
	}
	
	// Saves model to file "modelFile"
	public void class_savemodel(svm_model model, String modelFile) throws IOException {
		svm.svm_save_model(modelFile, model);
	}
}
