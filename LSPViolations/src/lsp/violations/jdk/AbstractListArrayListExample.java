package lsp.violations.jdk;

import java.util.AbstractList;
import java.util.ArrayList;

public class AbstractListArrayListExample {
	public void foo(AbstractList<Object> list) {
		list.add(new Integer(0));
		list.remove(0);
	}

	public static void main() {
		try {
			new AbstractListArrayListExample().foo(new ArrayList<Object>());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			new AbstractListArrayListExample().foo(new MyList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		main();
	}
}
