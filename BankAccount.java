public final class BankAccount {
    final ConcurrentBank bank;
    long balance;
    private final long number;

    BankAccount(ConcurrentBank bank, long number, long initialBalance) {
        this.bank = bank;
        this.number = number;
        this.balance = initialBalance;
    }

    public long getNumber() {
        return number;
    }

    public void deposit(long amount) {
        bank.changeBalance(this, amount, true);
    }

    public void withdraw(long amount) {
        bank.changeBalance(this, amount, false);
    }

    public long getBalance() {
        return bank.readBalance(this);
    }
}