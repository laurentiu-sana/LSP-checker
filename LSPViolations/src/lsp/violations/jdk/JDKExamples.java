package lsp.violations.jdk;

public class JDKExamples {
	public static void main(String args[]) {
		/** HashSet and LinkedList should have different interfaces */
		// HashSetLinkedListExample.main();

		/**
		 * AbstractList.remove() should be declared abstract and should not have
		 * a dummy implementation: throw new RuntimeException().
		 * There are many other similar cases in the JDK.
		 */
		//AbstractListArrayListExample.main();
		
		HashMapLinkedHashMapExample.main();
	}
}
