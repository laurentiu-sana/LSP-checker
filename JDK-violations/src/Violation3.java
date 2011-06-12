class ListFullOfNulls extends java.util.AbstractList<Object> {
	@Override
	public int size() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object get(int index) {
		return null;
	}
}

public class Violation3 {
	public void foo(java.util.AbstractList<Object> list) {
		list.add(new Integer(0));
		list.remove(0);
	}

	public static void main(String[] args) {
		new Violation3().foo(new java.util.ArrayList<Object>());
		new Violation3().foo(new ListFullOfNulls());
	}
}
