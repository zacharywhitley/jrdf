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

package org.jrdf.query.server;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.Variant;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A Spring-configurable Restlet Resource.
 *
 * @author Konstantin Laufer (laufer@cs.luc.edu)
 */
public class ConfigurableRestletResource extends Resource {
    private LinkedHashMap<MediaType, RepresentationFactory> representationTemplates;

    // TODO generation of representations of response entities methods other than GET
    // TODO think about how to deal with dependencies on so many concrete representations

    @Override
    public void init(final Context context, final Request request, final Response response) {
        // workaround for overzealous init method
        final ResourcePropertyHolder backup = new ResourcePropertyHolder();
        BeanUtils.copyProperties(this, backup);
        super.init(context, request, response);
        BeanUtils.copyProperties(backup, this);
    }

    @Override
    public List<Variant> getVariants() {
        final List<Variant> variants = new LinkedList<Variant>();
        for (MediaType mediaType : getRepresentationTemplates().keySet()) {
            variants.add(new Variant(mediaType));
        }
        return variants;
    }

    @Override
    public void setVariants(List<Variant> variants) {
        throw new UnsupportedOperationException("use setRepresentationTemplates instead");
    }

    public Map<MediaType, RepresentationFactory> getRepresentationTemplates() {
        if (representationTemplates != null) {
            return representationTemplates;
        } else {
            return Collections.emptyMap();
        }
    }

    public void setRepresentationTemplates(final Map<MediaType, RepresentationFactory> newRepresentationTemplates) {
        if (this.representationTemplates == null) {
            this.representationTemplates = new LinkedHashMap<MediaType, RepresentationFactory>();
        } else {
            this.representationTemplates.clear();
        }
        this.representationTemplates.putAll(newRepresentationTemplates);
    }

    protected Representation createTemplateRepresentation(final MediaType mediaType,
        final Map<String, Object> dataModel) {
        final RepresentationFactory factory = representationTemplates.get(mediaType);
        return factory.createRepresentation(mediaType, dataModel);
    }

}
