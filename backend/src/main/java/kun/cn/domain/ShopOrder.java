package kun.cn.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Entity @Table(name="shop_order") public class ShopOrder {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(unique=true, nullable=false) private String orderNo;
  private String customerName; private String recipientName; private String contactPhone; @Column(length=500) private String shippingAddress; private LocalDateTime createdAt; @Enumerated(EnumType.STRING) private Status status=Status.CREATED;
  private BigDecimal totalAmount;
  @OneToMany(mappedBy="order", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER) private List<OrderItem> items=new ArrayList<>();
  public enum Status { CREATED, ACCEPTED, COMPLETED, CANCELLED }
  public Long getId(){return id;} public String getOrderNo(){return orderNo;} public void setOrderNo(String v){orderNo=v;} public String getCustomerName(){return customerName;} public void setCustomerName(String v){customerName=v;} public String getRecipientName(){return recipientName;} public void setRecipientName(String v){recipientName=v;} public String getContactPhone(){return contactPhone;} public void setContactPhone(String v){contactPhone=v;} public String getShippingAddress(){return shippingAddress;} public void setShippingAddress(String v){shippingAddress=v;}
  public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;} public Status getStatus(){return status;} public void setStatus(Status v){status=v;} public BigDecimal getTotalAmount(){return totalAmount;} public void setTotalAmount(BigDecimal v){totalAmount=v;} public List<OrderItem> getItems(){return items;}
}
