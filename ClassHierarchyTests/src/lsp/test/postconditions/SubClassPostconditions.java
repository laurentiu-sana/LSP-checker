package lsp.test.postconditions;

public class SubClassPostconditions extends SuperClassPostconditions {
	@Override
	public int doWork(int x) {
		return -x;
	}
}
