package lsp.test.constraints;

public class SubClass extends SuperClass {
	@Override
	public int preconditions(int x) throws Exception {
		if (x < 99)
			throw new Exception("Exception");
		return x;
	}

	@Override
	public int postconditions(int x) {
		return x;
	}

	@Override
	public void invariants(int x) {
		member = x;
	}

	@Override
	public int constantMethod() {
		invariants(0);
		return 0;
	}

	public static void mainLSPChecker(String[] args) {
		try {
			new SubClass().preconditions(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		new SubClass().postconditions(0);
		new SubClass().invariants(0);
	}
}
