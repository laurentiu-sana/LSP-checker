package lsp.test.sav;

public class Subclass extends Superclass {
	public Subclass() {

	}

	@Override
	public void precond1(int x) {
		if (x < 10)
			throw new RuntimeException();
	}

	@Override
	public void precond2(int x, int y) {
		throw new RuntimeException();
	}

	@Override
	public void precond3(int x) {
		if (x == 0)
			throw new RuntimeException();
	}

	@Override
	public int postcond1(int x, int y) {
		if (x + y > 0 && x > 0)
			return x;
		if (x < 0 && y > 0)
			return 2 * x;
		return y;
	}

	@Override
	public int postcond2(int x) {
		if (x < 0)
			return -x - 1;
		return x - 1;
	}
	
	@Override
	public int postcond3(int x) {
		if (x < 0)
			return -x + 5;
		if (x < 10)
			return x + 10;
		return x + 200;
	}
}
