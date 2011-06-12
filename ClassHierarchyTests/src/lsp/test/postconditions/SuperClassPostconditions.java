package lsp.test.postconditions;

public class SuperClassPostconditions implements InterfacePostconditions {
	@Override
	public int doWork(int x) {
		return x + x;
	}
}
