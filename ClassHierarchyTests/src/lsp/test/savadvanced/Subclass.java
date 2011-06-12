package lsp.test.savadvanced;

public class Subclass extends Superclass {
	private int member;
	
	public Subclass() {

	}

	/*
	@Override
	public int advanced(MyDummyInterface di) {
		int aux = di.doWork();
		if (aux < 10)
			throw new RuntimeException();
		member1 = 100;
		return aux;
	}
	*/
	
	@Override
	public void precond(MyDummyInterface di) {
		di.check(member);
		if (member > 5 && member < 7)
			throw new RuntimeException();
	}	
}
