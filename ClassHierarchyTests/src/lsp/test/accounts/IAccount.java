package lsp.test.accounts;

public interface IAccount {
	public void open();

	public void setDeposit(int sum);

	public void close();
	
	public boolean isOpen();
}
