package lsp.test.features;

public class DeepPostconditions {
	public int foo(int x) {
		if (x < 0)
			return bar(x);
		return 2 * x;
	}

	public int bar(int x) {
		if (x < 10)
			return 3 * x;
		return 4 * x;
	}

	public static void mainLSPChecker(String[] args) {
		(new DeepPostconditions()).foo(0);
	}
}
