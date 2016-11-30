package lab4;

import org.jacop.constraints.Cumulative;
import org.jacop.constraints.Diff2;
import org.jacop.constraints.XplusCeqZ;
import org.jacop.constraints.XplusClteqZ;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.Search;

public class Regression {

	public Regression() {
		// Data
		int mul_delay = 2, add_delay = 1;
		int nbr_adders = 1, nbr_muls = 1;
		int mul_ops = 16, add_ops = 12, ops = mul_ops + add_ops;
		int res = 2;
		int mul = 1, add = 0;
		int last[] = {26, 27}; // Could easily be found using "edges"

		// 1 means multiplication, 0 means addition
		int[] operations = { mul, mul, mul, mul, mul, mul, mul, mul, add, add, add, add, add, add, mul, mul, mul, mul,
				add, add, mul, mul, mul, mul, add, add, add, add };

		int[][] edges = { { 0, 8 }, { 1, 8 }, { 2, 9 }, { 3, 9 }, { 4, 10 }, { 5, 10 }, { 6, 11 }, { 7, 11 }, { 8, 26 },
				          { 9, 12 }, { 10, 13 }, { 11, 27 }, { 12, 14 }, { 12, 16 }, { 13, 15 }, { 13, 17 }, { 14, 18 },
				          { 15, 18 }, { 16, 19 }, { 17, 19 }, { 18, 20 }, { 18, 22 }, { 19, 21 }, { 19, 23 }, { 20, 24 },
				          { 21, 24 }, { 22, 25 }, { 23, 25 }, { 24, 26 }, { 25, 27 } };
		int nbr_edges = edges.length;
		int delay[] = new int[ops];
		for (int i = 0; i < ops; i++) {
			delay[i] = operations[i] == mul ? mul_delay : add_delay;
		}

		// --------------------------

		Store store = new Store();

		// Constraints
		IntVar[] starts_add = new IntVar[add_ops];
		IntVar[] durations_add = new IntVar[add_ops];
		IntVar[] resources_add = new IntVar[add_ops];
		IntVar limit_add = new IntVar(store, "limit", 1, nbr_adders);

		IntVar[] starts_mul = new IntVar[mul_ops];
		IntVar[] durations_mul = new IntVar[mul_ops];
		IntVar[] resources_mul = new IntVar[mul_ops];
		IntVar limit_mul = new IntVar(store, "limit", 1, nbr_muls);

		IntVar[] starts = new IntVar[ops];

		// These are to make sure that the add and mul ops are separated
		IntVar[] o2_mul = new IntVar[mul_ops];
		IntVar[] o2_add = new IntVar[add_ops];
		
		populateVectors(mul_delay, add_delay, nbr_adders, nbr_muls, ops, mul, operations, store, starts_add,
				durations_add, resources_add, starts_mul, durations_mul, resources_mul, starts, o2_mul, o2_add);

		// Need constraints to resolve dependencies
		for (int i = 0; i < nbr_edges; i++) {
			int from = edges[i][0], to = edges[i][1];
			store.impose(new XplusClteqZ(starts[from], delay[from], starts[to]));
		}
		
		// Cost variable - we want to minimize the end time for one of the last ops
		IntVar cost = new IntVar(store, "cost", 0, 100);
		IntVar[] lastOps = new IntVar[last.length];
		// 
		for (int i = 0; i < last.length; i++) {
			int endNode = last[i];
			lastOps[i] = new IntVar(store, "end", );
			store.impose(new XplusCeqZ(starts[endNode], delay[endNode], ));
		}

		store.impose(new Cumulative(starts_add, durations_add, resources_add, limit_add));
		store.impose(new Cumulative(starts_mul, durations_mul, resources_mul, limit_mul));
		store.impose(new Diff2(starts_add, o2_add, durations_add, resources_add));
		store.impose(new Diff2(starts_mul, o2_mul, durations_mul, resources_mul));

		// SelectChoicePoint<IntVar> select = new
		// SimpleMatrixSelect<IntVar>(vars, new SmallestMin<IntVar>(),
		// new MostConstrainedStatic<IntVar>(), new IndomainMin<IntVar>(), 0);

		System.out.println(
				"\nVariable store size: " + store.size() + "\nNumber of constraints: " + store.numberConstraints());

		boolean result = store.consistency();

		System.out.println("1. Constraints consistent = " + result);

		long T1, T2, T;
		T1 = System.currentTimeMillis();

		Search<IntVar> label = new DepthFirstSearch<IntVar>();

		// result = label.labeling(store, select, cost);

		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("\n\t*** Execution time = " + T + " ms");

		if (result) {
			System.out.println("\n*** Yes");
			// PrintSchedule Sch = new PrintSchedule(Ns, Ts, Ds, Rs);
			// System.out.println(Sch);
		} else
			System.out.println("*** No");

	}

	private void populateVectors(int mul_delay, int add_delay, int nbr_adders, int nbr_muls, int ops, int mul,
			int[] operations, Store store, IntVar[] starts_add, IntVar[] durations_add, IntVar[] resources_add,
			IntVar[] starts_mul, IntVar[] durations_mul, IntVar[] resources_mul, IntVar[] starts, IntVar[] o2_mul,
			IntVar[] o2_add) {
		int j = 0, k = 0;
		for (int i = 0; i < ops; i++) {
			starts[i] = new IntVar(store, "start" + i, 0, 100);
			
			// Check if mul or add
			if (operations[i] == mul) {
				// It's mul
				starts_mul[j] = starts[i];
				durations_mul[j] = new IntVar(store, "duration" + i, mul_delay, mul_delay);
				resources_mul[j] = new IntVar(store, "resources" + i, 1, 1);
				o2_mul[j] = new IntVar(store, "resdiff" + i, nbr_adders + 1, nbr_adders + nbr_muls);
				j++;
			} else {
				// It's add
				starts_add[k] = starts[i];
				durations_add[k] = new IntVar(store, "duration" + i, add_delay, add_delay);
				resources_add[k] = new IntVar(store, "resources" + i, 1, 1);
				o2_add[k] = new IntVar(store, "resdiff" + i, 1, nbr_adders);
				k++;
			}
		}
	}

	public static void main(String args[]) {
		new Regression();
	}

}
