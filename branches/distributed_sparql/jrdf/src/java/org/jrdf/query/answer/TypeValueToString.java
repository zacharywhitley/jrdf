package org.jrdf.query.answer;

import org.jrdf.query.answer.xml.TypeValue;

/**
 * Created by IntelliJ IDEA.
 * User: anewman
 * Date: 17/10/2008
 * Time: 13:53:34
 * To change this template use File | Settings | File Templates.
 */
public interface TypeValueToString {
    String[] convert(TypeValue[] results);
}
