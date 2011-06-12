package lsp.test.sav;

public class Superclass {
	public Superclass() {

	}

	public void precond1(int x) {
		if (x < 0)
			throw new RuntimeException();
	}
	
	public void precond2(int x, int y) {
		if (2 * x < y)
			throw new RuntimeException();
		
		x++;
		if (x > y - 10)
			throw new RuntimeException();
	}
	
	public void precond3(int x) {
		throw new RuntimeException();
	}
	
	public int postcond1(int x, int y) {
		if (x - y > 0)
			return y;
		return x;
	}
	
	public int postcond2(int x) {
		if (x < 0)
			return -x;
		return x;
	}
	
	public int postcond3(int x) {
		if (x < 0)
			return -x + 10;
		if (x < 10)
			return x + 15;
		return x + 100;
	}
}
