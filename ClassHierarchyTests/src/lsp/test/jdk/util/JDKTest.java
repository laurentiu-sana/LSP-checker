package lsp.test.jdk.util;

public class JDKTest {
	private static int doWork(Collection<Integer> data) {
		for (int i = 0; i < 10; i++)
			data.add(0);
		return data.size();
	}

	public static void mainLSPChecker(String[] args) {
		System.err.println(doWork(new LinkedList<Integer>()));
		System.err.println(doWork(new HashSet<Integer>()));
	}
}
