package lsp.jpf.results.analyzer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lsp.jpf.parse.Constants;

public class PostconditionConstraintsComparator implements
		IConstraintsComparator {
	
	private String replaceConditionWithNewVars(String cond, Map<String, String> vars, String suffix) {
		Iterator<String> it = vars.keySet().iterator();
		cond = " " + cond + " ";
		
		while(it.hasNext()) {
			String varName = it.next();
			while (cond.indexOf(" " + varName + " ") >= 0)
				cond = cond.replace(" " + varName + " ", " " + varName + suffix + " ");
		}
		
		return cond;
	}

	@Override
	public void compare(MethodResults superResults, MethodResults subResults,
			Set<String> warns) {		
		Map<String, String> superPostconds = superResults.getPostconditions();
		Map<String, String> subPostconds = subResults.getPostconditions();
		
		if (superPostconds.equals(subPostconds))
			return;
		
		if (superPostconds.size() == 0 || subPostconds.size() == 0) {
			/** Probably we are comparing void methods */
			return;
		}

		StringBuffer strbuf = new StringBuffer();
		strbuf.append("(not");
		strbuf.append(" (or ");
		
		Map<String, String> symVars = superResults.getSymVars();
		Iterator<String> itSuper = superPostconds.keySet().iterator();

		while (itSuper.hasNext()) {
			String condSuper = itSuper.next();
			Iterator<String> itSub = subPostconds.keySet().iterator();
			
			if (condSuper == null || superPostconds.get(condSuper).length() == 0)
				continue;
			
			while (itSub.hasNext()) {
				String condSub = itSub.next();

				String resSuper = superPostconds.get(condSuper);
				String resSub = subPostconds.get(condSub);
				
				if (condSub == null || subPostconds.get(condSub).length() == 0)
					continue;
				
				if (condSuper.length() == 0)
					condSuper = "true";
				
				if (condSub.length() == 0)
					condSub = "true";

				/*
				(exists (xsuper_1 Int)
				  ..
				  (exists (xsuper_2 Int)
				    (and c1(xsuper_1, ...) c2 (= res1(xsuper_1) res2))))
				*/
				Iterator<String> it = symVars.keySet().iterator();
				int counter = 0;
				while (it.hasNext()) {
					String varName = it.next();
					String varType = symVars.get(varName);
					strbuf.append(" (exists ( " + varName + "super " + varType + " ) ");
					counter++;
				}
				
				strbuf.append(" (and ");
				
				// c1
				strbuf.append(
					replaceConditionWithNewVars(condSuper.replaceAll(" instanceof ", "_instanceof_").replaceAll("\\.", "_"),
					symVars, "super"));
				strbuf.append(" ");
				
				// c2
				strbuf.append(replaceConditionWithNewVars(
					condSub.replaceAll(" instanceof ", "_instanceof_").replaceAll("\\.", "_"),
					symVars, "sub"));
				strbuf.append(" ");
				
				// (= r1 r2)
				if (resSuper == null || resSuper.length() == 0)
					resSuper = Constants.generateDefaultValueForZ3Type(superResults.getReturnZ3Type());
				resSuper = replaceConditionWithNewVars(resSuper, symVars, "super");
				
				if (resSub == null || resSub.length() == 0)
					resSub = Constants.generateDefaultValueForZ3Type(subResults.getReturnZ3Type());
				resSub = replaceConditionWithNewVars(resSub, symVars, "sub");

				strbuf.append(" (= " + resSuper + " " + resSub + " )");
				
				strbuf.append(" )");
				
				for (int i = 0 ; i < counter ; i++)
					strbuf.append(" )");
			}
		}
		
		strbuf.append(") ");
		strbuf.append(")");
		
		/** Early optimization */
		if (strbuf.toString().equals("(not (or ) )"))
			return;
		
		String z3Script = Constants.generateZ3VerificationCode(superResults,
				strbuf.toString().replaceAll("==", "="), "postcond",
				superResults.getSymVars());

		Constants.reportWarning("{different postconditions}\n", superResults,
				subResults, superPostconds, subPostconds, warns, Z3Wrapper.executeScript(z3Script));
	}
}
