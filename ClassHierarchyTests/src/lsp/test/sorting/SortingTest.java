package lsp.test.sorting;

import lsp.test.Util;

public class SortingTest {
	public void doWork(ISort sortingAlgorithm) {
		int data[] = new int[] { 5, 3, 2, 1 };
		sortingAlgorithm.sort(data);

		for (int i = 0; i < data.length - 1; i++) {
			if (!(data[i] <= data[i + 1])) {
				Util.log(sortingAlgorithm.getClass(), "sort()");
				break;
			}
		}
	}

	public static void mainLSPChecker(String[] args) {
		(new SortingTest()).doWork(new BasicSortingAlgorithm());
		(new SortingTest()).doWork(new BubblesortAlgorithm());
		(new SortingTest()).doWork(new IncorrectSortingAlgorithm());
	}
}
