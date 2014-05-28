import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Blocking.Candidate;
import Blocking.Rule;
import Data.DataStore;

public class Main {
	/*--------------------------------Fields------------------------------------*/
	
	/*--------------------------------Methods-----------------------------------*/
	
	public static void main(String[] args) {
		DataStore store = new DataStore();
		String itemsfile = "data/models.json";
		
        File res = new File("res.txt");
        if (res.exists()) {
        	res.delete();
        }
		
		FileReader fr = null;
		try {
			fr = new FileReader("data/posts.txt");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ArrayList<String> posts = new ArrayList<String>();
		BufferedReader br = new BufferedReader(fr);
		String s;
		try {
			while((s = br.readLine()) != null) {
			posts.add(s);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		int storebase_size = 8055;		// number of elements in dataset
		int RS_size = 1000;				// number of elements in Reference Set
		int SVMtrain_size = 8055;		// number of SVM train items
		int multiSVMtrain_size = 8055;	// number of Multi-ClassSVM train items
		int numofattrs = 6;				// number of checking attributes
		
		
		store.init_base(itemsfile, storebase_size);
		
		int size = (RS_size > store.getBase().size()) ? store.getBase().size(): RS_size;
		
		BlockingSchemeLearner bsl = new BlockingSchemeLearner(store, 1, size);
		PostExplorer explorer = new PostExplorer(
				bsl.getRS(),
				store,
				numofattrs,
				"data/SVMmodel.model",
				SVMtrain_size,
				"data/MultiSVMmodel.model",
				multiSVMtrain_size);
		//*
		
		for(int j = 0; j < posts.size(); j += 1) {
			Post post = new Post(posts.get(j));
			//long begin = System.currentTimeMillis();
			Rule rule = bsl.sequential_covering(post);
			//rule.print();
			//System.out.println("");
			//*
			ArrayList<Candidate> cands = bsl.getCandidates(rule);
			//System.out.println(System.currentTimeMillis() - begin + "ms");
			//*/
			
			/*
			for (int i = 0; i < cands.size(); i++) {
				System.out.println("");
				cands.get(i).print();
			}
			//*/
	
			//*
			ArrayList<Double> prediction = explorer.svm_predict(post.content(), cands);
			
			int index = 0;
			double max_pr = 0;
			for (int i = 0; i < prediction.size(); i++) {
				if (prediction.get(i) > max_pr) {
					index = i;
					max_pr = prediction.get(i);
					//cands.get(i).print();
					//System.out.println("");
				}
			}
			//cands.get(index).print();
			//System.out.println("");
			//*/
			
			//*
			explorer.results(
					post, 
					explorer.multisvm_predict(post, cands.get(index)),
					cands.get(index));
			//*/
			System.out.println("");
			System.out.println("Succeed " + (j + 1));
		}
	}
}
