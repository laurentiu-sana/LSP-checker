package lsp.jpf.test;

public class InvariantsSuperClass {
	/** Invariants example */
	protected int member = 0;

	public int invariants(int x, int y) {
		int z = x - y;
		if (z > x)
			member = x;
		else
			member = y;
		return 0;
	}

	public boolean isSet() {
		return member != 0;
	}

	public boolean instanceOk() {
		return true;
	}

	public boolean isSetFake() {
		return false;
	}

	public int getMember() {
		return member;
	}

	public int getMemberFake() {
		member = 1;
		return getMember();
	}
	
	public static void main(String[] args) {
		(new InvariantsSuperClass()).invariants(0, 0);
	}
}
