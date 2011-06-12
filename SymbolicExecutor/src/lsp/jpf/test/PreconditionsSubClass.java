package lsp.jpf.test;

public class PreconditionsSubClass extends PreconditionsSuperClass {
	@Override
	public int sqrt(int x) throws Exception {
		if (x + x < 50) {
			if (x < 10)
				throw new Exception();
			if (x > 5)
				throw new Exception("something interesting");
		}
		return (int) Math.sqrt(x + 0.0);
	}

	@Override
	public void doWork(int x) throws Exception {
		if (x >= 1)
			throw new Exception();
	}

	public static void main(String[] args) {
		try {
			(new PreconditionsSubClass()).sqrt(0);
			//(new PreconditionsSubClass()).doWork(0);
		} catch (Exception e) {

		}
	}
}
