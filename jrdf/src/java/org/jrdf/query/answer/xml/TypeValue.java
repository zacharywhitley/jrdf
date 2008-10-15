package org.jrdf.query.answer.xml;

public class TypeValue {
    private String type;
    private String value;
    private String suffix;
    private String suffixType;

    public TypeValue() {
    }

    public TypeValue(String type, String value) {
        setValues(type, value, null, null);
    }

    public TypeValue(String type, String value, boolean isDatatype, String suffix) {
        if (isDatatype) {
            setValues(type, value, "datatype", suffix);
        } else {
            setValues(type, value, "xml:lang", suffix);
        }
    }

    private void setValues(String type, String value, String suffixType, String suffix) {
        this.type = type;
        this.value = value;
        this.suffixType = suffixType;
        this.suffix = suffix;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getSuffixType() {
        return suffixType;
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public String toString() {
        return "Type: " + type + " Value: " + value + " Suffix Type: " + suffixType + " Suffix: " + suffix;
    }
}
