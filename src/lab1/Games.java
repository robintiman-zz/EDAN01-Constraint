package lab1;

import org.jacop.constraints.XgteqY;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMax;
import org.jacop.search.SimpleSelect;

public class Games {

	public Games() {
		// Constants
		int num = 4;
		int cap = 5;
		int refill = 2;
		int[] fun = {4,1,2,3};

		Store store = new Store();
		IntVar tok = new IntVar(store, "tok", 0, cap);
		IntVar visits = new IntVar(store, "visits");
		IntVar totfun = new IntVar(store, "totfun");


		IntVar[] games = new IntVar[num];
		for (int i = 0; i < num; i++) {
			games[i] = new IntVar(store, "G" + i, 1, cap);
		}
		
		// Thinking that we want to maximize the number of tokens spent on
		// the most fun game.
		store.impose(new XgteqY(games[0], games[3]));
		store.impose(new XgteqY(games[3], games[2]));
		store.impose(new XgteqY(games[2], games[1]));
		
		// TODO: Make it work
		// TODO: Figure out where refill should be used and how to decrease the 
		// 		 number of tokens used each turn. 
		DepthFirstSearch<IntVar> search = new DepthFirstSearch<>();
		SimpleSelect<IntVar> ss = new SimpleSelect<IntVar>(games, null, new IndomainMax<IntVar>());
		boolean result = search.labeling(store, ss);
		System.out.println(result);
	}
	
	public static void main(String[] args) {
		new Games();
	}
}
