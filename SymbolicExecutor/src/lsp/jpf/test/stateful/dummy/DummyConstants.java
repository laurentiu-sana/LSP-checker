package lsp.jpf.test.stateful.dummy;

public class DummyConstants {
	private static int member = 0;

	public static Object getConfiguration() {
		return new Object();
	}

	private static int getValue(int x) {
		if (x > 0)
			return 2 * x;
		return -3 * x;
	}

	public static int computeScore(int x, int y) {
		getConfiguration();
		if (x < y) {
			//doSomething();
			getValue(x);
		}
		return x + y;
	}

	public static void doSomething() {
		/** Method with side-effects */
		member++;
	}
}
