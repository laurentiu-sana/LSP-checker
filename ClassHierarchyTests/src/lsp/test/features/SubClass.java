package lsp.test.features;

public class SubClass extends SuperClass {
	@Override
	public void preconditions(int x) {
		if (x < 1000)
			throw new RuntimeException();
	}

	@Override
	public int postconditions(int x) {
		return x * x;
	}
}
