package lsp.scoring.test;

import junit.framework.TestCase;
import lsp.scoring.ScoringAlgorithmMain;

import org.junit.Test;

/**
 * Test units for the LSP checker;
 *  - ./log/ *.xml and scoring_algorithm.log
 *  - ./*.lsp.sort contains methods ordered by their score
 */
public class TestUnits extends TestCase {
	public final static String DUMMY_REGEX = "lsp\\.test\\.dummy\\..*";
	public final static String DUMMY_PACKAGE = "lsp.test.dummy";

	public final static String CARS_REGEX = "lsp\\.test\\.cars\\..*";
	public final static String CARS_PACKAGE = "lsp.test.cars";

	public final static String SORTING_REGEX = "lsp\\.test\\.sorting\\..*";
	public final static String SORTING_PACKAGE = "lsp.test.sorting";

	public final static String NUMERIC_REGEX = "lsp\\.test\\.numeric\\..*";
	public final static String NUMERIC_PACKAGE = "lsp.test.numeric";

	@Test
	public void testDummy() {
		ScoringAlgorithmMain.main(DUMMY_REGEX, DUMMY_PACKAGE);
	}

	@Test
	public void testCars() {
		ScoringAlgorithmMain.main(CARS_REGEX, CARS_PACKAGE);
	}

	@Test
	public void testSorting() {
		ScoringAlgorithmMain.main(SORTING_REGEX, SORTING_PACKAGE);
	}

	@Test
	public void testNumeric() {
		ScoringAlgorithmMain.main(NUMERIC_REGEX, NUMERIC_PACKAGE);
	}
}
