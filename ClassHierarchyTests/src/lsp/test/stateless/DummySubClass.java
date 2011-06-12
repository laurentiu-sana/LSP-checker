package lsp.test.stateless;

public class DummySubClass extends DummySuperClass {
	public DummySubClass() {

	}

	/** Preconditions */
	@Override
	public int preconditions(int x) throws Exception {
		if (x < 10)
			throw new Exception();
		return x;
	}

	/** Postconditions */
	@Override
	public int postconditions(int x, int y) {
		if (x > y)
			return x * x;
		return y * y;
	}

	/** Invariants */
	@Override
	public int invariants(int x) {
		if (x + x > 100) {
			member = x;
			return x;
		}
		return 0;
	}

	@Override
	public boolean isSet() {
		member = 0;
		return member == 0;
	}

	public int getMember() {
		return member;
	}

	public boolean isSetFake() {
		boolean result = member == 1;
		member = 1;
		return result;
	}

	public static void mainLSPChecker2(String[] args) {
		/**
		 * FIXME: currently, I have to write "by hand" the method calls
		 * with default values to parameters (e.g. 0 for int)
		 * 
		 * In the next version, I'll use <soot> to generate the main method.
		 * The symbolic executed methods are in the configuration file: <default.xml>
		 */
		if (args.length <= 0) {
			System.err.println("Expected method's name!");
			return;
		}

		if (args[0].equals("preconditions")) {
			/** Testing preconditions */
			try {
				new DummySubClass().preconditions(0);
			} catch (Exception e) {

			}
		}

		if (args[0].equals("postconditions")) {
			/** Testing postconditions */
			new DummySubClass().postconditions(0, 0);
		}

		if (args[0].equals("invariants")) {
			/** Testing invariants */
			new DummySubClass().invariants(0);
		}
	}

	public static void mainLSPChecker(String[] args) {
		/** Testing preconditions */
		try {
			new DummySubClass().preconditions(0);
		} catch (Exception e) {

		}

		/** Testing postconditions */
		new DummySubClass().postconditions(0, 0);

		/** Testing invariants */
		new DummySubClass().invariants(0);
	}
}
