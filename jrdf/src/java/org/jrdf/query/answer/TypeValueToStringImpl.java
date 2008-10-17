package org.jrdf.query.answer;

import org.jrdf.query.answer.xml.DatatypeType;
import static org.jrdf.query.answer.xml.DatatypeType.NONE;
import org.jrdf.query.answer.xml.TypeValue;

public class TypeValueToStringImpl implements TypeValueToString {
    public String[] convert(TypeValue[] results) {
        String[] stringResults = new String[results.length];
        for (int i = 0; i < results.length; i++) {
            TypeValue result = results[i];
            StringBuffer stringResult = new StringBuffer();
            stringResult.append(result.getValue());
            if (result.getSuffixType() != NONE) {
                appendSuffix(result, stringResult);
            }
            stringResults[i] = stringResult.toString();
        }
        return stringResults;
    }

    private void appendSuffix(TypeValue result, StringBuffer stringResult) {
        if (result.getSuffixType().equals(DatatypeType.DATATYPE)) {
            stringResult.append("^^\"" + result.getSuffix() + "\"");
        } else if (result.getSuffixType().equals(DatatypeType.XML_LANG)) {
            stringResult.append("@@\"" + result.getSuffix() + "\"");
        }
    }
}
