package kun.cn.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class SizeOption {
    private String code;
    private Integer lengthValue;
    private Integer widthValue;

    public SizeOption() {}
    public SizeOption(String code, Integer lengthValue, Integer widthValue) {
        this.code = code;
        this.lengthValue = lengthValue;
        this.widthValue = widthValue;
    }
    public String getCode() { return code; }
    public void setCode(String value) { code = value; }
    public Integer getLengthValue() { return lengthValue; }
    public void setLengthValue(Integer value) { lengthValue = value; }
    public Integer getWidthValue() { return widthValue; }
    public void setWidthValue(Integer value) { widthValue = value; }
}
