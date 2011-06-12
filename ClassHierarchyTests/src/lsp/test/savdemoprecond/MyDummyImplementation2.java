package lsp.test.savdemoprecond;

public class MyDummyImplementation2 implements MyDummyInterface {

	@Override
	public int doWork() {
		return 10;
	}

	@Override
	public void check(int x) {
		if (x > 10)
			throw new RuntimeException();
	}
}
