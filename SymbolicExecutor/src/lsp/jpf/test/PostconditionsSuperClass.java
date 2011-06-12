package lsp.jpf.test;

public class PostconditionsSuperClass {
	public int foo(int x, int y) {
		int z = x + y;
		if (z > 0) {
			z = 1;
			z = z - x;
		} else {
		}
		z = 2 * z;
		return z;
	}

	public static void main(String[] args) {
		(new PostconditionsSuperClass()).foo(0, 0);
	}
}
