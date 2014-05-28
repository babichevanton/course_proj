import java.util.ArrayList;

import Blocking.*;
import Data.DataStore;
import Metrics.Levenstein;

public class BlockingSchemeLearner {
	/*--------------------------------Fields------------------------------------*/
	private ArrayList<Candidate> reference_set;
	
	/*--------------------------------Methods-----------------------------------*/
	// Constructors
	public BlockingSchemeLearner() {
		reference_set = new ArrayList<Candidate>();
	}
	public BlockingSchemeLearner(DataStore store, int begin, int end) {
		reference_set = new ArrayList<Candidate>();
		ArrayList<Candidate> input = store.getBase();
		for (int i = begin - 1; i < end; i++) {
			reference_set.add(input.get(i));
		}
	}
	
	// Checking covering candidate by rule
	private boolean is_covering(Candidate cand, Rule rule) {
		boolean[] flag1 = new boolean[rule.getConjunctions().size()];
		for (int i = 0; i < flag1.length; i++) {
			flag1[i] = true;
		}
		// For each conjunction in rule
		for (int i = 0; i < rule.getConjunctions().size(); i++) {
			boolean flag2 = true;
			Conjunction conj = rule.getConjunctions().get(i);
			// For each attribute in conjunction
			for (int j = 0; j < conj.getAttr().size(); j++) {
				String attr = cand.search(conj.getAttr().get(j).getName());
				if ((attr == null) || (attr.compareTo(
						conj.getAttr().get(j).getValue()) != 0)) {
					flag2 = false;
					break;
				}
			}
			if (!flag2) {
				flag1[i] = false;
				break;
			}
		}
		for (int i = 0; i < flag1.length; i++) {
			if (flag1[i]) {
				return true;
			}	
		}
		return false;
	}
	
	// Returns examples of post's sense
	private ArrayList<Candidate> getExamples(Post post) {
		ArrayList<Candidate> result = new ArrayList<Candidate>();
		double feas_match = 0.2;  	// feas_match is a minimal feasible percent 
									// of matching attributes in candidate.
		double feas_err_len = 0.1; 	// feas_err_len is a feasible percent of 
									// difference in any attribute's value and post

		// for each element from the Reference Set
		for (int i = 0; i < reference_set.size(); i++) {
			// number of feasible attributes
			int minfeasattr = (int) (reference_set.get(i).attributes().size() * feas_match);
			if (reference_set.get(0).attributes().size() * feas_match > minfeasattr) {
				minfeasattr += 1;
			}
			// for each attribute in candidate
			// Checking the candidate by post
			int existing = 0;
			for (int j = 0; j < reference_set.get(i).attributes().size(); j++) {
				// feasible difference
				int maxdiff = (int) (reference_set.get(i).attributes().get(j).getValue().length()
						* feas_err_len);
				if (Levenstein.editdist(reference_set.get(i).attributes().get(j).getValue(), 
							 post.find_attr(reference_set.get(i).attributes().get(j).getValue()))
							 <= maxdiff) {
					existing += 1;
				}
			}
			if (existing >= minfeasattr) {
				result.add(reference_set.get(i));
			}
		}

		return result;
	}
	
	// Computes Reduction Ratio of rule
	private double ReductionRatio(Rule rule) {
		double subset = 0.0;
		
		// for each element from the Reference Set
		for (int i = 0; i < reference_set.size(); i++) {
			if (is_covering(reference_set.get(i), rule)) {
				subset += 1;
			}
		}
		
		return 1 - subset / reference_set.size();
	}
	
	// Computes Pair Completeness of rule
	private double PairCompleteness(Rule rule, ArrayList<Candidate> examples) {
		int truepositives = 0;

		// for each element from the Examples
		for (int i = 0; i < examples.size(); i++) {
			// Checking the candidate by rule
			if (is_covering(examples.get(i), rule)) {
				truepositives += 1;
			}
		}
		
		return (examples.size() == 0) ? 0 : ((double) truepositives) / examples.size();
	}
	
	// Computes the rule covering all the examples
	public Rule sequential_covering(Post post) {
		Rule result = new Rule();
		ArrayList<Candidate> examples = getExamples(post);
		
		ArrayList<AttributeValues> attributes = new ArrayList<AttributeValues>();
		
		// Constructing list of all attributes
		// for each element of the Reference Set
		for (int i = 0; i < reference_set.size(); i++) {
			// for each attribute in candidate
			for (int j = 0; j < reference_set.get(i).attributes().size(); j++) {
				boolean found = false;
				int lokation = 0;
				String name = reference_set.get(i).attributes().get(j).getName();
				String value = reference_set.get(i).attributes().get(j).getValue();
				// for each attribute in list of all attributes				
				for (int k = 0; k < attributes.size(); k++) {
					if (name.compareTo(attributes.get(k).getName()) == 0) {
						found = true;
						lokation = k;
						break;
					}
				}
				if (found) { // Inserting a value in list of values
					attributes.get(lokation).insetValue(value);
				} else { // Adding a new attribute
					ArrayList<String> newvalues = new ArrayList<String>();
					newvalues.add(value);
					AttributeValues new_attr = new AttributeValues(name, newvalues);
					attributes.add(new_attr);
				}
			}
		}

		Conjunction conj = learn_one_conj(attributes, 0.5, examples);
		
		while (examples.size() != 0) {
			result.getConjunctions().add(conj);
			//removing examples covered by the "conj"
			Rule one_rule = new Rule();
			one_rule.getConjunctions().add(conj);
			//int index = 1;
			//System.out.println("");
			//System.out.println("Examples, covered by the " + index + " conjunction");
			for (int i = examples.size() - 1; i >= 0; i--) {
				if (is_covering(examples.get(i), one_rule)) {
					//examples.get(i).print();
					examples.remove(i);
				}
			}
			conj = learn_one_conj(attributes, 0.5, examples);
		}
		
		return result;
	}
	
	// Learns one conjunction to rule
	private Conjunction learn_one_conj(
			ArrayList<AttributeValues> attributes, 
			double min_thresh, 
			ArrayList<Candidate> examples) {
		Conjunction result = new Conjunction();
		
		while (attributes.size() != 0) {
			Conjunction child = new Conjunction(result);
			String selected = new String();
			// checking every "result"'s child conjunctions by RR and PC 
			// for each attribute in list
			for (int i = 0; i < attributes.size(); i++) {
				// for each value in list of attribute's values
				for (int j = 0; j < attributes.get(i).getValues().size(); j++) {
					ArrayList<Conjunction> conj = new ArrayList<Conjunction>();
					ArrayList<Conjunction> res_conj = new ArrayList<Conjunction>();
					Attribute one_attr = new Attribute(
							attributes.get(i).getName(),
							attributes.get(i).getValues().get(j));
					child.getAttr().add(one_attr);
					conj.add(child);
					res_conj.add(result);
					Rule one_rule = new Rule(conj);
					Rule res_rule = new Rule(res_conj);
					double RR_or = ReductionRatio(one_rule);
					double RR_rr = ReductionRatio(res_rule);
					double PC_or = PairCompleteness(one_rule, examples);
					if ((RR_or > RR_rr) && (PC_or >= min_thresh)) {
						result.copy(child);
						selected = one_attr.getName();
					}
					child.getAttr().remove(child.getAttr().size() - 1);
				}
			}
			// removing selected attribute from list
			// for each attribute in list
			if (selected.compareTo("") == 0) {
				break;
			} else {
				for (int i = 0; i < attributes.size(); i++) {
					if (attributes.get(i).getName().compareTo(selected) == 0) {
						attributes.remove(i);
						break;
					}
				}
			}
		}

		return result;
	}

	// Returns Candidates covered by the "rule"
	public ArrayList<Candidate> getCandidates(Rule rule) {
		ArrayList<Candidate> result = new ArrayList<Candidate>();

		// for each element from the Reference Set
		for (int i = 0; i < reference_set.size(); i++) {
			if (is_covering(reference_set.get(i), rule)) {
				result.add(reference_set.get(i));
			}
		}

		return result;
	}
	
	// Returns Reference Set elements
	public ArrayList<Candidate> getRS() {
		return reference_set;
	}
	
	// Print Reference Set
	public void printRS() {
		for (int i = 0; i < reference_set.size(); i++) {
			reference_set.get(i).print();
			System.out.println("");
		}
	}

}
