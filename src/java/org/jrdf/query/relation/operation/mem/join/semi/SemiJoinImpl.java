/*
 * $Header$
 * $Revision: 982 $
 * $Date: 2006-12-08 18:42:51 +1000 (Fri, 08 Dec 2006) $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2009 The JRDF Project.  All rights reserved.
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

package org.jrdf.query.relation.operation.mem.join.semi;

import org.jrdf.query.relation.EvaluatedRelation;
import static org.jrdf.query.relation.constants.RelationDEE.RELATION_DEE;
import static org.jrdf.query.relation.constants.RelationDUM.RELATION_DUM;
import org.jrdf.query.relation.operation.DyadicJoin;
import org.jrdf.query.relation.operation.mem.common.RelationProcessor;
import org.jrdf.query.relation.operation.mem.join.TupleEngine;

import java.util.LinkedHashSet;

public final class SemiJoinImpl implements DyadicJoin {
    private final TupleEngine tupleEngine;
    private final RelationProcessor relationProcessor;

    public SemiJoinImpl(RelationProcessor newRelationProcessor, TupleEngine newTupleEngine) {
        this.tupleEngine = newTupleEngine;
        this.relationProcessor = newRelationProcessor;
    }

    public EvaluatedRelation join(EvaluatedRelation relation1, EvaluatedRelation relation2) {
        EvaluatedRelation relation = isDeeDumOrSame(relation1, relation2);
        if (relation == null) {
            LinkedHashSet<EvaluatedRelation> relations = new LinkedHashSet<EvaluatedRelation>();
            relations.add(relation1);
            relations.add(relation2);
            relation = relationProcessor.processRelations(relations, tupleEngine);
        }
        return relation;
    }

    private EvaluatedRelation isDeeDumOrSame(EvaluatedRelation relation1, EvaluatedRelation relation2) {
        EvaluatedRelation relation = null;
        if (relation1 == RELATION_DUM || relation2 == RELATION_DUM) {
            relation = RELATION_DUM;
        } else if (relation1 == RELATION_DEE) {
            relation = RELATION_DEE;
        } else if (relation1 == relation2 || relation1.equals(relation2)) {
            relation = relation1;
        }
        return relation;
    }
}
