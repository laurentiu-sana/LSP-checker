package lsp.scoring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.print.attribute.standard.Chromaticity;

import org.apache.log4j.Logger;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.tagkit.SignatureTag;
import soot.tagkit.Tag;
import soot.util.Chain;

/**
 * Wrapping class used to sort methods by their score
 * and to serialize, toString(), the information into a file
 */
public class MethodScore implements Comparable<MethodScore> {
	private final static Logger LOGGER = Logger.getLogger(MethodScore.class);

	public final static Double EPS = 0.001;
	private ClassHierarchy ch;
	private SootMethod method;
	private Double score;
	private HashMap<SootClass, List<SootClass>> subtypes;

	public MethodScore(SootMethod method, Double score) {
		this.score = score;
		this.method = method;
		subtypes = new HashMap<SootClass, List<SootClass>>();
	}

	public int compareTo(MethodScore obj) {
		Double diff = obj.score - this.score;
		if (diff < EPS)
			return -1;
		else if (diff > EPS)
			return 1;
		return 0;
	}

	@Override
	public String toString() {
		return method.toString() + " scored " + score;
	}

	private List<SootClass> getPossibleInstances(SootClass sc) {
		List<SootClass> list = null;

		if (subtypes.containsKey(sc)) {
			list = subtypes.get(sc);
			log("possible instances(" + sc + ") = " + list);
			return list;
		}

		list = new LinkedList<SootClass>();

		if (sc.isConcrete() && sc.isPublic())
			list.add(sc);

		/** Computes the subtypes */
		if (ch != null && ch.getInfo() != null) {
			ClassInfo ci = ch.getInfo().get(sc);
			if (ci != null) {
				HashSet<SootClass> d_subtypes = ci.getSubClasses();
				if (d_subtypes != null) {
					for (SootClass subtype : d_subtypes) {
						if (subtype.isPublic() && subtype.isConcrete())
							list.addAll(getPossibleInstances(subtype));
					}
				}
			}
		}
		subtypes.put(sc, list);
		log("possible instances(" + sc + ") = " + list);
		return list;
	}

	@SuppressWarnings("unchecked")
	private Set<String> generateInstances(SootClass sc, boolean recursive,
			boolean isClass, boolean isStatic) {
		// FIXME: object generation is not perfect, may integrate an open-source solution?
		Set<String> list = new HashSet<String>();

		if (!recursive) {
			// It seems that <Soot> sees jdk.util.ResourceBundle$Control as a public and concrete type
			if (sc.isConcrete() && sc.isPublic() && !sc.getName().contains("$")) {
				//ch.addImportClass(sc.getName());
				if (isStatic) {
					list.add(sc.getName());
				} else {
					for (SootMethod method : sc.getMethods()) {
						if (!method.isAbstract()
								&& method.isPublic()
								&& method.getName().equals(
										SootMethod.constructorName)) {
							if (isClass)
								list.add(sc + ".class.getClass()");
							else {
								if (hasTwoMethodsWithValidNull(method))
									continue;
								StringBuffer str = new StringBuffer("new ");
								str.append(sc.getName());
								str.append("(");
								List<Type> params = method.getParameterTypes();
								int i = 0, n = params.size();

								for (Type param : params) {
									if (param instanceof RefType
											|| param instanceof ArrayType)
										str.append("null");
									else if (param instanceof IntType)
										str.append("0");
									else if (param instanceof BooleanType)
										str.append("false");
									else if (param instanceof CharType)
										str.append("(char) 0");
									else if (param instanceof ByteType)
										str.append("(byte) 0");
									else if (param instanceof ShortType)
										str.append("(short) 0");
									else if (param instanceof FloatType)
										str.append("0.0f");
									else if (param instanceof DoubleType)
										str.append("0.0");
									else if (param instanceof LongType)
										str.append("0l");
									else
										str.append("0");

									if (++i < n)
										str.append(",");
								}
								str.append(")");
								list.add(str.toString());
							}
						}
					}
				}
			}
		} else {
			List<SootClass> possibleInstances = null;
			if (sc.getName().equals(Constants.CLASS_STR))
				possibleInstances = getPossibleInstances(Scene.v()
						.getSootClass(Constants.OBJECT_STR));
			else
				possibleInstances = getPossibleInstances(sc);
			for (SootClass subtype : possibleInstances) {
				if (!subtype.isAbstract() && subtype.isPublic())
					list.addAll(generateInstances(subtype, false, isClass,
							isStatic));
			}
		}
		return list;
	}

	private boolean addPossibleTypes(Type it, List<StringBuffer> buffers,
			int order, boolean isClass, boolean isStatic, boolean addNull) {
		Set<String> generatedObjects = null;

		if (order > 0 && it instanceof RefType) {
			// Special case: order > 0 and reference type
			generatedObjects = new HashSet<String>();
			generatedObjects.add(it.getArrayType().baseType.toString());
		} else {
			if (it instanceof RefType) {
				generatedObjects = generateInstances(((RefType) it)
						.getSootClass(), true, isClass, isStatic);
				if (addNull) {
					// null is a valid reference
					generatedObjects.add("null");
				}
			} else if (it instanceof IntType) {
				generatedObjects = new HashSet<String>();
				generatedObjects.add("int");
			} else if (it instanceof CharType) {
				generatedObjects = new HashSet<String>();
				generatedObjects.add("char");
			} else if (it instanceof LongType) {
				generatedObjects = new HashSet<String>();
				generatedObjects.add("long");
			} else if (it instanceof ShortType) {
				generatedObjects = new HashSet<String>();
				generatedObjects.add("short");
			} else if (it instanceof ByteType) {
				generatedObjects = new HashSet<String>();
				generatedObjects.add("byte");
			} else if (it instanceof DoubleType) {
				generatedObjects = new HashSet<String>();
				generatedObjects.add("double");
			} else if (it instanceof FloatType) {
				generatedObjects = new HashSet<String>();
				generatedObjects.add("float");
			} else if (it instanceof BooleanType) {
				generatedObjects = new HashSet<String>();
				generatedObjects.add("boolean");
			} else
				reportNotImplemented("[add possible types] unknown type: "
						+ it.getClass(), true);
		}

		if (order > 0) {
			// type => new Type[]..[] { }
			// order = 3, type = int => new int[][][] {}
			StringBuffer prefix = new StringBuffer("");
			for (int i = 0; i < order; i++)
				prefix.append("[]");
			Set<String> aux = new HashSet<String>();
			for (String type : generatedObjects)
				aux.add("new " + type + prefix.toString() + "{}");
			generatedObjects = aux;
		}

		int g_size = generatedObjects.size();
		if (g_size == 0)
			return false;

		Iterator<String> g_it = null;
		Set<StringBuffer> new_buffers = new HashSet<StringBuffer>();

		for (StringBuffer strbuf : buffers) {
			g_it = generatedObjects.iterator();
			if (g_size > 1) {
				for (int j = 1; j < g_size; j++) {
					StringBuffer strbuf_copy = new StringBuffer(strbuf
							.toString());
					strbuf_copy.append(g_it.next());
					new_buffers.add(strbuf_copy);
				}
				strbuf.append(g_it.next());
			} else if (g_size == 1)
				strbuf.append(g_it.next());
		}

		if (g_size > 1
				&& !(Constants.MAX_METHODS_TESTS > 0 && buffers.size() > Constants.MAX_METHODS_TESTS))
			buffers.addAll(new_buffers);
		return true;
	}

	private StringBuffer filterConstructors(StringBuffer strbuf) {
		/**
		 * new lsp.test.features.AdvancedConstructor().<init>(0,new lsp.test.features.SuperClass());
		 * =>
		 * (new lsp.test.features.AdvancedConstructor(0, new lsp.test.features.SuperClass());
		 */
		StringTokenizer strtok = new StringTokenizer(strbuf.toString(), ";");
		StringBuffer data = new StringBuffer();

		while (strtok.hasMoreTokens()) {
			String str = strtok.nextToken();

			if (str.length() <= 1)
				continue;

			// FIXME: This is an ULTRA HACK; for some method this algorithm doesn't
			// generate the right method call and we comment the code (it may be used by the client)
			if (str.contains("(,") || str.contains(",,") || str.contains(",)"))
				return null;

			if (str.contains(SootMethod.constructorName)) {
				StringTokenizer tokens = new StringTokenizer(str, "<>\n");
				String token1 = null;
				String token2 = null;

				if (tokens.hasMoreTokens())
					token1 = tokens.nextToken();
				if (tokens.hasMoreTokens())
					if (tokens.hasMoreTokens())
						tokens.nextToken();
				if (tokens.hasMoreTokens())
					token2 = tokens.nextToken();

				tokens = new StringTokenizer(token1, "()");
				if (tokens.hasMoreTokens())
					token1 = tokens.nextToken();

				if (token1 != null && token2 != null)
					data.append(token1 + token2 + ";\n");
			} else {
				data.append(str);
				data.append(";\n");
			}
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	private static List<String> getParametersTypes(SootMethod method) {
		List<String> params = new LinkedList<String>();
		List<Type> methodParams = method.getParameterTypes();
		for (Type type : methodParams) {
			if (type instanceof RefType || type instanceof ArrayType)
				params.add("reference");
			else
				params.add("primitive");
		}
		return params;
	}

	private static boolean hasTwoMethodsWithValidNull(SootMethod method) {
		/*
		 * Checks if the class of method contains two function with the same
		 * signature, in terms of primitive types and references
		 * 
		 * For example foo(Set, Map) and foo(Object, List) has the same signature
		 * foo(Ref,Ref) and we return true
		 */
		SootClass m_class = method.getDeclaringClass();
		if (m_class == null)
			return false;

		List<String> paramTypes = getParametersTypes(method);

		List<SootMethod> methods = m_class.getMethods();
		for (SootMethod it : methods) {
			if (it.getName().equals(method.getName()) && it != method) {
				List<String> itParamTypes = getParametersTypes(it);
				if (paramTypes.equals(itParamTypes))
					return true;
			}
		}
		return false;
	}

	private void commitTestCases(StringBuffer buffer, StringBuffer src,
			boolean isValid) {
		// The string is invalid generated and we comment it
		if (!isValid)
			buffer
					.append("/** Sorry, the LSP checker could not generate a valid test case\n");
		StringTokenizer strtok = new StringTokenizer(src.toString(), "\n");
		while (strtok.hasMoreTokens()) {
			String str = strtok.nextToken();
			if (str.length() > 1) {
				buffer.append("        try {");
				buffer.append(str);
				buffer.append(" } catch(Exception e) { }\n");
			}
		}
		if (!isValid)
			buffer.append("*/\n");
	}

	private StringBuffer generateMethodTestCase(SootMethod method,
			List<Type> basicTypes, List<RefType> symRefs, boolean isClass) {
		log("method " + method + ", basicTypes " + basicTypes + ", symRefs "
				+ symRefs + ", isClass " + isClass);
		boolean useComparableHack = false;
		boolean addNull = !hasTwoMethodsWithValidNull(method);
		List<StringBuffer> buffers = new LinkedList<StringBuffer>();
		Set<String> possibleObjects = generateInstances(method
				.getDeclaringClass(), false, false, method.isStatic());

		buffers.add(new StringBuffer());
		for (String obj : possibleObjects) {
			// object + . + method
			for (StringBuffer strbuf : buffers) {
				strbuf.append(" ");
				strbuf.append(obj);
				strbuf.append(".");
				strbuf.append(method.getName());
				strbuf.append("(");
			}

			int i = 0;
			int size = method.getParameterCount();

			/**
			 * Just another hack, if we see MyClass.compareTo(Object) and
			 * MyClass implements Comparable, we change it to MyClass.compareTo(MyClass)
			 */
			if (size == 1 && method.getName().equals("compareTo")) {
				SootClass methodClass = method.getDeclaringClass();
				Chain<SootClass> interfaces = methodClass.getInterfaces();
				for (SootClass ii : interfaces) {
					if (ii.getName().equals("java.lang.Comparable")) {
						useComparableHack = true;
						break;
					}
				}
			}

			for (Object it : method.getParameterTypes()) {
				if (it instanceof RefType) {
					if (useComparableHack) {
						if (!addPossibleTypes(RefType.v(method
								.getDeclaringClass().toString()), buffers, 0,
								isClass, false, addNull))
							return null;
					} else if (!addPossibleTypes((RefType) it, buffers, 0,
							isClass, false, addNull))
						return null;
				} else if (it instanceof ArrayType) {
					ArrayType arrayType = (ArrayType) it;
					if (!addPossibleTypes(arrayType.baseType, buffers,
							arrayType.numDimensions, isClass, false, addNull))
						return null;
				} else if (it instanceof BooleanType) {
					for (StringBuffer strbuf : buffers)
						strbuf.append("false");
				} else if (it instanceof CharType) {
					for (StringBuffer strbuf : buffers)
						strbuf.append("(char) 0");
				} else if (it instanceof ByteType) {
					for (StringBuffer strbuf : buffers)
						strbuf.append("(byte) 0");
				} else if (it instanceof ShortType) {
					for (StringBuffer strbuf : buffers)
						strbuf.append("(short) 0");
				} else if (it instanceof FloatType) {
					for (StringBuffer strbuf : buffers)
						strbuf.append("0.0f");
				} else if (it instanceof DoubleType) {
					for (StringBuffer strbuf : buffers)
						strbuf.append("0.0");
				} else {
					for (StringBuffer strbuf : buffers)
						strbuf.append("0");
				}
				if (i < size - 1) {
					for (StringBuffer strbuf : buffers)
						strbuf.append(",");
				}
				i++;
			}
			for (StringBuffer strbuf : buffers)
				strbuf.append(");");
		}

		int tests_counter = 0;

		StringBuffer buffer = new StringBuffer();
		for (StringBuffer strbuf : buffers) {
			if (Constants.MAX_METHODS_TESTS > 0) {
				if (tests_counter++ > Constants.MAX_METHODS_TESTS)
					break;
			}
			StringBuffer str = filterConstructors(strbuf);
			if (str != null) {
				if (str.length() > 0)
					commitTestCases(buffer, str, true);
			} else
				commitTestCases(buffer, strbuf, false);
		}
		return buffer;
	}

	@SuppressWarnings("unchecked")
	public StringBuffer generateTestCases(ClassHierarchy ch) {
		this.ch = ch;
		boolean generate = score >= Constants.THRESHOLD_SCORE;

		// Even if the score is not high enough, we generate a test case
		// for the current method
		if (!generate && method.getParameterCount() > 0)
			generate = true;

		if (method.getName().equals(Constants.MAIN_LSP_CHECKER)
				|| method.getName().equals(Constants.MAIN_LSP_CHECKER2))
			return null;

		generate = method.isPublic() & method.isConcrete();

		// If the main method is polymorphic, we forget about this
		if (method.getName().equals("main")
				&& method.getReturnType().toString().equals("void")
				&& method.isStatic()
				&& (method.getParameterCount() == 1)
				&& method.getParameterType(0).toString().equals(
						Constants.STRING_ARRAY_STR))
			generate = false;

		// We skip the generic methods analysis
		List<Tag> tags = method.getTags();
		for (Tag tag : tags) {
			if (tag instanceof SignatureTag) {
				generate = false;
				break;
			}
		}

		if (!generate)
			return null;

		HashMap<SootClass, ClassInfo> info = ch.getInfo();
		List<Type> params = method.getParameterTypes();
		HashMap<RefType, List<RefType>> typesMap = new HashMap<RefType, List<RefType>>();

		List<Type> basicTypes = new LinkedList<Type>();
		List<RefType> symRefs = new LinkedList<RefType>();

		boolean hasClassParam = false;

		for (Type it : params) {
			if (it instanceof RefType) {
				RefType type = (RefType) it;
				List<RefType> types = new LinkedList<RefType>();
				SootClass typeClass = type.getSootClass();

				/**
				 * We believe that the types of java.lang.Class can be generated by
				 * the available instances of java.lang.Object.
				 */
				if (typeClass.getName().equals(Constants.CLASS_STR)) {
					hasClassParam = true;
					typeClass = Scene.v().getSootClass(Constants.OBJECT_STR);
				}
				computePossibleTypes(info, typeClass, types);
				log("possible types(" + typeClass + ") = " + types);
				typesMap.put(type, types);
			} else
				basicTypes.add(it);
		}

		if (typesMap.size() == 1) {
			RefType symRef = typesMap.keySet().iterator().next();
			symRefs.add(symRef);
		}

		return generateMethodTestCase(method, basicTypes, symRefs,
				hasClassParam);
	}

	private void reportNotImplemented(String msg, boolean quit) {
		System.err.println("[not implemented] " + msg);
		if (quit)
			System.exit(0);
	}

	private void computePossibleTypes(HashMap<SootClass, ClassInfo> info,
			SootClass typeClass, List<RefType> types) {
		ClassInfo ci = info.get(typeClass);
		if (ci == null)
			return;
		if (typeClass.isConcrete() && typeClass.isPublic()) {
			if (Constants.MAX_METHODS_TESTS > 0
					&& types.size() < Constants.MAX_METHODS_TESTS)
				types.add(typeClass.getType());
		}
		if (ci.getSubClasses() != null) {
			for (SootClass subTypes : ci.getSubClasses())
				computePossibleTypes(info, subTypes, types);
		}
	}

	private void log(String msg) {
		//LOGGER.info(msg);
	}
}
