package lsp.test.savdemopostcond;

public class SuperClassPostconditions implements InterfacePostconditions {
	@Override
	public int doWork(int x) {
		return x + x;
	}
}
