package lsp.test.cars;

public class BMWX5 extends BMW implements ILuxury {
	@Override
	public String sayYourName() {
		return super.sayYourName() + " [luxury edition]";
	}
}
