/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2007 The JRDF Project.  All rights reserved.
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

package org.jrdf;

import org.jrdf.graph.Graph;
import org.jrdf.graph.mem.GraphFactory;
import org.jrdf.query.relation.AttributeComparator;
import org.jrdf.query.relation.AttributeValuePairComparator;
import org.jrdf.query.relation.TupleComparator;
import org.jrdf.sparql.SparqlConnection;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Uses the default wiring xml file or one given to it to construct various JRDF components using Spring.
 *
 * @author Andrew Newman
 * @version $Id: TestJRDFFactory.java 533 2006-06-04 17:50:31 +1000 (Sun, 04 Jun 2006) newmana $
 */
public final class JRDFFactoryImpl implements JRDFFactory {
    private static final String DEFAULT_WIRING_CONFIG = "wiring.xml";
    private static final ClassPathXmlApplicationContext BEAN_FACTORY =
        new ClassPathXmlApplicationContext(DEFAULT_WIRING_CONFIG);

    private JRDFFactoryImpl() {
    }

    public void refresh() {
        BEAN_FACTORY.refresh();
    }

    public static JRDFFactory getFactory() {
        return new JRDFFactoryImpl();
    }

    public ClassPathXmlApplicationContext getContext() {
        return BEAN_FACTORY;
    }

    public Graph getNewGraph() {
        GraphFactory graphFactory = (GraphFactory) BEAN_FACTORY.getBean("graphFactory");
        return graphFactory.getGraph();
    }

    public AttributeValuePairComparator getNewAttributeValuePairComparator() {
        return (AttributeValuePairComparator) BEAN_FACTORY.getBean("avpComparator");
    }

    public AttributeComparator getNewAttributeComparator() {
        return (AttributeComparator) BEAN_FACTORY.getBean("attributeComparator");
    }

    public TupleComparator getNewTupleComparator() {
        return (TupleComparator) BEAN_FACTORY.getBean("tupleComparator");
    }

    public SparqlConnection getNewSparqlConnection() {
        return (SparqlConnection) BEAN_FACTORY.getBean("sparqlConnection");
    }
}
