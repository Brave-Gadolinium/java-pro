import java.util.HashMap;
import java.util.Map;

public final class ConcurrentBank {
    private final Object lock = new Object();
    private final Map<Long, BankAccount> accounts = new HashMap<>();

    private long lastNumber;
    private long totalBalance;

    public BankAccount createAccount(long initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Начальный баланс не может быть отрицательным");
        }

        synchronized (lock) {
            long number = Math.incrementExact(lastNumber);
            long newTotal = Math.addExact(totalBalance, initialBalance);

            BankAccount account = new BankAccount(this, number, initialBalance);
            accounts.put(number, account);
            lastNumber = number;
            totalBalance = newTotal;
            return account;
        }
    }

    public void transfer(BankAccount from, BankAccount to, long amount) {
        requirePositive(amount);

        synchronized (lock) {
            requireRegistered(from);
            requireRegistered(to);

            if (from == to) {
                throw new IllegalArgumentException("Нельзя перевести деньги на тот же счёт");
            }
            requireFunds(from, amount);

            long newFromBalance = from.balance - amount;
            long newToBalance = Math.addExact(to.balance, amount);
            from.balance = newFromBalance;
            to.balance = newToBalance;
        }
    }

    public long getTotalBalance() {
        synchronized (lock) {
            return totalBalance;
        }
    }

    long readBalance(BankAccount account) {
        synchronized (lock) {
            return account.balance;
        }
    }

    void changeBalance(BankAccount account, long amount, boolean deposit) {
        requirePositive(amount);

        synchronized (lock) {
            if (deposit) {
                long newBalance = Math.addExact(account.balance, amount);
                long newTotal = Math.addExact(totalBalance, amount);
                account.balance = newBalance;
                totalBalance = newTotal;
            } else {
                requireFunds(account, amount);
                account.balance -= amount;
                totalBalance -= amount;
            }
        }
    }

    private void requireRegistered(BankAccount account) {
        if (account == null || account.bank != this
                || accounts.get(account.getNumber()) != account) {
            throw new IllegalArgumentException("Счёт не зарегистрирован в этом банке");
        }
    }

    private static void requirePositive(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
    }

    private static void requireFunds(BankAccount account, long amount) {
        if (account.balance < amount) {
            throw new IllegalStateException("Недостаточно средств");
        }
    }
}