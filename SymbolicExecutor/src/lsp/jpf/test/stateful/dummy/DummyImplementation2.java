package lsp.jpf.test.stateful.dummy;

public class DummyImplementation2 extends DummyImplementation1 {
	@Override
	public int getResults() {
		super.foo();
		return 1;
	}
	
	public int bar() {
		return  0;
	}
}
