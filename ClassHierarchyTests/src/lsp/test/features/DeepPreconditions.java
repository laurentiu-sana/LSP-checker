package lsp.test.features;

public class DeepPreconditions {
	public int bar(int x) {
		if (x < 0)
			throw new RuntimeException();
		return -x;
	}

	public void foo(int x) {
		if (x < 10) {
			if (bar(x) >= x)
				throw new RuntimeException();
		}
	}

	public static void mainLSPChecker(String[] args) {
		(new DeepPreconditions()).bar(0);
		(new DeepPreconditions()).foo(0);
	}
}
