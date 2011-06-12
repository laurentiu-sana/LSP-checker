package lsp.test.stateful;

public class DummyImplementation2 extends DummyImplementation1 {
	@Override
	public int getResults() {
		super.foo();
		return 1;
	}

	public int bar() {
		return 0;
	}

	@Override
	public int postconditions(int x) {
		return x * x;
	}

	@Override
	public void preconditions(int x) {
		if (x <= 100)
			throw new RuntimeException();
	}
}
