package lsp.jpf.test;

import gov.nasa.jpf.symbc.Debug;

import java.net.URL;
import java.net.URLClassLoader;

public class BasicSymbcTest {
	static class BaseClass {
		public int foo(int x, int y) {
			int z = x + y;
			if (z > 0) {
				z = 1;
				z = z - x;
			} else {
			}
			z = 2 * z;
			return z;
		}

		private int bar(int x, int y) {
			int z = x + y;
			if (z > 0) {
				z = 1;
			}
			if (x < 5) {
				z = -z;
			}
			return z;
		}
	}

	public static void doTest(int x, int y) {
		BaseClass mc = new BaseClass();
		int res;

		if (-100 <= x && x <= 100 && 1 <= y && y <= 3) {
			res = mc.foo(x, y);
			// res = mc.bar(x, y);
			Debug.printPC("BaseClass.myMethod Path Condition: ");
			System.out.println("Result: " + res);
		}
	}

	public static void main(String[] args) {
		//Get the System Classloader
		//String classPath = System.getProperty("java.class.path", ".");
		//System.out.println(classPath);
		doTest(1, 2);
	}
}
