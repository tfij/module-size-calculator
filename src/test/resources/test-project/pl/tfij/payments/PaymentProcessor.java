import java.util.ArrayList;
import java.util.List;

public class PaymentProcessor {
    private List<Transaction> transactions;

    public PaymentProcessor() {
        this.transactions = new ArrayList<>();
    }

    public void processPayment(User user, double amount) {
        Transaction transaction = new Transaction(user, amount);
        transaction.process();
        transactions.add(transaction);
    }

    public void refundPayment(User user, double amount) {
        Transaction transaction = new Transaction(user, -amount);
        transaction.process();
        transactions.add(transaction);
    }

    public double getTotalProcessedAmount() {
        double totalAmount = 0;
        for (Transaction transaction : transactions) {
            totalAmount += transaction.getAmount();
        }
        return totalAmount;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public static void main(String[] args) {
        PaymentProcessor processor = new PaymentProcessor();
        User user1 = new User("user1");
        User user2 = new User("user2");

        processor.processPayment(user1, 100.0);
        processor.processPayment(user2, 50.0);
        processor.refundPayment(user1, 20.0);

        System.out.println("Total processed amount: $" + processor.getTotalProcessedAmount());
        System.out.println("Transactions:");
        for (Transaction transaction : processor.getTransactions()) {
            System.out.println(transaction);
        }
    }
}

class Transaction {
    private User user;
    private double amount;

    public Transaction(User user, double amount) {
        this.user = user;
        this.amount = amount;
    }

    public void process() {
        // Actual payment processing logic here
        System.out.println("Processing transaction: User - " + user.getName() + ", Amount - $" + amount);
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "user=" + user +
                ", amount=" + amount +
                '}';
    }
}

class User {
    private String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
