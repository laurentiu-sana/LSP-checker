package lsp.test.numeric;

public class NumericTest {
	public MyInteger field1;
	public MyDummyInterface3 field2;

	public static void doWork(MyInteger[][][] tensor) {
		for (int i = 0; i < tensor.length; i++) {
			for (int j = 0; j < tensor[i].length; j++) {
				for (int k = 0; k < tensor[i].length; k++) {
					if (i == j && j == k)
						tensor[i][j][k].setValue(i + j + k);
					else if (i * k == j + k)
						tensor[i][j][k].setValue(i * j * k);
					else
						tensor[i][j][k].setValue(i * j + k);
				}

			}
		}
	}

	public static void doWork(MyInteger tensor) {
	}

	private static <T extends MyInteger> MyInteger[][][] allocateArray(
			Class<T> type, int x, int y, int z) {
		MyInteger[][][] data = new MyInteger[x][y][z];

		for (int i = 0; i < x; i++)
			for (int j = 0; j < y; j++)
				for (int k = 0; k < z; k++)
					try {
						data[i][j][k] = type.newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
		return data;
	}

	public static void mainLSPChecker(String[] args) {
		doWork(allocateArray(MyInteger.class, 3, 3, 3));
		doWork(allocateArray(MyDouble.class, 3, 3, 3));
	}
}
