package CS151;

public class Transaction {
	private int accountFrom;
	public int getAccountFrom() {
		return accountFrom;
	}

	public int getAccountTo() {
		return accountTo;
	}

	public double getAmount() {
		return amount;
	}

	private int accountTo;
	private double amount;
	
	public Transaction(int accountFrom, int accountTo, double amount) {
		this.accountFrom = accountFrom;
		this.accountTo = accountTo;
		this.amount = amount;
	}
}
