package kun.cn.domain;

import jakarta.persistence.*;

@Entity
public class ShopProfile {
    @Id private Long id = 1L;
    @Column(nullable = false) private String name = "健儿宝童装批发";
    private String notice = "童装批发，支持选款咨询与批量采购";
    private String nameZh = "健儿宝童装批发";
    private String nameEn = "Jian'erbao Kidswear";
    private String noticeZh = "童装批发，支持选款咨询与批量采购";
    private String noticeEn = "Children's clothing wholesale. Style selection and bulk orders welcome.";
    @Lob @Column(columnDefinition = "LONGTEXT") private String coverImageBase64;
    @Lob @Column(columnDefinition = "LONGTEXT") private String wechatQrBase64;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String value) { name = value; }
    public String getNotice() { return notice; }
    public void setNotice(String value) { notice = value; }
    public String getNameZh() { return nameZh; }
    public void setNameZh(String value) { nameZh = value; if (name == null || name.isBlank()) name = value; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String value) { nameEn = value; }
    public String getNoticeZh() { return noticeZh; }
    public void setNoticeZh(String value) { noticeZh = value; if (notice == null || notice.isBlank()) notice = value; }
    public String getNoticeEn() { return noticeEn; }
    public void setNoticeEn(String value) { noticeEn = value; }
    public String getCoverImageBase64() { return coverImageBase64; }
    public void setCoverImageBase64(String value) { coverImageBase64 = value; }
    public String getWechatQrBase64() { return wechatQrBase64; }
    public void setWechatQrBase64(String value) { wechatQrBase64 = value; }
}
