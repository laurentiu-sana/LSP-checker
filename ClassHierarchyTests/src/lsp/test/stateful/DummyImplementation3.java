package lsp.test.stateful;

public class DummyImplementation3 extends DummyImplementation2 {
	@Override
	public int getResults() {
		return 100;
	}

	@Override
	public int postconditions(int x) {
		return 0;
	}
	

	@Override
	public void preconditions(int x) {
		if (x < 0)
			throw new RuntimeException();
	}

}
