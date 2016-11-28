package lab2_1;


import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;
import java.util.*;

public class Logistics {
    public static void main(String[] args) {
        // Input variables 1
        // int graph_size = 6;
        // int start = 1;
        // int n_dests = 1;
        // int [] dest = {6};
        // int n_edges = 7;
        // int [] from = {1,1,2,2,3,4,4};
        // int [] to = {2,3,3,4,5,5,6};
        // int [] cost = {4,2,5,10,3,4,11};

        // // Input variables 2
        // int graph_size = 6;
        // int start = 1;
        // int n_dests = 2;
        // int [] dest = {5,6};
        // int n_edges = 7;
        // int [] from = {1,1,2, 2,3,4, 4};
        // int [] to = {2,3,3, 4,5,5, 6};
        // int [] cost = {4,2,5,10,3,4,11};

        // // Input variables 3
        int graph_size = 6;
        int start = 1;
        int n_dests = 2;
        int [] dest = {5,6};
        int n_edges = 9;
        int [] from = {1,1,1,2,2,3,3,3,4};
        int [] to = {2,3,4,3,5,4,5,6,6};
        int [] cost = {6,1,5,5,3,5,6,4,2};

        Store store = new Store();
        IntVar totalCost = new IntVar(store, "totalCost", -1000, 1000);


        IntVar[][] edges = new IntVar[n_dests][graph_size];
        for(int i = 0; i < n_dests; i++) {
            for(int j = 0; j < graph_size; j++) {
                // Create new intVar
                edges[i][j] = new IntVar(store, "edge_" + (i+1) + "_" + (j+1));

                if (j != (start - 1)){
                    // If not destination, add itself to domain
                    if (j != dest[i]-1) {
                            edges[i][j].addDom(j+1, j+1);
                    }
                    edges[i][j].addDom(1,1);
                }
            }
            store.impose(new Subcircuit(edges[i]));
        }
        //  Add edges to domains of intvars
        for (int i = 0; i < n_edges; i++) {
            for(int j = 0; j < n_dests; j++) {
                edges[j][to[i] - 1].addDom(from[i], from[i]);
                edges[j][from[i] - 1].addDom(to[i], to[i]);
            }
        }

        // Specifies whether edge number i is included in solution
        BooleanVar[] edgeIncluded = new BooleanVar[n_edges];
        for (int i = 0; i < n_edges; i++) {
            edgeIncluded[i] = new BooleanVar(store, "Edge_"+i);
        }

        for(int j = 0; j < n_edges; j++) {
            ArrayList<PrimitiveConstraint> constraints = new ArrayList<PrimitiveConstraint>();
            for(int i = 0; i < n_dests; i++) {
                constraints.add(new XeqC(edges[i][from[j] - 1], to[j]));
                constraints.add(new XeqC(edges[i][to[j] - 1], from[j]));
            }
            // If edge is included in any of the subcircuits, set value of edgeIncluded to 1
            store.impose(new Reified(new Or(constraints), edgeIncluded[j]));
        }

        store.impose(new SumWeight(edgeIncluded, cost, totalCost));


        //() Minimize cost
        Search<IntVar> search = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleMatrixSelect<IntVar>(edges, new SmallestMax<IntVar>(), new IndomainMin<IntVar>());

        search.setSolutionListener(new PrintOutListener<IntVar>());

        boolean result = search.labeling(store, select, totalCost);
        if (result) {
			System.out.println("\n*** Yes");
			System.out.println("Solution (included edges) : " + java.util.Arrays.asList(edgeIncluded));
			System.out.println("The total cost is: " + (totalCost.value()));
		}
		else System.out.println("\n*** No");
    }
}