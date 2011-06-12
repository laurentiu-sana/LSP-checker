package lsp.jpf.test.stateful.dummy;

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

}
