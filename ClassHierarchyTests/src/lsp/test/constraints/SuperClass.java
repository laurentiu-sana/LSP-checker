package lsp.test.constraints;

public class SuperClass {
	protected int member;

	public int preconditions(int x) throws Exception {
		if (x < 0)
			throw new Exception();
		return x;
	}

	public int postconditions(int x) {
		if (x < 0)
			x = -x;
		return 0;
	}

	public void invariants(int x) {
		if (x < 10)
			member = (postconditions(x) * 2);
		else
			member = (postconditions(x) * 3);
	}

	public int constantMethod() {
		return 0;
	}

	public String toString() {
		return member + "";
	}

	public static void mainLSPChecker(String[] args) {
		try {
			new SuperClass().preconditions(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		new SuperClass().postconditions(0);
		new SuperClass().invariants(0);
	}
}
