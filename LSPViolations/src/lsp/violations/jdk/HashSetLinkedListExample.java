package lsp.violations.jdk;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

public class HashSetLinkedListExample {
	private Collection<Object> m_data;

	public HashSetLinkedListExample(Collection<Object> data) {
		m_data = data;
	}

	public void insertData(int n) {
		for (int i = 0; i < n; i++)
			m_data.add(new Integer(1));
	}

	public int getSize() {
		return m_data.size();
	}

	public static void main() {
		int n = 10;

		HashSetLinkedListExample ex1 = new HashSetLinkedListExample(
				new HashSet<Object>());
		ex1.insertData(n);

		HashSetLinkedListExample ex2 = new HashSetLinkedListExample(
				new LinkedList<Object>());
		ex2.insertData(n);

		if (ex1.getSize() != ex2.getSize()) {
			System.out.println("LSP violated, classes involved: "
					+ Collection.class.getClass() + ", "
					+ HashSet.class.getClass() + ", "
					+ LinkedList.class.getClass());
			System.out.println(ex1.getSize() + " " + ex2.getSize());
		}
	}
}
