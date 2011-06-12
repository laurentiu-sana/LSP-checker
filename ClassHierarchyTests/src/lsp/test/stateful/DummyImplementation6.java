package lsp.test.stateful;

public class DummyImplementation6 extends DummyImplementation5 {
	@Override
	public void preconditions(int x) {
		if (x >= 777)
			throw new RuntimeException();
	}
}
