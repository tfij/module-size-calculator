import java.util.HashMap;
import java.util.Map;

public class PaymentGateway {
    private static final Map<String, Double> walletBalance = new HashMap<>();

    public static void addWalletBalance(String userId, double amount) {
        if (walletBalance.containsKey(userId)) {
            double currentBalance = walletBalance.get(userId);
            walletBalance.put(userId, currentBalance + amount);
        } else {
            walletBalance.put(userId, amount);
        }
    }

    public static double getWalletBalance(String userId) {
        return walletBalance.getOrDefault(userId, 0.0);
    }

    public static boolean processPayment(String userId, double amount) {
        if (walletBalance.containsKey(userId) && walletBalance.get(userId) >= amount) {
            double currentBalance = walletBalance.get(userId);
            walletBalance.put(userId, currentBalance - amount);
            return true;
        }
        return false;
    }

    public static void refundPayment(String userId, double amount) {
        if (walletBalance.containsKey(userId)) {
            double currentBalance = walletBalance.get(userId);
            walletBalance.put(userId, currentBalance + amount);
        }
    }

    public static void main(String[] args) {
        addWalletBalance("user1", 100.0);
        addWalletBalance("user2", 50.0);

        System.out.println("User1 Wallet Balance: $" + getWalletBalance("user1"));
        System.out.println("User2 Wallet Balance: $" + getWalletBalance("user2"));

        double paymentAmount = 30.0;
        if (processPayment("user1", paymentAmount)) {
            System.out.println("Payment of $" + paymentAmount + " processed successfully for user1");
        } else {
            System.out.println("Payment of $" + paymentAmount + " failed for user1 due to insufficient balance");
        }

        paymentAmount = 70.0;
        if (processPayment("user2", paymentAmount)) {
            System.out.println("Payment of $" + paymentAmount + " processed successfully for user2");
        } else {
            System.out.println("Payment of $" + paymentAmount + " failed for user2 due to insufficient balance");
        }

        refundPayment("user1", 20.0);
        System.out.println("User1 Wallet Balance after refund: $" + getWalletBalance("user1"));
    }
}
