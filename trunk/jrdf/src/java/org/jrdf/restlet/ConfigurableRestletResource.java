package org.jrdf.restlet;

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
