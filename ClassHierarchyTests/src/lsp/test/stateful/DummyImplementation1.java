package lsp.test.stateful;

public class DummyImplementation1 implements DummyInterface {
	protected int member;

	protected void foo() {
		this.member = -1;
	}

	@Override
	public int getResults() {
		foo();
		return 0;
	}

	@Override
	public int postconditions(int x) {
		return x;
	}

	@Override
	public void preconditions(int x) {
		if (x <= 0)
			throw new RuntimeException();
	}
}
