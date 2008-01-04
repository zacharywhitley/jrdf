/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2008 The JRDF Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        the JRDF Project (http://jrdf.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The JRDF Project" and "JRDF" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, please contact
 *    newmana@users.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "JRDF"
 *    nor may "JRDF" appear in their names without prior written
 *    permission of the JRDF Project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the JRDF Project.  For more
 * information on JRDF, please see <http://jrdf.sourceforge.net/>.
 *
 */

package org.jrdf.persistence;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

/**
 * @author Peter Bednar
 * @author Jozef Wagner, http://wagjo.com/
 */
class PropertyMapping {
    private URI uri;
    private boolean inverse;
    private Class targetEntity;
    private Field field;
    private PropertyDescriptor property;

    public PropertyMapping(Field field, Annotation annotation, String namespace) {
        this.field = field;
        field.setAccessible(true);
        setMapping(annotation, namespace);
    }

    public PropertyMapping(PropertyDescriptor property, Annotation annotation, String namespace) {
        this.property = property;
        field.setAccessible(true);
        setMapping(annotation, namespace);
    }

    public URI getURI() {
        return uri;
    }

    public Class getPropertyType() {
        return field != null ? field.getType() : property.getPropertyType();
    }

    public Class getTargetEntityType() {
        return targetEntity;
    }

    public boolean isInverse() {
        return inverse;
    }

    public boolean isCollection() {
        return Collection.class.isAssignableFrom(getPropertyType());
    }

    public Object getValue(Object obj) {
        try {
            if (field != null) {
                return field.get(obj);
            } else {
                return property.getReadMethod().invoke(obj);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("You tried to get from \"" + field + "\"");
        }
    }

    public void setValue(Object obj, Object value) {
        try {
            if (field != null) {
                field.set(obj, value);
            } else {
                property.getWriteMethod().invoke(obj, value);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("You tried to set \"" + field + "\" with value: " + value);
        }
    }

    private void setMapping(Annotation annotation, String namespace) {
        String uri = null;

        if (annotation instanceof Property) {
            Property pa = (Property) annotation;

            uri = pa.URI();
            if (uri.equals("")) {
                String ns = pa.namespace().equals("") ? namespace :
                    pa.namespace();
                uri = namespace + pa.name();
            }

            targetEntity = pa.targetEntity();
            if (targetEntity == void.class) {
                targetEntity = getPropertyType();
            }

        } else if (annotation instanceof InverseProperty) {
            InverseProperty pa = (InverseProperty) annotation;

            uri = pa.URI();
            if (uri.equals("")) {
                String ns = pa.namespace().equals("") ? namespace : pa.namespace();
                uri = namespace + pa.name();
            }

            inverse = true;
            targetEntity = pa.targetEntity();
            if (targetEntity == void.class) {
                targetEntity = getPropertyType();
            }
        }

        if (uri != null) {
            try {
                this.uri = new URI(uri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Incorrect URI syntax.");
            }
        }
    }
}
