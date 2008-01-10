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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Peter Bednar
 * @author Jozef Wagner, http://wagjo.com/
 */
public class RDFIntrospector {

    private static Map<Class, ClassMapping> mappings = Collections.synchronizedMap(new HashMap<Class, ClassMapping>());

    public static boolean isRDFResource(Class cls) {
        return cls.isAnnotationPresent(RDFResource.class);
    }

    public static URI getURI(Object obj) {
        return getMappings(obj).getURI(obj);
    }

    public static ClassMapping getMappings(Object obj) {
        return getMappings(obj.getClass());
    }

    public static ClassMapping getMappings(Class cls) {
        try {
            ClassMapping cm = mappings.get(cls);
            if (cm == null) {
                cm = createMappings(cls);
                mappings.put(cls, cm);
            }
            return cm;
        } catch (IntrospectionException ie) {
            throw new IllegalArgumentException();
        }
    }

    private static ClassMapping createMappings(Class cls) throws IntrospectionException {

        if (!isRDFResource(cls)) {
            throw new IllegalArgumentException("@RDFResource annotation is not present.");
        }

        Class c = cls;
        List<URI> typeURIs = new LinkedList<URI>();

        PropertyMapping urid = null;
        Set<PropertyMapping> properties = new LinkedHashSet<PropertyMapping>();

        do {
            if (c.isAnnotationPresent(RDFResource.class)) {
                addClassTypes(c, typeURIs);
                String ns = getClassNamespace(c);

                for (Field field : c.getDeclaredFields()) {
                    Annotation annotation = getPropertyAnnotation(field);
                    if (annotation != null) {
                        PropertyMapping pm = new PropertyMapping(field,
                            annotation,
                            ns);

                        if (pm.getURI() == null) {
                            urid = pm;
                        } else {
                            properties.add(pm);
                        }
                    }
                }
            }
            c = c.getSuperclass();
        } while (c != null);

        PropertyDescriptor[] pd =
            Introspector.getBeanInfo(cls).getPropertyDescriptors();

        for (PropertyDescriptor property : pd) {
            Annotation annotation = getPropertyAnnotation(property);
            if (annotation != null) {
                String ns = getDeclaredNamespace(property, cls);
                PropertyMapping pm = new PropertyMapping(property, annotation,
                    ns);

                if (pm.getURI() == null) {
                    urid = pm;
                } else {
                    properties.add(pm);
                }
            }
        }

        if (urid == null) {
            throw new IllegalArgumentException("@URId property is not specified.");
        }

        return new ClassMapping(cls, getClassNamespace(cls), typeURIs.toArray(new URI[typeURIs.size()]), urid,
            properties);
    }

    private static String getClassNamespace(Class cls) {
        RDFResource resource = (RDFResource) cls.getAnnotation(RDFResource.class);
        String namespace = resource.namespace();
        if (namespace.length() == 0) {
            namespace = cls.getPackage().getName();
        }
        return namespace;
    }

    private static void addClassTypes(Class cls, List<URI> typeURIs) {
        RDFResource resource = (RDFResource) cls.getAnnotation(RDFResource.class);
        try {
            if (resource.typeURIs().length != 0) {
                for (String uri : resource.typeURIs()) {
                    typeURIs.add(new URI(uri));
                }
            } else if (resource.typeNames().length != 0) {
                String namespace = getClassNamespace(cls);

                for (String name : resource.typeNames()) {
                    typeURIs.add(new URI(namespace + name));
                }
            }
        } catch (URISyntaxException use) {
            throw new IllegalArgumentException("Incorrect URI syntax.");
        }
    }

    private static Annotation getPropertyAnnotation(AnnotatedElement elm) {
        if (elm.isAnnotationPresent(URId.class)) {
            return elm.getAnnotation(URId.class);
        }

        if (elm.isAnnotationPresent(Property.class)) {
            return elm.getAnnotation(Property.class);
        }

        if (elm.isAnnotationPresent(InverseProperty.class)) {
            return elm.getAnnotation(InverseProperty.class);
        }

        return null;
    }

    private static Annotation getPropertyAnnotation(PropertyDescriptor property) {
        Method read = property.getReadMethod();
        if (read != null) {
            Annotation annot = getPropertyAnnotation(read);
            if (annot != null) {
                return annot;
            }
        }

        Method write = property.getWriteMethod();
        if (write != null) {
            Annotation annot = getPropertyAnnotation(write);
            if (annot != null) {
                return annot;
            }
        }

        return null;
    }

    private static String getDeclaredNamespace(PropertyDescriptor property, Class cls) {
        Method get = property.getReadMethod();
        Method set = property.getWriteMethod();
        if (getPropertyAnnotation(get) != null) {
            cls = get.getDeclaringClass();
        } else if (getPropertyAnnotation(set) != null) {
            cls = set.getDeclaringClass();
        }
        return getClassNamespace(cls);
    }
}
