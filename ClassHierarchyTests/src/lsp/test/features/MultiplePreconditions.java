package lsp.test.features;

public class MultiplePreconditions {
	public void multiplePreconditions(SuperClass cl1, SuperClass cl2, int x) {
		cl1.preconditions(x);
		cl2.preconditions(x);
	}

	public void simplePreconditions(SuperClass cl, int x) {
		if (x == 55)
			cl.preconditions(x);
		x = 3 * x;
		if (x < 0)
			cl.preconditions(x);
	}

	public void simpleInterfacePreconditions(MyInterface obj, int x) {
		obj.preconditions(x);
	}

	public static void mainLSPChecker(String[] args) {
		// The methods should be in the reverse order in the class hierarchy,
		// and we need information about the preconditions of the possible
		// virtual methods
		(new SubClass()).preconditions(0);
		(new SuperClass()).preconditions(0);
		(new MultiplePreconditions()).simplePreconditions(new SuperClass(), 0);
		(new MultiplePreconditions()).simplePreconditions(new SubClass(), 0);

		(new MultiplePreconditions()).simpleInterfacePreconditions(
				new SuperClass(), 0);
		(new MultiplePreconditions()).simpleInterfacePreconditions(
				new SubClass(), 0);

		// FIXME: not working :-(
		//(new MultiplePreconditions()).multiplePreconditions(new SuperClass(),
		//		new SuperClass(), 0);
	}
}
