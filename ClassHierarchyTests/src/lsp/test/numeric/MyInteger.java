package lsp.test.numeric;

public class MyInteger implements MyIValue, MyDummInterface1, MyDummyInterface2 {
	private int value;

	public MyInteger() {
		value = 0;
	}

	public void setValue(int value) {
		System.out.println("Setting value in " + this.getClass());
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
