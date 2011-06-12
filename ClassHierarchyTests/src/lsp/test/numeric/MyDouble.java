package lsp.test.numeric;

public class MyDouble extends MyInteger {
	private double value;

	public MyDouble() {
		super();
		value = 0.0;
	}

	public void setValue(double value) {
		System.out.println("Setting value in " + this.getClass());
		this.value = value;
	}

	public int getValue() {
		return (int) value;
	}

}
