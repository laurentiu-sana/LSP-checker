package lsp.test.savdemopostcond;

public class SubClass extends SuperClass {
	@Override
	public int postcond(int x) {
		if (x < 0)
			return -x;
		return x;
	}

}
