package lsp.jpf.scs;

import junit.framework.Assert;
import junit.framework.TestCase;
import lsp.test.constraints.SubClass;
import lsp.test.constraints.SuperClass;

/**
 * What should the LSP checker generate for SubClass and SuperClass
 */


public class DummyTestSCS extends TestCase {
	private static SubClass subObj = new SubClass();
	private static SuperClass superObj = new SuperClass();

	//@Test(value = { "true" })
	public void testPreconditions() throws Exception {
		subObj.preconditions(-1);
		superObj.preconditions(-1);

		subObj.preconditions(0);
		superObj.preconditions(0);
	}

	//@Test(value = { "true" })
	public void testPostConditions() {
		Assert.assertTrue(subObj.postconditions(-1) == superObj
				.postconditions(-1));
		Assert.assertTrue(subObj.postconditions(0) == superObj
				.postconditions(0));
	}

	//@Test(value = { "true" })
	public void testInvariants() {
		subObj.invariants(0);
		superObj.invariants(0);
		Assert.assertTrue(subObj.toString().equals(superObj.toString()));

		subObj.invariants(-1);
		superObj.invariants(-1);
		Assert.assertTrue(subObj.toString().equals(superObj.toString()));

		subObj.invariants(11);
		superObj.invariants(11);
		Assert.assertTrue(subObj.toString().equals(superObj.toString()));
	}
}


