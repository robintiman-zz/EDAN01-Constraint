package lab1;

import org.jacop.constraints.SumInt;
import org.jacop.constraints.XgtY;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMax;
import org.jacop.search.LargestMax;
import org.jacop.search.PrintOutListener;
import org.jacop.search.SimpleSelect;

public class Games {

	public Games() {
		// Constants
		int num = 4;
		int cap = 5;
		int refill = 2;
		int[] fun = { 4, 1, 2, 3 };

		Store store = new Store();
		IntVar tok = new IntVar(store, "tok", 0, cap);
		IntVar spent = new IntVar(store, "spent", num, cap + (num - 1) * refill);
		IntVar visits = new IntVar(store, "visits");
		IntVar spentInv = new IntVar(store, "max");
		

		IntVar[] games = new IntVar[num];
		IntVar[] funs = new IntVar[num];
		IntVar[] usage = new IntVar[num];
		for (int i = 0; i < num; i++) {
			games[i] = new IntVar(store, "G" + i, 1, cap);
			funs[i] = new IntVar(store, "Fun" + i, fun[i], fun[i]);
			usage[i] = new IntVar(store, "R" + i, 1,1);
		}
		
		
		
		// Thinking that we want to maximize the number of tokens spent on
		// the most fun game.
		store.impose(new XgtY(games[0], games[3]));
		store.impose(new XgtY(games[3], games[2]));
		store.impose(new XgtY(games[2], games[1]));
		store.impose(new SumInt(store, games, "<=", spent));
		// store.impose(new Cumulative(games, funs, usage, tok));
		
		
//		IntVar fun = new IntVar(store, "fun");
//		NetworkBuilder net = new NetworkBuilder(fun);
//		
//		Node source = net.addNode("source", cap);
//		Node sink = net.addNode("sink", -cap);
//		
//		Node A = net.addNode("A", 0);
//		Node B = net.addNode("B", 0);
//		Node C = net.addNode("C", 0);
//		Node D = net.addNode("D", 0);
//		
		
		// TODO: Make it work
		// TODO: Figure out where refill should be used and how to decrease the
		// number of tokens used each turn.
		DepthFirstSearch<IntVar> search = new DepthFirstSearch<IntVar>();
		SimpleSelect<IntVar> ss = new SimpleSelect<IntVar>(games, new LargestMax<IntVar>(),
				new IndomainMax<IntVar>());

		search.setSolutionListener(new PrintOutListener<IntVar>());
		
		boolean result = search.labeling(store, ss);
	}

	public static void main(String[] args) {
		new Games();
	}
}
