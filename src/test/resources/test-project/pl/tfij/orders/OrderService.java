import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private List<Order> orders;
    private OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orders = new ArrayList<>();
        this.orderRepository = orderRepository;
    }

    public void placeOrder(User user, List<Product> products) {
        Order order = new Order(user, products);
        orders.add(order);
        orderRepository.saveOrder(order);
    }

    public List<Order> getAllOrders() {
        return orders;
    }

    public List<Order> getOrdersByUser(User user) {
        List<Order> userOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.getUser().equals(user)) {
                userOrders.add(order);
            }
        }
        return userOrders;
    }

    public void cancelOrder(Order order) {
        if (orders.contains(order)) {
            orders.remove(order);
            orderRepository.deleteOrder(order);
        }
    }

    public double calculateTotalRevenue() {
        double totalRevenue = 0;
        for (Order order : orders) {
            totalRevenue += order.getTotalAmount();
        }
        return totalRevenue;
    }

    public static void main(String[] args) {
        OrderRepository orderRepository = new OrderRepository();
        OrderService orderService = new OrderService(orderRepository);

        User user1 = new User("user1");
        User user2 = new User("user2");

        Product product1 = new Product("1", "Product 1", "Description 1", 100.0, "Category 1");
        Product product2 = new Product("2", "Product 2", "Description 2", 50.0, "Category 2");

        List<Product> user1Products = new ArrayList<>();
        user1Products.add(product1);
        user1Products.add(product2);

        List<Product> user2Products = new ArrayList<>();
        user2Products.add(product2);

        orderService.placeOrder(user1, user1Products);
        orderService.placeOrder(user2, user2Products);

        System.out.println("Total revenue: $" + orderService.calculateTotalRevenue());
        System.out.println("All orders:");
        for (Order order : orderService.getAllOrders()) {
            System.out.println(order);
        }
    }
}