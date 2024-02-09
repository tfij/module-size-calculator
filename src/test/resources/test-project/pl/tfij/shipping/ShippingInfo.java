import java.util.Date;

public class ShippingInfo {
    private Long orderId;
    private ShippingAddress shippingAddress;
    private Date shippingDate;
    private Date estimatedDeliveryDate;
    private String trackingNumber;
    private String shippingMethod;
    private Double shippingCost;

    public ShippingInfo(Long orderId, ShippingAddress shippingAddress, Date shippingDate, Date estimatedDeliveryDate, String trackingNumber, String shippingMethod, Double shippingCost) {
        this.orderId = orderId;
        this.shippingAddress = shippingAddress;
        this.shippingDate = shippingDate;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.trackingNumber = trackingNumber;
        this.shippingMethod = shippingMethod;
        this.shippingCost = shippingCost;
    }

    // Getters and setters

    // Additional methods to reach 50+ LOC
}
