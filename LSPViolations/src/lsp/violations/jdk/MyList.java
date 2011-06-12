package lsp.violations.jdk;

import java.util.AbstractList;

public class MyList extends AbstractList<Object> {
	@Override
	public int size() {
		return 0;
	}

	@Override
	public Object get(int index) {
		return null;
	}
}
