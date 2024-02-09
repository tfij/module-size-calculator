import java.util.ArrayList;
import java.util.List;

public class BillingService {
    private List<Invoice> invoices;

    public BillingService() {
        this.invoices = new ArrayList<>();
    }

    public void generateInvoice(Order order) {
        Invoice invoice = new Invoice(order);
        invoice.calculateTotal();
        invoices.add(invoice);
    }

    public double getTotalRevenue() {
        double totalRevenue = 0;
        for (Invoice invoice : invoices) {
            totalRevenue += invoice.getTotalAmount();
        }
        return totalRevenue;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public static void main(String[] args) {
        BillingService billingService = new BillingService();
        Order order1 = new Order("order1");
        Order order2 = new Order("order2");

        billingService.generateInvoice(order1);
        billingService.generateInvoice(order2);

        System.out.println("Total revenue: $" + billingService.getTotalRevenue());
        System.out.println("Invoices:");
        for (Invoice invoice : billingService.getInvoices()) {
            System.out.println(invoice);
        }
    }
}

class Invoice {
    private Order order;
    private double totalAmount;

    public Invoice(Order order) {
        this.order = order;
    }

    public void calculateTotal() {
        // Calculation logic based on order details
        totalAmount = 100.0; // Sample calculation
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "order=" + order +
                ", totalAmount=" + totalAmount +
                '}';
    }
}

class Order {
    private String orderId;

    public Order(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                '}';
    }
}
