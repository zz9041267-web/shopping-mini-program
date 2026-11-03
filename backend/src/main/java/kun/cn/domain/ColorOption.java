package kun.cn.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class ColorOption {
    private String code;
    private String nameZh;
    private String nameEn;
    private String hexValue;

    public ColorOption() {}
    public ColorOption(String code, String nameZh, String nameEn, String hexValue) {
        this.code = code;
        this.nameZh = nameZh;
        this.nameEn = nameEn;
        this.hexValue = hexValue;
    }
    public String getCode() { return code; }
    public void setCode(String value) { code = value; }
    public String getNameZh() { return nameZh; }
    public void setNameZh(String value) { nameZh = value; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String value) { nameEn = value; }
    public String getHexValue() { return hexValue; }
    public void setHexValue(String value) { hexValue = value; }
}
