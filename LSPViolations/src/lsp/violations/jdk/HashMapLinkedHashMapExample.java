package lsp.violations.jdk;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HashMapLinkedHashMapExample {
	public void foo(Map<Object, Object> map) {
		//init e protected ? ntru=> merge overridat
	}

	public static void main() {
		new HashMapLinkedHashMapExample().foo(new HashMap<Object, Object>());
		new HashMapLinkedHashMapExample()
				.foo(new LinkedHashMap<Object, Object>(
						new HashMap<Object, Object>()));
	}
}
