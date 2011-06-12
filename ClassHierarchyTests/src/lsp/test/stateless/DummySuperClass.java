package lsp.test.stateless;

public class DummySuperClass {
	public DummySuperClass() {

	}

	/** Preconditions */
	public int preconditions(int x) throws Exception {
		if (x < 0)
			throw new Exception();
		if (x > 100)
			throw new Exception();

		assert x > 50;

		if (x > 30)
			throw new Exception();
		return x;
	}

	/** Postconditions */
	public int postconditions(int x, int y) {
		if (x > y)
			return 2 * x;
		return 2 * y;
	}

	/** Invariants */
	protected int member = 0;

	public int invariants(int x) {
		if (x > 0) {
			member = x;
			return 0;
		}
		return x + x;
	}

	public boolean isSet() {
		return member == 0;
	}

	public int getMember() {
		return member;
	}

	public boolean isSetFake() {
		return isSet();
	}

	/**
	 * FIXME: currently, I have to write "by hand" the method calls
	 * with default values to parameters (e.g. 0 for int)
	 * 
	 * In the next version, I'll use <soot> to generate the main method.
	 * The symbolic executed methods are in the configuration file: <default.xml>
	 */
	public static void mainLSPChecker2(String[] args) {
		// Change to main() if you would like to test the methods separately
		if (args.length <= 0) {
			System.err.println("Expected method's name!");
			return;
		}

		if (args[0].equals("preconditions")) {
			/** Testing preconditions */
			try {
				new DummySuperClass().preconditions(0);
			} catch (Exception e) {

			}
		}

		if (args[0].equals("postconditions")) {
			/** Testing postconditions */
			new DummySuperClass().postconditions(0, 0);
		}

		if (args[0].equals("invariants")) {
			/** Testing invariants */
			new DummySuperClass().invariants(0);
		}
	}

	public static void mainLSPChecker(String[] args) {
		/** Testing preconditions */
		try {
			new DummySuperClass().preconditions(0);
		} catch (Exception e) {

		}

		/** Testing postconditions */
		new DummySuperClass().postconditions(0, 0);

		/** Testing invariants */
		new DummySuperClass().invariants(0);
	}
}
