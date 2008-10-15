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

    public boolean equals(Object obj) {
        if (isNull(obj)) {
            return false;
        }
        if (sameReference(this, obj)) {
            return true;
        }
        try {
            TypeValue typeValue = (TypeValue) obj;
            return typeValue.getType().equals(getType()) && typeValue.getValue().equals(typeValue.getValue()) &&
                typeValue.getSuffixType().equals(getSuffixType()) && typeValue.getSuffix().equals(getSuffix());
        } catch (ClassCastException cce) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Type: " + type + " Value: " + value + " Suffix Type: " + suffixType + " Suffix: " + suffix;
    }
}
