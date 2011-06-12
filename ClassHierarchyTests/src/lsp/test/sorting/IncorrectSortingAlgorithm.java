package lsp.test.sorting;

public class IncorrectSortingAlgorithm extends BasicSortingAlgorithm {
	@Override
	public void sort(int[] data) {
		super.sort(data);
		if (data.length >= 1) {
			int aux;
			aux = data[0];
			data[0] = data[1];
			data[1] = aux;
		}
	}
}
