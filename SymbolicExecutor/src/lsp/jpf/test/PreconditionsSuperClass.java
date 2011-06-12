package lsp.jpf.test;

public class PreconditionsSuperClass {
	protected int member = -1;

	public int sqrt(int x) throws Exception {
		if (x < 0)
			return 0;
		return (int) Math.sqrt(x + 0.0);
	}

	public void doWork(int x) throws Exception {
		member++;
	}

	public static void main(String[] args) throws Exception {
		(new PreconditionsSuperClass()).sqrt(0);
		//(new PreconditionsSuperClass()).doWork(0);
	}
}
