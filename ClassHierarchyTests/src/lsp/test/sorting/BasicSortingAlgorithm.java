package lsp.test.sorting;

import java.util.Arrays;

public class BasicSortingAlgorithm implements ISort {
	@Override
	public void sort(int[] data) {
		Arrays.sort(data);
	}
}
