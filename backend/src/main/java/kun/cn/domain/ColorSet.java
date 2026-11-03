package kun.cn.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ColorSet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String nameZh;
    @Column(nullable = false) private String nameEn;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "color_set_options", joinColumns = @JoinColumn(name = "color_set_id"))
    @OrderColumn(name = "sort_order")
    private List<ColorOption> options = new ArrayList<>();

    public Long getId() { return id; }
    public String getNameZh() { return nameZh; }
    public void setNameZh(String value) { nameZh = value; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String value) { nameEn = value; }
    public List<ColorOption> getOptions() { return options; }
    public void setOptions(List<ColorOption> value) { options = value == null ? new ArrayList<>() : new ArrayList<>(value); }
}
