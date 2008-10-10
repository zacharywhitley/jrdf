package org.jrdf.restlet;

import freemarker.template.Configuration;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.resource.Representation;

import java.io.IOException;
import java.util.Map;

public class FreemarkerRepresentationFactory implements RepresentationFactory {
    private MediaType mediaType;
    private String templateName;
    private Configuration freemarkerConfig;

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(final MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    public void setFreemarkerConfig(final Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    public Representation createRepresentation(final MediaType defaultMediaType, final Map<String, Object> dataModel) {
        MediaType actualMediaType = getMediaType();
        if (actualMediaType == null) {
            actualMediaType = defaultMediaType;
        }
        try {
            return new TemplateRepresentation(freemarkerConfig.getTemplate(templateName), dataModel, actualMediaType);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
