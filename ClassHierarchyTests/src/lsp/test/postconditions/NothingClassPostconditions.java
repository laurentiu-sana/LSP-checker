package lsp.test.postconditions;

public class NothingClassPostconditions extends SubClassPostconditions {
	@Override
	public int doWork(int x) {
		return x * 100;
	}
}
