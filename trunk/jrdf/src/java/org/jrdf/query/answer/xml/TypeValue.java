package org.jrdf.query.answer.xml;

import static org.jrdf.query.answer.xml.DatatypeType.*;

public class TypeValue {
    private SparqlResultType type;
    private String value;
    private String suffix;
    private DatatypeType suffixType;

    public TypeValue() {
    }

    public TypeValue(SparqlResultType type, String value) {
        setValues(type, value, null, null);
    }

    public TypeValue(SparqlResultType type, String value, boolean isDatatype, String suffix) {
        if (isDatatype) {
            setValues(type, value, DATATYPE, suffix);
        } else {
            setValues(type, value, XML_LANG, suffix);
        }
    }

    private void setValues(SparqlResultType type, String value, DatatypeType suffixType, String suffix) {
        this.type = type;
        this.value = value;
        this.suffixType = suffixType;
        this.suffix = suffix;
    }

    public SparqlResultType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public DatatypeType getSuffixType() {
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
