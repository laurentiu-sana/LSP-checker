package lsp.test.stateful;

public class DummyImplementation5 implements DummyBaseInterface {

	@Override
	public int postconditions(int x) {
		return 0;
	}

	@Override
	public void preconditions(int x) {
		if (x > 9999)
			throw new RuntimeException("Testing the LSP checker");
	}
}
