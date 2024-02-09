import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHistory {
    private Map<User, List<Order>> orderHistory;

    public OrderHistory() {
        this.orderHistory = new HashMap<>();
    }

    public void addOrder(User user, Order order) {
        if (orderHistory.containsKey(user)) {
            orderHistory.get(user).add(order);
        } else {
            List<Order> orders = new ArrayList<>();
            orders.add(order);
            orderHistory.put(user, orders);
        }
    }

    public List<Order> getOrdersByUser(User user) {
        return orderHistory.getOrDefault(user, new ArrayList<>());
    }

    public int getTotalOrdersCount() {
        int totalCount = 0;
        for (List<Order> orders : orderHistory.values()) {
            totalCount += orders.size();
        }
        return totalCount;
    }

    public double getTotalRevenue() {
        double totalRevenue = 0;
        for (List<Order> orders : orderHistory.values()) {
            for (Order order : orders) {
                totalRevenue += order.getTotalAmount();
            }
        }
        return totalRevenue;
    }

    public static void main(String[] args) {
        OrderHistory orderHistory = new OrderHistory();
        User user1 = new User("user1");
        User user2 = new User("user2");

        Product product1 = new Product("1", "Product 1", "Description 1", 100.0, "Category 1");
        Product product2 = new Product("2", "Product 2", "Description 2", 50.0, "Category 2");

        Order order1 = new Order(user1, List.of(product1, product2));
        Order order2 = new Order(user2, List.of(product2));

        orderHistory.addOrder(user1, order1);
        orderHistory.addOrder(user2, order2);

        System.out.println("Total orders count: " + orderHistory.getTotalOrdersCount());
        System.out.println("Total revenue: $" + orderHistory.getTotalRevenue());
        System.out.println("Orders for user1: " + orderHistory.getOrdersByUser(user1));
    }
}