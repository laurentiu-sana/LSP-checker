package lsp.test.savdemoprecond;

public class SAVSuperClass {
	public void precond1(int x) {
		if (x < 5)
			throw new RuntimeException();
	}
	
	public void precond2(int x, int y) {
		precond1(y);
		if (x < 10)
			throw new RuntimeException();
	}
	
	private int member;	
	public void precond3(MyDummyInterface di) {
		di.check(member);
	}
}
