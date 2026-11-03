package kun.cn.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "customer_address")
public class CustomerAddress {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 100) private String customerName;
    @Column(nullable = false, length = 100) private String recipientName;
    @Column(nullable = false, length = 40) private String contactPhone;
    @Column(nullable = false, length = 500) private String shippingAddress;
    private boolean defaultAddress;
    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String value) { customerName = value; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String value) { recipientName = value; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String value) { contactPhone = value; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String value) { shippingAddress = value; }
    public boolean isDefaultAddress() { return defaultAddress; }
    public void setDefaultAddress(boolean value) { defaultAddress = value; }
}
