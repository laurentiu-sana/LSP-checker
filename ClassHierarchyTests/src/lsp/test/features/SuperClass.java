package lsp.test.features;

public class SuperClass implements MyInterface {
	@Override
	public void preconditions(int x) {
		if (x < 10)
			throw new RuntimeException();
	}

	@Override
	public int postconditions(int x) {
		return 2 * x;
	}
}
