import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

public class Games {
	public static void main(String[] args) {
		long T1, T2, T;
		T1 = System.currentTimeMillis();
		send();
		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("\n\t*** Execution time = " + T + " ms");
	}

	static void send() {
		// States whether skipping at most one game should be allowed
		boolean skip = true;

		// Input 1
		//int num = 4;
		//int cap = 5;
		//int refill = 2;
		//int[] fun = {4, 1, 2, 3};

		// Input 2
		//int num = 4;
		//int cap = 5;
		//int refill = 2;
		//int[] fun = {4, -1, -2, 3};

		// Input 3
		 int num = 5;
		 int cap = 3;
		 int refill = 2;
		 int[] fun = {4, 1, -2, 3, 4};


		Store store = new Store();
		IntVar[] games = new IntVar[num];
		IntVar[] tokens = new IntVar[num];

		// Introduce helper variable refillVar (to simplify constraints later)
		IntVar capVar = new IntVar(store, "cap", cap, cap);
		store.impose(new XeqC(capVar, cap));

		// Initialize variables
		for(int i = 0; i < num; i++) {
			games[i] = new IntVar(store, "G" + (i+1), 0, cap);
			tokens[i] = new IntVar(store, "T" + (i+1), 0, cap);
		}


		IntVar count;
		if(skip) {
			count = new IntVar(store, "count", 0, 1);
		} else {
			count = new IntVar(store, "count", 0, 0);
		}
		store.impose(new Among(games, new IntervalDomain(0,0), count));

		// Initialize constraints for i=0
		store.impose(new XlteqY(games[0], tokens[0]));
		store.impose(new XeqC(tokens[0], cap));

		// Initialize constraints
		for(int i = 1; i < num; i++) {
			// g[i] <= t[i]
			store.impose(new XlteqY(games[i], tokens[i]));

			// t[i-1] - g[i-1] + refill = t[i] =>
			// refill = t[i] - t[i-1] + g[i-1]
			IntVar temp = new IntVar(store, "Diff" + (i+1), 0, cap+refill);
			store.impose(new LinearInt(store,
			new IntVar[] { temp, tokens[i-1], games[i-1]},
			new int[] { 1, -1, 1 },
			"==",
			refill));
			IntVar[] ls = { temp, capVar };
			store.impose(new Min(ls, tokens[i]));
		}

		// Search for solution
		System.out.println("Number of variables: "+ store.size() +
		"\nNumber of constraints: " + store.numberConstraints());

		Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(games,
		null,
		new IndomainMin<IntVar>());
		search.setSolutionListener(new PrintOutListener<IntVar>());
		IntVar funSum = new IntVar(store, "funSum", -1000, 1000);

		// Create an array to be able to do a LinearInt constraint as:
		// G1 * F1 + G2 * F2 +... + funSum * 1 == 0
		IntVar[] tempArr = new IntVar[num + 1];
		int[] tempFun = new int[num + 1];

		for(int i = 0; i < num; i++) {
			tempArr[i] = games[i];
			tempFun[i] = fun[i];
		}

		tempArr[num] = funSum;
		tempFun[num] = 1;
		store.impose(new LinearInt(store, tempArr, tempFun, "==", 0));

		boolean result = search.labeling(store, select, funSum);

		if (result) {
			System.out.println("\n*** Yes");
			System.out.println("Solution : "+ java.util.Arrays.asList(games));
			System.out.println("Tokens at start : "+ java.util.Arrays.asList(tokens));
			System.out.println("The funSum is: " + (-funSum.value()));
		}
		else System.out.println("\n*** No");
	}
}
