import java.util.Date;
import java.util.UUID;

public class ProductDiscount {
    private UUID discountId;
    private String discountCode;
    private double discountAmount;
    private Date startDate;
    private Date endDate;
    private boolean active;
    private String description;

    public ProductDiscount(UUID discountId, String discountCode, double discountAmount, Date startDate, Date endDate, boolean active, String description) {
        this.discountId = discountId;
        this.discountCode = discountCode;
        this.discountAmount = discountAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.description = description;
    }

    public UUID getDiscountId() {
        return discountId;
    }

    public void setDiscountId(UUID discountId) {
        this.discountId = discountId;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ProductDiscount{" +
                "discountId=" + discountId +
                ", discountCode='" + discountCode + '\'' +
                ", discountAmount=" + discountAmount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", active=" + active +
                ", description='" + description + '\'' +
                '}';
    }

    // Other methods...

    public boolean isExpired() {
        Date currentDate = new Date();
        return currentDate.after(endDate);
    }
}
