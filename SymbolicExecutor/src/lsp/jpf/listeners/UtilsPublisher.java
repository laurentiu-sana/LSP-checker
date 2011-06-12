package lsp.jpf.listeners;

import java.util.StringTokenizer;

public class UtilsPublisher {
	public static String refactorToken(String token) {
		if (token.contains("STRING"))
			token = token.replaceAll("\\[\\-?[0-9]+\\]", "");
		else if (token.contains("$assertionsDisabled"))
			return "assertion_disabled";
		else if (token.contains("CONST_")) {
			// CONST_0 => CONST
			return token.replaceAll("CONST_", "");
		} else if (token.contains("_")) {
			// x_13_SYMINT => x
			// di_1_SYMREF => di
			int index = token.lastIndexOf('_');
			String aux = token.substring(0, index);
			if (token.endsWith(")"))
				token = aux + ")";
			else
				token = aux;

			if (token.contains("_")) {
				index = token.lastIndexOf('_');
				aux = token.substring(0, index);

				if (token.endsWith(")"))
					token = aux + ")";
				else
					token = aux;
			}
		} else if (token.contains("[") && token.contains("]")) {
			// DummySuperClass[271].member[-1000] => DummySuperClass.member
			token = token.replaceAll("\\[\\-?[0-9]+\\]", "");
		}
		return token;
	}

	public static String refactorString(String string) {
		/** {# = 1} x_13_SYMINT[1] > CONST_0 => x > 0 */
		StringTokenizer strtok = new StringTokenizer(string, " ");
		StringBuffer strbuf = new StringBuffer();

		if (string.contains("{# = ")) {
			for (int i = 0; i < 3; i++)
				if (strtok.hasMoreTokens())
					strtok.nextToken();
		}

		while (strtok.hasMoreTokens()) {
			if (strbuf.length() > 0)
				strbuf.append(" ");
			String str = refactorToken(strtok.nextToken());
			if (str.equals("")) {
				if (strtok.hasMoreTokens())
					strtok.nextToken();
				if (strtok.hasMoreTokens())
					strtok.nextToken();
				if (strtok.hasMoreTokens())
					strtok.nextToken();
			} else
				strbuf.append(str);
		}
		
		return removeAssertion(strbuf.toString());
	}

	public static String removeAssertion(String string) {
		if (string == null)
			return null;
		if (string.endsWith(" && "))
			return string.substring(0, string.length() - 4);
		return string;
	}

	public static String refactorSymValues(String symValues) {
		StringTokenizer strtok = new StringTokenizer(symValues, ",");
		StringBuffer strbuf = new StringBuffer();

		while (strtok.hasMoreTokens()) {
			if (strbuf.length() > 0)
				strbuf.append(", ");
			strbuf.append(refactorToken(strtok.nextToken()));
		}

		return removeAssertion(strbuf.toString());
	}

	public static String combineArgSym(MethodSummary methodSummary) {
		StringBuffer strbuf = new StringBuffer();
		StringTokenizer strargt = new StringTokenizer(methodSummary
				.getArgTypes(), ",");
		StringTokenizer strargv = new StringTokenizer(methodSummary
				.getSymValues(), ",");

		while (true) {
			if (strargt.hasMoreTokens() && strargv.hasMoreTokens()) {
				strbuf.append(strargt.nextToken() + " " + strargv.nextToken());
				if (strargt.hasMoreTokens() && strargv.hasMoreTokens())
					strbuf.append(", ");
			} else
				break;
		}

		return strbuf.toString();
	}

}
