package lsp.test.stateful;

public class DummySubClass extends DummySuperClass {
	@Override
	protected int bar(int x) {
		if (x <= 0)
			return 0;
		return x;
	}

	@Override
	public int foo(int x, int y) {
		if (bar(x) < x)
			return x;
		return y;
	}

	public void preconditions(DummyBaseInterface bi, int x) {
		bi.preconditions(x);
	}

	public int postconditions(DummyBaseInterface bi, int x) {
		if (x > 1000)
			return x;
		return bi.postconditions(x);
	}

	public static void mainLSPChecker(String[] args) {
		(new DummySubClass()).foo(0, 0);

		// Instantiate all the objects + their methods
		(new DummyImplementation1()).preconditions(0);
		(new DummyImplementation2()).preconditions(0);
		(new DummyImplementation3()).preconditions(0);
		(new DummyImplementation4()).preconditions(0);
		(new DummyImplementation5()).preconditions(0);
		(new DummyImplementation6()).preconditions(0);

		(new DummySubClass()).preconditions(new DummyImplementation1(), 0);
		(new DummySubClass()).preconditions(new DummyImplementation5(), 0);

		//(new DummySubClass()).postconditions(null, 0);
	}
}
