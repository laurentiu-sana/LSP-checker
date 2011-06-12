package lsp.jpf.test;

public class PostconditionsSubClass extends PostconditionsSuperClass {
	@Override
	public int foo(int x, int y) {
		if (x > 0)
			y = x;
		else
			y = 2 * x;
		return 2 * y;
	}
	
	public static void main(String[] args) {
		(new PostconditionsSubClass()).foo(0, 0);
	}
}
