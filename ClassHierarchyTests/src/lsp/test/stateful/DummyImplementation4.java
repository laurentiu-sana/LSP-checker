package lsp.test.stateful;

public class DummyImplementation4 extends DummyImplementation3 {
	@Override
	public int getResults() {
		return super.getResults();
	}

	@Override
	public int postconditions(int x) {
		return x + x;
	}

	@Override
	public void preconditions(int x) {
		if (x >= 50)
			throw new RuntimeException();
	}
}
