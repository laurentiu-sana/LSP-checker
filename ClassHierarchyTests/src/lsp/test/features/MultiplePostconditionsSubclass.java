package lsp.test.features;

public class MultiplePostconditionsSubclass extends MultiplePostconditionsSuperclass {
	@Override
	public void multiplePostconditions(SuperClass cl1, SuperClass cl2, int x) {
		cl1.postconditions(x);
		cl2.postconditions(x);
	}

	@Override
	public int simplePostconditions(SuperClass cl, int x) {
		if (x == 55)
			return cl.postconditions(x);
		x = 3 * x;
		if (x < 0)
			return cl.postconditions(x);
		return x * x * x;
	}

	@Override
	public int simpleInterfacePostconditions(MyInterface obj, int x) {
		return obj.postconditions(x) + 1;
	}

	public static void mainLSPChecker(String[] args) {
		// The methods should be in the reverse order in the class hierarchy,
		// and we need information about the postconditions of the possible
		// virtual methods

		(new SubClass()).postconditions(0);

		(new MultiplePostconditionsSubclass()).simplePostconditions(
				new SubClass(), 0);
		(new MultiplePostconditionsSubclass()).simpleInterfacePostconditions(
				new SubClass(), 0);

		// FIXME: not working :-(
		//(new MultiplePostconditions()).multiplePostconditions(new SuperClass(),
		//		new SuperClass(), 0);
	}
}
