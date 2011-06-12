package lsp.test.accounts;

public class AccountsTest {
	public void test(IAccount account, int x) {
		account.open();
		account.setDeposit(x);
		account.close();

		assert !account.isOpen();
	}

	public static void mainLSPChecker(String[] args) {
		//ClassLoader loader = AccountsTest.class.getClassLoader();
		//loader.setDefaultAssertionStatus(true);
		(new AccountsTest()).test(new OrdinaryAccount(), 0);
		(new AccountsTest()).test(new PlatinumAccount(), 0);
	}
}
