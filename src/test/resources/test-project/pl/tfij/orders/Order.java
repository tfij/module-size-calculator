class Order {
    private User user;
    private List<Product> products;

    public Order(User user, List<Product> products) {
        this.user = user;
        this.products = products;
    }

    public double getTotalAmount() {
        double totalAmount = 0;
        for (Product product : products) {
            totalAmount += product.getPrice();
        }
        return totalAmount;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Order{" +
                "user=" + user +
                ", products=" + products +
                '}';
    }
}