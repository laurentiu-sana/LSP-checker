package lsp.test.features;

public class AdvancedConstructor {

	static {
		System.err.println("static initializer");
		new AdvancedConstructor();
	}

	public AdvancedConstructor() {

	}

	public AdvancedConstructor(int x) {

	}

	public AdvancedConstructor(MyInterface mi) {

	}

	public AdvancedConstructor(int x, MyInterface mi, SuperClass msuper,
			SubClass msub) {

	}

	public static void foo() {

	}

	public static void foo(int x) {

	}

	public static void foo(MyInterface mi) {

	}

	public static void foo(int x, MyInterface mi, SuperClass msuper,
			SubClass msub) {

	}
}
