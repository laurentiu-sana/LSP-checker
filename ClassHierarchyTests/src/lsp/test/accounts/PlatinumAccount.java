package lsp.test.accounts;

public class PlatinumAccount extends OrdinaryAccount {
	private boolean isOpen;
	private int sum;

	@Override
	public void open() {
		isOpen = true;
		sum = 0;
	}

	@Override
	public void setDeposit(int sum) {
		this.sum = sum;
	}

	@Override
	public void close() {
		assert sum >= 100;
		isOpen = false;
		sum = 0;
	}

	@Override
	public boolean isOpen() {
		return isOpen;
	}

	public static void mainLSPChecker(String[] args) {
		(new PlatinumAccount()).open();
		(new PlatinumAccount()).setDeposit(50);
		(new PlatinumAccount()).close();

		assert !(new PlatinumAccount()).isOpen();
	}
}
