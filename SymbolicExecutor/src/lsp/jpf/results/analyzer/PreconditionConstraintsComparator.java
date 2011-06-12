package lsp.jpf.results.analyzer;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lsp.jpf.parse.Constants;

public class PreconditionConstraintsComparator implements
		IConstraintsComparator {

	@Override
	public void compare(MethodResults superResults, MethodResults subResults,
			Set<String> warns) {
		
		Map<String, String> superPreconds = superResults.getPreconditions();
		Map<String, String> subPreconds = subResults.getPreconditions();

		if (superPreconds.equals(subPreconds))
			return;
		
		if (superPreconds.size() == 0)
			superPreconds.put("false", "Assert failed");
		
		if (subPreconds.size() == 0)
			subPreconds.put("false", "Assert failed");
				
		StringBuffer strbuf = new StringBuffer();
		strbuf.append("(or ");
		
		
		Iterator<String> itSuper = superPreconds.keySet().iterator();
		
		while(itSuper.hasNext()) {
			Iterator<String> itSub = subPreconds.keySet().iterator();
			String condSuper = itSuper.next();
			
			while(itSub.hasNext()) {
				String condSub = itSub.next();
				
				if (condSuper == null || condSuper.length() == 0)
					condSuper = "true";
				
				if (condSub == null || condSub.length() == 0)
					condSub = "true";
				
				strbuf.append(" (not (implies");
				strbuf.append(" (not " + condSuper + ")");
				strbuf.append(" (not " + condSub + ")");
				strbuf.append(") ) ");
			}
		}
		
		strbuf.append(")");
		
		String z3Script = Constants.generateZ3VerificationCode(superResults,
				strbuf.toString().replaceAll("==", "="), "precond",
				superResults.getSymVars());

		Constants.reportWarning("{different preconditions}\n", superResults,
				subResults, superPreconds, subPreconds, warns, Z3Wrapper.executeScript(z3Script));
	}
}
