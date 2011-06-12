package lsp.test.savdemoprecond;

public class SAVSubClass extends SAVSuperClass {
	@Override
	public void precond1(int x) {
		if (x < 10)
			throw new RuntimeException();
	}
	
	@Override
	public void precond2(int x, int y) {
		if (x + y > 100)
			throw new RuntimeException();
	}
	
	private int member;
	@Override
	public void precond3(MyDummyInterface di) {
		di.check(member);
		if (member > 5 && member < 7)
			throw new RuntimeException();
	}	
}
