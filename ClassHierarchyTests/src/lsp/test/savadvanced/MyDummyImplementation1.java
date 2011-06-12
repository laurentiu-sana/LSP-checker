package lsp.test.savadvanced;

public class MyDummyImplementation1 implements MyDummyInterface {
	@Override
	public int doWork() {
		return -5;
	}
	
	public void check(int x) {
		if (x < 0)
			throw new RuntimeException();
	}
}
