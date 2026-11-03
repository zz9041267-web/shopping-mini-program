package kun.cn.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(length = 1000) private String description;
    @Column(length = 150) private String nameZh;
    @Column(length = 150) private String nameEn;
    @Column(length = 1000) private String descriptionZh;
    @Column(length = 1000) private String descriptionEn;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal price;
    @Lob @Column(columnDefinition = "LONGTEXT") private String imageBase64;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_display_images", joinColumns = @JoinColumn(name = "product_id"))
    @OrderColumn(name = "sort_order")
    @Lob @Column(name = "image_base64", columnDefinition = "LONGTEXT")
    private List<String> displayImagesBase64 = new ArrayList<>();
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_detail_images", joinColumns = @JoinColumn(name = "product_id"))
    @OrderColumn(name = "sort_order")
    @Lob @Column(name = "image_base64", columnDefinition = "LONGTEXT")
    private List<String> detailImagesBase64 = new ArrayList<>();
    private Integer stock = 0;
    private Boolean onSale = true;
    @ManyToOne(optional = false) private Category category;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "size_set_id") private SizeSet sizeSet;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "color_set_id") private ColorSet colorSet;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String value) { name = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { description = value; }
    public String getNameZh() { return nameZh; }
    public void setNameZh(String value) { nameZh = value; if (name == null || name.isBlank()) name = value; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String value) { nameEn = value; }
    public String getDescriptionZh() { return descriptionZh; }
    public void setDescriptionZh(String value) { descriptionZh = value; if (description == null || description.isBlank()) description = value; }
    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String value) { descriptionEn = value; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal value) { price = value; }
    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String value) { imageBase64 = value; }
    public List<String> getDisplayImagesBase64() { return displayImagesBase64; }
    public void setDisplayImagesBase64(List<String> value) { displayImagesBase64 = value == null ? new ArrayList<>() : new ArrayList<>(value); }
    public List<String> getDetailImagesBase64() { return detailImagesBase64; }
    public void setDetailImagesBase64(List<String> value) { detailImagesBase64 = value == null ? new ArrayList<>() : new ArrayList<>(value); }
    public Integer getStock() { return stock; }
    public void setStock(Integer value) { stock = value; }
    public Boolean getOnSale() { return onSale; }
    public void setOnSale(Boolean value) { onSale = value; }
    public Category getCategory() { return category; }
    public void setCategory(Category value) { category = value; }
    public SizeSet getSizeSet() { return sizeSet; }
    public void setSizeSet(SizeSet value) { sizeSet = value; }
    public ColorSet getColorSet() { return colorSet; }
    public void setColorSet(ColorSet value) { colorSet = value; }
}
