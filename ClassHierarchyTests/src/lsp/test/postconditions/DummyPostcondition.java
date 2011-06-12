package lsp.test.postconditions;

public class DummyPostcondition {
	/*
	public int postcond1(int x) {
		return 2 * x;
	}

	public int postcond2(int x, int y) {
		if (x < y)
			return x + y;
		return x - y;
	}
	
	public int postcond3(int x, int y) {
		if (x < 0)
			return postcond2(x, y);
		return postcond1(y);
	}
	
	private static int memberStatic = 0;	
	public int postcond4(int x, int y) {
		if (x < y)
			memberStatic = x + y;
		else
			memberStatic = x - y;
		return memberStatic * 5;
	}

	private int member = 0;
	public int postcond5(int x, int y) {
		if (x < y)
			member = x + y;
		else
			member = x - y;
		return member * 5;
	}
	*/
	
	/*
	private static int memberStatic2 = 0;
	public int postcond6(int x) {
		if (x < 0)
			memberStatic2 = -x;
		else
			memberStatic2 = x;
		return 2 * x;
	}
	
	private static int getStaticMember() {
		return memberStatic2;
	}
	
	public int postcond7(int x) {
		// Should see some side effects
		return getStaticMember() + postcond6(x) + getStaticMember();
	}

	private int member2 = 0;
	private void doSomething(int x) {
		member2 = x * (-1);
	}
 
	public int postcond8(int x) {
		member2 = x + x;
		doSomething(x);
		if (x > 0)
			return member2;
		return member2 + member2;
	}*/
	
	
	public int postcond9(InterfacePostconditions ip, int x) {
		if (x < 0)
			return 3 * ip.doWork(x);
		else
			return x + ip.doWork(x);
	}
	
	public int postcond10(SubClassPostconditions ip, int x) {
		if (x < 0)
			return 3 * ip.doWork(x);
		else
			return x + ip.doWork(x);
	}	
}
