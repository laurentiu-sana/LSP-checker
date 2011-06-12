package lsp.jpf.test.stateful.dummy;

public class DummySubClass extends DummySuperClass {
	@Override
	protected int bar(int z) {
		if (z <= 0)
			return 0;
		return z;
	}

	@Override
	public int foo(int x, int y) {
		if (bar(x + y) < x)
			return x;
		return y;
	}

	public static void main(String[] args) {
		(new DummySubClass()).foo(0, 0);
	}
}
