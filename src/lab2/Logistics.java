package lab2;

import org.jacop.constraints.Subcircuit;
import org.jacop.constraints.XneqC;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.PrintOutListener;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;

public class Logistics {

	public Logistics() {
		int graph_size = 6;
		int start = 1;
		int n_dests = 1;
		int[] dest = { 6 };
		int n_edges = 7;
		int[] from = { 1, 1, 2, 2, 3, 4, 4 };
		int[] to = { 2, 3, 3, 4, 5, 5, 6 };
		int[] cost_arr = { 4, 2, 5, 10, 3, 4, 11 };

		Store store = new Store();

		// We seek to minimize cost
		IntVar cost = new IntVar(store, "cost", 0, 50);

		// Setup FDVs
		IntVar[] v = new IntVar[graph_size];
		for (int i = 0; i < graph_size; i++) {
			v[i] = new IntVar(store, "city" + i, i, i);
		}

		// Add domains
		for (int i = 0; i < n_edges; i++) {
			v[from[i] - 1].addDom(to[i] - 1, to[i] - 1);
			v[to[i] - 1].addDom(from[i] - 1, from[i] - 1);
		}

		// Start and dests can't point to themselves because they must be in 
		// the solution.
		store.impose(new XneqC(v[start-1], start-1));
		for (int d : dest) {
			store.impose(new XneqC(v[d-1], d-1));
		}
		store.impose(new Subcircuit(v));

		// Find solution 
		long T1, T2;
		T1 = System.currentTimeMillis();
		Search<IntVar> label = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(v, null,
				new IndomainMin<IntVar>());
		label.setSolutionListener(new PrintOutListener<IntVar>());
		label.setAssignSolution(true);
		label.setPrintInfo(true);
		label.getSolutionListener().searchAll(true);
		boolean res = label.labeling(store, select);
		T2 = System.currentTimeMillis();
		System.out.println("");
		System.out.println("\n\t*** Execution time = " + (T2 - T1) + " ms");
		System.out.println("");
		if (res) {
			System.out.println("*** Yes");
			System.out.println(java.util.Arrays.asList(v));
			IntVar[] theSolution = label.getVariables();
			System.out.println("getVariables " + java.util.Arrays.asList(theSolution));
		} else
			System.out.println("*** No");
	}

	public static void main(String[] args) {
		new Logistics();
	}
}
