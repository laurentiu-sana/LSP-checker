package lsp.jpf.test;

import gov.nasa.jpf.symbc.Debug;
import gov.nasa.jpf.symbc.Symbolic;

public class ComplexSymbcTest {
	static class MyClassWithSymbolicFields {
		@Symbolic("true")
		public int field1;

		@Symbolic("true")
		public int field2;

		public int myMethod1() {
			int z = field1 + field2;
			if (z > 0) {
				z = 1;
			} else {
				z = z - field1;
			}
			z = field1 * z;
			return z;
		}
	}

	static class MyClassWithFields {
		public int field1;

		public int field2;

		public int myMethod1() {
			int z = field1 + field2;
			if (z > 0) {
				z = 1;
			} else {
				z = z - field1;
			}
			z = field1 * z;
			return z;
		}
	}

	static void var1() {
		MyClassWithSymbolicFields mc = new MyClassWithSymbolicFields();
		mc.myMethod1();
		Debug.printPC("\nMyClassWithFields.myMethod1 Path Condition: ");
	}
	
	static void var2(int x, int y) {
		MyClassWithFields mc = new MyClassWithFields();

		mc.field1 = x;
		mc.field2 = y;
		mc.myMethod1();
		Debug.printPC("\nMyClassWithFields.myMethod1 Path Condition: ");
	}

	public static void main(String[] args) {
		// JPF parameter: +symbolic.method=myMethod1()
		var1();

		// JPF parameter: +symbolic.method=var2(sym#sym)
		//var2(1, 1);
	}
}
