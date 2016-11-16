package lab2;

import java.util.ArrayList;

import org.jacop.constraints.IfThenElse;
import org.jacop.constraints.LinearInt;
import org.jacop.constraints.Subcircuit;
import org.jacop.constraints.XeqC;
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
		int[] from =     { 1, 1, 2, 2,  3, 4, 4 };
		int[] to =       { 2, 3, 3, 4,  5, 5, 6 };
		int[] cost_arr = { 4, 2, 5, 10, 3, 4, 11 };

		Store store = new Store();

		// We seek to minimize cost
		IntVar cost = new IntVar(store, "cost", 1, 50);

		// Setup FDVs
		IntVar[] v = new IntVar[graph_size];
		for (int i = 0; i < graph_size; i++) {
			v[i] = new IntVar(store, "city" + (i + 1), i + 1, i + 1);
		}

		// Add domains
		for (int i = 0; i < n_edges; i++) {
			v[from[i] - 1].addDom(to[i], to[i]);
			v[to[i] - 1].addDom(from[i], from[i]);
		}
		v[5].addDom(start, start);
		// Start and dests can't point to themselves because they must be in
		// the solution.
		store.impose(new XneqC(v[start - 1], start));
		for (int d : dest) {
			store.impose(new XneqC(v[d - 1], d));
		}

		IntVar[] c = new IntVar[n_edges+1];
		int[] weights = new int[n_edges + 1];
//		IntVar[] weights = new IntVar[n_edges];
		for (int i = 0; i < n_edges; i++) {
			c[i] = new IntVar(store, "cost" + (i + 1));
			c[i].addDom(cost_arr[i], cost_arr[i]);
//			weights[i] = new BooleanVar(store);
			weights[i] = -1;
		}
		c[n_edges] = cost;
		weights[n_edges] = 1;
		store.impose(new Subcircuit(v));
		
		for (int node = 1; node <= graph_size; node++) {
			// store.impose(new IfThenElse(new XeqC(v[n1-1], n1), new
			// XeqC(c[n1-1], 0),
			// new XeqC(c[n1-1], cost_arr[i])));
			ArrayList<Integer> node_edges = new ArrayList<Integer>();
			for (int i = 0; i < n_edges; i++) {
				if (from[i] == node)
					node_edges.add(i);
				if (to[i] == node)
					node_edges.add(i);
			}

			for (int e : node_edges) {
//				store.impose(new Reified(new XeqC(v[node-1], node), weights[e]));
//				store.impose(new IfThen(new XeqC(c[e], 0), new XeqC(v[node-1],node)));
				store.impose(new IfThenElse(new XeqC(c[e], 0), 
						new XeqC(v[node-1], node), new XeqC(c[e], cost_arr[e])));

			}
			
		}
//		int[] weights_int = new int[n_edges+1];
//		for (int i = 0; i < n_edges; i++) {
//			weights_int[i] = weights[i].value();
//		}
//		weights_int[n_edges] = -1;
		store.impose(new LinearInt(store, c, weights, "==", 0));


		//		System.out.println(store.toString() + "\n*************");

		// Find solution
		long T1, T2;
		T1 = System.currentTimeMillis();
		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(v, null, 
				new IndomainMin<IntVar>());
		search.setSolutionListener(new PrintOutListener<IntVar>());
		//		search.setAssignSolution(true);
		// search.setPrintInfo(true);
		//		search.getSolutionListener().searchAll(true);
		//		search.printAllSolutions();
		search.setOptimize(true);
		boolean res = search.labeling(store, select, cost);
		T2 = System.currentTimeMillis();
		System.out.println("");
		System.out.println("\n\t*** Execution time = " + (T2 - T1) + " ms");
		System.out.println("");
		if (res) {
			System.out.println("*** Yes");
			System.out.println(search.getCostValue());
			System.out.println(java.util.Arrays.asList(v));
			for (IntVar o: c)
				System.out.println(o.toString());
		} else {
			System.out.println("*** No");
		}
	}

	public static void main(String[] args) {
		new Logistics();
	}
}
