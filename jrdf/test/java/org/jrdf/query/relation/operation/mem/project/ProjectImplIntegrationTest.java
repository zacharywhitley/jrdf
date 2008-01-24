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

package org.jrdf.query.relation.operation.mem.project;

import junit.framework.TestCase;
import org.jrdf.TestJRDFFactory;
import org.jrdf.query.relation.Attribute;
import org.jrdf.query.relation.Relation;
import org.jrdf.query.relation.Tuple;
import org.jrdf.query.relation.operation.Project;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO3_OBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R2;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.POS_FOO4_PREDICATE_R5;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_PREDICATE_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECTOBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECTOBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECTOBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECTPREDICATE;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECTPREDICATEOBJECT;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECTPREDICATEOBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECTPREDICATEOBJECT_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECTPREDICATE_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECTPREDICATE_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR1_SUBJECT_R3;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.VAR_BAR2_PREDICATE_R4;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createASingleTuple;
import static org.jrdf.query.relation.operation.mem.RelationIntegrationTestUtil.createRelation;

import java.util.HashSet;
import java.util.Set;

/**
 * Tests the integration between project and other classes such as relations, attribute value pairs, etc.
 *
 * @author Andrew Newman
 * @version $Revision:$
 */
public class ProjectImplIntegrationTest extends TestCase {
    private static final TestJRDFFactory FACTORY = TestJRDFFactory.getFactory();
    private static final Project PROJECT = FACTORY.getNewProject();

    public void testRemoveNothing() {
        Set<Tuple> tuple = createTestTuples();

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        resultTuple.addAll(tmpTuple);
        tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO3_OBJECT_R4);
        resultTuple.addAll(tmpTuple);

        Set<Attribute> remove = new HashSet<Attribute>();
        checkExcludeProject(remove, createRelation(tuple), createRelation(resultTuple));
    }

    public void testRemoveSubject() {
        Set<Tuple> tuple = createTestTuples();

        Set<Tuple> resultTuple = createASingleTuple(POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        Set<Tuple> tmpTuple = createASingleTuple(POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        resultTuple.addAll(tmpTuple);
        tmpTuple = createASingleTuple(POS_FOO4_PREDICATE_R5, POS_FOO3_OBJECT_R4);
        resultTuple.addAll(tmpTuple);

        Set<Attribute> remove = new HashSet<Attribute>();
        remove.add(VAR_BAR1_SUBJECT);
        checkExcludeProject(remove, createRelation(tuple), createRelation(resultTuple));
    }

    public void testRemovePredicate() {
        Set<Tuple> tuple = createTestTuples();

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO3_OBJECT_R3);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO3_OBJECT_R4);
        resultTuple.addAll(tmpTuple);

        Set<Attribute> remove = new HashSet<Attribute>();
        remove.add(POS_FOO4_PREDICATE);
        checkExcludeProject(remove, createRelation(tuple), createRelation(resultTuple));
    }

    public void testKeepSubject() {
        Set<Tuple> tuple = createTestTuples();
        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3);
        Set<Attribute> keep = new HashSet<Attribute>();
        keep.add(VAR_BAR1_SUBJECT);
        checkIncludeProject(keep, createRelation(tuple), createRelation(resultTuple));
    }

    public void testKeepSubjectPredicate() {
        Set<Tuple> tuple = createTestTuples();

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R2);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3);
        resultTuple.addAll(tmpTuple);
        tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R5);
        resultTuple.addAll(tmpTuple);

        Set<Attribute> keep = new HashSet<Attribute>();
        keep.add(VAR_BAR1_SUBJECT);
        keep.add(POS_FOO4_PREDICATE);
        checkIncludeProject(keep, createRelation(tuple), createRelation(resultTuple));
    }

    public void testSameSubjectPredicate() {
        Set<Tuple> tuple = createASingleTuple(VAR_BAR1_SUBJECTPREDICATE_R3, POS_FOO3_OBJECT_R3);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECTPREDICATE_R4, POS_FOO3_OBJECT_R4);
        tuple.addAll(tmpTuple);

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECTPREDICATE_R3);
        tmpTuple = createASingleTuple(VAR_BAR1_SUBJECTPREDICATE_R4);
        resultTuple.addAll(tmpTuple);

        Set<Attribute> keep = new HashSet<Attribute>();
        keep.add(VAR_BAR1_SUBJECTPREDICATE);
        checkIncludeProject(keep, createRelation(tuple), createRelation(resultTuple));
    }

    public void testSameSubjectObject() {
        Set<Tuple> tuple = createASingleTuple(VAR_BAR1_SUBJECTOBJECT_R3, VAR_BAR2_PREDICATE_R4);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_PREDICATE_R4, VAR_BAR1_SUBJECTOBJECT_R4);
        tuple.addAll(tmpTuple);

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECTOBJECT_R3);
        tmpTuple = createASingleTuple(VAR_BAR1_SUBJECTOBJECT_R4);
        resultTuple.addAll(tmpTuple);

        Set<Attribute> keep = new HashSet<Attribute>();
        keep.add(VAR_BAR1_SUBJECTOBJECT);
        checkIncludeProject(keep, createRelation(tuple), createRelation(resultTuple));
    }

    public void testSameSubjectPredicateObject() {
        Set<Tuple> tuple = createASingleTuple(VAR_BAR1_SUBJECTPREDICATEOBJECT_R3);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECTPREDICATEOBJECT_R4);
        tuple.addAll(tmpTuple);

        Set<Tuple> resultTuple = createASingleTuple(VAR_BAR1_SUBJECTPREDICATEOBJECT_R3);
        tmpTuple = createASingleTuple(VAR_BAR1_SUBJECTPREDICATEOBJECT_R4);
        resultTuple.addAll(tmpTuple);

        Set<Attribute> keep = new HashSet<Attribute>();
        keep.add(VAR_BAR1_SUBJECTPREDICATEOBJECT);
        checkIncludeProject(keep, createRelation(tuple), createRelation(resultTuple));
    }

    private Set<Tuple> createTestTuples() {
        Set<Tuple> tuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R2, POS_FOO3_OBJECT_R3);
        Set<Tuple> tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R3, POS_FOO3_OBJECT_R4);
        tuple.addAll(tmpTuple);
        tmpTuple = createASingleTuple(VAR_BAR1_SUBJECT_R3, POS_FOO4_PREDICATE_R5, POS_FOO3_OBJECT_R4);
        tuple.addAll(tmpTuple);
        return tuple;
    }

    private void checkExcludeProject(Set<Attribute> remove, Relation relation, Relation expectedRelation) {
        Relation actualRelation = PROJECT.exclude(relation, remove);
        assertEquals(expectedRelation, actualRelation);
    }

    private void checkIncludeProject(Set<Attribute> keep, Relation relation, Relation expectedRelation) {
        Relation actualRelation = PROJECT.include(relation, keep);
        assertEquals(expectedRelation, actualRelation);
    }
}
