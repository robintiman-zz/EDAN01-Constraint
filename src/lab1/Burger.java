package lab1;

import org.jacop.constraints.XeqY;
import org.jacop.constraints.XltC;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;

public class Burger {
	
	public Burger() {
		Store store = new Store();
		IntVar sod = new IntVar(store, "sod", 0,3000);
		IntVar fat = new IntVar(store, "fat", 0,150);
		IntVar cal = new IntVar(store, "cal", 0,3000);
		IntVar ket = new IntVar(store, "ket");
		IntVar let = new IntVar(store, "let");
		IntVar pic = new IntVar(store, "pic");
		IntVar tom = new IntVar(store, "tom");
		IntVar cost;
		
		
		store.impose(new XeqY(ket, let));
		store.impose(new XeqY(pic, tom));
		store.impose(new XltC(sod, 3000));
		store.impose(new XltC(fat, 150));
		store.impose(new XltC(cal, 3000));
		
		DepthFirstSearch<IntVar> dfs = new DepthFirstSearch<IntVar>();
		
		
		
	}
	
}
