import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.ListIterator;

class MyNonIterableList extends AbstractSequentialList<Object> {
	@Override
	public ListIterator<Object> listIterator(int arg0) {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}	
}

class MyEmptyList extends AbstractList<Object> {

	@Override
	public Object get(int arg0) {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}
	
	@Override
	public boolean add(Object o) {
		return false;
	}
}

public class Violation4 {
	public boolean foo(AbstractList<Object> collection) {
		return collection.listIterator() == null;
	}

	public static void main(String[] args) {
		System.out.println("Instance #1: " + new Violation4().foo(new MyNonIterableList()));
		System.out.println("Instance #2: " + new Violation4().foo(new MyEmptyList()));
	}
}
