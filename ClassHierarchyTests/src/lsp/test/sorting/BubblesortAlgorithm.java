package lsp.test.sorting;

public class BubblesortAlgorithm extends BasicSortingAlgorithm {
	@Override
	public void sort(int[] data) {
		int n = data.length;
		int aux;
		boolean ordered = false;

		do {
			ordered = true;

			for (int i = 0; i < n - 1; i++) {
				if (data[i] > data[i + 1]) {
					aux = data[i];
					data[i] = data[i + 1];
					data[i + 1] = aux;
					ordered = false;
				}
			}
		} while (!ordered);
	}
}
