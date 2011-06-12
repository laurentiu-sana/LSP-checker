package lsp.jpf.test;

/** Invariants example */
public class InvariantsSubClass extends InvariantsSuperClass {
	@Override
	public int invariants(int x, int y) {
		if (y > x)
			member = x;
		else
			member = y;
		return 0;
	}

	@Override
	public boolean isSet() {
		return false;
	}

	@Override
	public boolean instanceOk() {
		return false;
	}

	@Override
	public int getMember() {
		return -member;
	}

	@Override
	public boolean isSetFake() {
		member = 1;
		return member != 0;
	}

	public static void main(String[] args) {
		(new InvariantsSubClass()).invariants(0, 0);
	}
}
