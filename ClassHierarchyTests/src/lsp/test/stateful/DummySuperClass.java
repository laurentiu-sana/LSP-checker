package lsp.test.stateful;


public class DummySuperClass {
	public DummySuperClass() {

	}

	protected void privatePreconditions(int x) throws Exception {
		if (x > 1000)
			throw new Exception();
	}

	public int testPreconditions(int x) throws Exception {
		if (x < 0)
			throw new Exception();
		privatePreconditions(x);
		return x;
	}

	public void preconditions(int x, int y) throws Exception {
		assert testPreconditions(x) > 100;
	}

	protected int bar(int x) {
		if (x <= 0)
			return -x;
		return x;
	}

	public int foo(int x, int y) {
		if (bar(x) < y)
			return x;
		return y;
	}

	/** Real-life invariants */
	protected int member = 0;
	protected String key, value;

	public boolean isAlive() {
		if (member == 0)
			return true;
		return member == 0;
	}

	public int isAlive(int x, int y) {
		if (x >= 0)
			return x;
		return -x;
	}

	public int getMember() {
		return member;
	}

	@Override
	public String toString() {
		return "key=|" + key + "|, value=|" + value + "|";
	}

	public boolean isSet() {
		if (isAlive())
			return member != 0;
		if (member == 100)
			member++;
		return false;
	}

	public int inv1() {
		return inv2() + 1;
	}

	public int inv2() {
		return inv3() + 2;
	}

	public int inv3() {
		member = 1;
		return member;
	}

	public int inv4() {
		if (DummyConstants.getConfiguration() != null)
			return 0;
		return -1;
	}

	public int inv5() {
		DummyConstants.doSomething();
		return 0;
	}

	public String inv6() {
		DummyConstants.computeScore(0, 0);
		return "Hello World!";
	}

	public int inv7(DummyInterface di) {
		/** Currently dropped because hierarchy analyzing is not implemented */
		if (di != null)
			return di.getResults();
		return 0;
	}

	public int inv8(DummyImplementation3 di) {
		/** Currently dropped because hierarchy analyzing is not implemented */
		if (di != null)
			di.getResults();
		return 0;
	}

	public static void mainLSPChecker(String[] args) {
		try {
			(new DummySuperClass()).preconditions(0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		(new DummySuperClass()).foo(0, 0);

		(new DummySuperClass()).isAlive();
		(new DummySuperClass()).isAlive(0, 0);

		(new DummySuperClass()).isSet();
		(new DummySuperClass()).toString();

		(new DummySuperClass()).getMember();

		(new DummySuperClass()).inv1();
		(new DummySuperClass()).inv2();
		(new DummySuperClass()).inv3();
		(new DummySuperClass()).inv4();
		(new DummySuperClass()).inv5();
		(new DummySuperClass()).inv6();

		new DummyImplementation1();
		new DummyImplementation2();
		new DummyImplementation3();
		new DummyImplementation4();

		(new DummySuperClass()).inv7(null);
		(new DummySuperClass()).inv8(null);
	}
}
