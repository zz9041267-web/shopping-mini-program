package kun.cn.domain;

import jakarta.persistence.*;

@Entity
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String name;
    @Column(length = 100) private String nameZh;
    @Column(length = 100) private String nameEn;
    private Integer sortOrder = 0;

    public Category() {}
    public Category(String nameZh, String nameEn, Integer sortOrder) {
        this.name = nameZh;
        this.nameZh = nameZh;
        this.nameEn = nameEn;
        this.sortOrder = sortOrder;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String value) { name = value; }
    public String getNameZh() { return nameZh; }
    public void setNameZh(String value) { nameZh = value; if (name == null || name.isBlank()) name = value; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String value) { nameEn = value; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer value) { sortOrder = value; }
}
