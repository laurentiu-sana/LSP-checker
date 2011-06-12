package lsp.test.accounts;

public class OrdinaryAccount implements IAccount {
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
		isOpen = false;
		sum = 0;
	}

	@Override
	public boolean isOpen() {
		return isOpen;
	}

	public static void mainLSPChecker(String[] args) {
		(new OrdinaryAccount()).open();
		(new OrdinaryAccount()).setDeposit(50);
		(new OrdinaryAccount()).close();

		assert !(new OrdinaryAccount()).isOpen();
	}
}
