package org.jrdf.query.answer.xml;

import static org.jrdf.query.answer.xml.DatatypeType.DATATYPE;
import static org.jrdf.query.answer.xml.DatatypeType.XML_LANG;
import static org.jrdf.query.answer.xml.DatatypeType.*;
import static org.jrdf.query.answer.xml.SparqlResultType.*;
import static org.jrdf.util.EqualsUtil.isNull;
import static org.jrdf.util.EqualsUtil.sameReference;

public class TypeValueImpl implements TypeValue {
    private SparqlResultType type;
    private String value;
    private String suffix;
    private DatatypeType suffixType;
    private static final int PRIME = 31;

    public TypeValueImpl() {
        setValues(UNBOUND, "", NONE, "");
    }

    public TypeValueImpl(SparqlResultType type, String value) {
        setValues(type, value, NONE, "");
    }

    public TypeValueImpl(SparqlResultType type, String value, boolean isDatatype, String suffix) {
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

    public int hashCode() {
        int result;
        result = (type != null ? type.hashCode() : 0);
        result = PRIME * result + (value != null ? value.hashCode() : 0);
        result = PRIME * result + (suffix != null ? suffix.hashCode() : 0);
        result = PRIME * result + (suffixType != null ? suffixType.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (isNull(obj)) {
            return false;
        }
        if (sameReference(this, obj)) {
            return true;
        }
        try {
            TypeValue typeValue = (TypeValue) obj;
            return compareTypeAndValue(typeValue) && compareSuffixAndSuffixType(typeValue);
        } catch (ClassCastException cce) {
            return false;
        }
    }

    private boolean compareTypeAndValue(TypeValue typeValue) {
        return typeValue.getType().equals(getType()) && typeValue.getValue().equals(typeValue.getValue());
    }

    private boolean compareSuffixAndSuffixType(TypeValue typeValue) {
        return typeValue.getSuffixType().equals(getSuffixType()) && typeValue.getSuffix().equals(getSuffix());
    }

    @Override
    public String toString() {
        return "Type: " + type + " Value: " + value + " Suffix Type: " + suffixType + " Suffix: " + suffix;
    }
}
