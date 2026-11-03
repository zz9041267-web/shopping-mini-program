package kun.cn.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long productId;
    private String productName;
    private String productNameEn;
    private BigDecimal unitPrice;
    private Integer quantity;
    private String sizeCode;
    private Integer sizeLength;
    private Integer sizeWidth;
    private String colorCode;
    private String colorNameZh;
    private String colorNameEn;
    private String colorHexValue;
    @JsonIgnore @ManyToOne @JoinColumn(name = "order_id") private ShopOrder order;

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long value) { productId = value; }
    public String getProductName() { return productName; }
    public void setProductName(String value) { productName = value; }
    public String getProductNameEn() { return productNameEn; }
    public void setProductNameEn(String value) { productNameEn = value; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal value) { unitPrice = value; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer value) { quantity = value; }
    public String getSizeCode() { return sizeCode; }
    public void setSizeCode(String value) { sizeCode = value; }
    public Integer getSizeLength() { return sizeLength; }
    public void setSizeLength(Integer value) { sizeLength = value; }
    public Integer getSizeWidth() { return sizeWidth; }
    public void setSizeWidth(Integer value) { sizeWidth = value; }
    public String getColorCode() { return colorCode; }
    public void setColorCode(String value) { colorCode = value; }
    public String getColorNameZh() { return colorNameZh; }
    public void setColorNameZh(String value) { colorNameZh = value; }
    public String getColorNameEn() { return colorNameEn; }
    public void setColorNameEn(String value) { colorNameEn = value; }
    public String getColorHexValue() { return colorHexValue; }
    public void setColorHexValue(String value) { colorHexValue = value; }
    public ShopOrder getOrder() { return order; }
    public void setOrder(ShopOrder value) { order = value; }
}
