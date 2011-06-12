package lsp.test.savadvanced;

public class Superclass {
	private int member;
	
	public Superclass() {

	}

	/*
	public void basic() {
		member1 = 0;
	}

	public int medium(MyDummyInterface di) {
		return di.doWork();
	}

	public int advanced(MyDummyInterface di) {
		int aux = di.doWork();
		if (aux < 0)
			throw new RuntimeException();
		member1 = 1;
		return aux;
	}*/
	
	public void precond(MyDummyInterface di) {
		di.check(member);
	}
}
