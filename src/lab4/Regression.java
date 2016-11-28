package lab4;

import java.util.ArrayList;

import org.jacop.constraints.Constraint;
import org.jacop.constraints.Cumulative;
import org.jacop.constraints.Diff2;
import org.jacop.constraints.Rectangle;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.MostConstrainedStatic;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleMatrixSelect;
import org.jacop.search.SmallestMin;
import org.jacop.ui.PrintSchedule;

public class Regression {

	public Regression() {
		// Constants
		int mul_length = 2, add_length = 1;
		int nbr_adders = 1, nbr_muls = 1;
		int mul_ops = 16, add_ops = 12, ops = mul_ops + add_ops;
		int res = 2;
		int nbr_edges = 30;
		ArrayList<int[]> edgeList = new ArrayList<int[]>();
		// 1 means multiplication, 0 means addition
		int[] muladd = { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0 };
		int[][] edges = { { 0, 8 }, { 1, 8 }, { 2, 9 }, { 3, 9 }, { 4, 10 }, { 5, 10 }, { 6, 11 }, { 7, 11 }, { 8, 26 },
				{ 9, 12 }, { 10, 13 }, { 11, 27 }, { 12, 14 }, { 12, 16 }, { 13, 15 }, { 13, 17 }, { 14, 18 },
				{ 15, 18 }, { 16, 19 }, { 17, 19 }, { 18, 20 }, { 18, 22 }, { 19, 21 }, { 19, 23 }, { 20, 24 },
				{ 21, 24 }, { 22, 25 }, { 23, 25 }, { 24, 26 }, { 25, 27 } };

		Store store = new Store();

		// Constraints
		IntVar[] starts_add = new IntVar[add_ops];
		IntVar[] durations_add = new IntVar[add_ops];
		IntVar[] resources_add = new IntVar[add_ops];
		IntVar limit_add = new IntVar(store, "limit", 0, nbr_adders);
		
		IntVar[] starts_mul = new IntVar[mul_ops];
		IntVar[] durations_mul = new IntVar[mul_ops];
		IntVar[] resources_mul = new IntVar[mul_ops];
		IntVar limit_mul = new IntVar(store, "limit", 0, nbr_muls);
		
		
		for (int i = 0; i < ops; i++) {
						
			// Check if mul or add
			if (muladd[i] == 1) {
				// It's mul
				starts_add[i] = new IntVar(store, "start" + i, 0, 100);
				durations_add[i] = new IntVar(store, "duration" + i, 1, add_length);
				resources_add[i] = new IntVar(store, "resources" + i, 1, 1);
				
			} else {
				// It's add
				
			}
			
			IntVar[] length = { new IntVar(store, 1, dur), new IntVar(store, 1, 1) };
			IntVar[] origin = { new IntVar(store), new IntVar(store) };
			recs[i] = new Rectangle(length, origin);

		}

		// Cumulative is used to schedule, Diff2 is used to make sure that a mul
		// doesn't get
		// assigned to an add resource and vice versa.
		Constraint cum = new Cumulative(starts, durations, resources, limit);
		Constraint diff = new Diff2(recs, exclusive, true);
		store.impose(cum);
		store.impose(diff);
		
		

		SelectChoicePoint<IntVar> select = new SimpleMatrixSelect<IntVar>(vars, new SmallestMin<IntVar>(),
				new MostConstrainedStatic<IntVar>(), new IndomainMin<IntVar>(), 0);

		System.out.println(
				"\nVariable store size: " + store.size() + "\nNumber of constraints: " + store.numberConstraints());

		boolean result = store.consistency();

		System.out.println("1. Constraints consistent = " + result);

		long T1, T2, T;
		T1 = System.currentTimeMillis();

		Search<IntVar> label = new DepthFirstSearch<IntVar>();

		result = label.labeling(store, select, cost);

		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("\n\t*** Execution time = " + T + " ms");

		if (result) {
			System.out.println("\n*** Yes");
			PrintSchedule Sch = new PrintSchedule(Ns, Ts, Ds, Rs);
			System.out.println(Sch);
		} else
			System.out.println("*** No");

	}

	public static void main(String[] args) {
		new Regression();
	}

}
