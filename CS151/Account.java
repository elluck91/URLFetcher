package CS151;

public class Account {
	private int balance;
	private int accountId;
	private int numberOfTransactions;
	
	public double getBalance() {
		return balance;
	}

	public synchronized void deposit(double amount) {
		this.balance += amount;
		increaseTransactions();
	}
	
	public synchronized void withdraw(double amount) {
		this.balance -= amount;
		increaseTransactions();
	}
	
	public synchronized void increaseTransactions() {
		numberOfTransactions++;
	}
	
	public int getNumOfTrans() {
		return numberOfTransactions;
	}	

	public int getAccountId() {
		return accountId;
	}

	public Account(int id) {
		balance = 1000;
		accountId = id;
		numberOfTransactions = 0;
	}
	
	public String toString() {
		return "account: " + accountId + " bal: " + balance + " trans: " + numberOfTransactions;
	}
	
}
