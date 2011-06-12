package lsp.jpf.results.analyzer;

import java.util.Set;

public interface IConstraintsComparator {
	/** Compares two results and reports the warning in the given set */
	public void compare(MethodResults superResults, MethodResults subResults,
			Set<String> warns);
}
