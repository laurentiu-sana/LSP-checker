package lsp.test.savdemopostcond;

public class SubClassPostconditions extends SuperClassPostconditions {
	@Override
	public int doWork(int x) {
		return -x;
	}
}
