package org.jrdf.restlet;

/**
 * A Spring-configurable Restlet Resource.
 *
 * @author Konstantin Laufer (laufer@cs.luc.edu)
 */
public final class ResourcePropertyHolder {
    private boolean modifiable;
    private boolean available;
    private boolean negotiateContent;
    private boolean readable;

    public boolean isModifiable() {
        return modifiable;
    }

    public void setModifiable(final boolean modifiable) {
        this.modifiable = modifiable;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isNegotiateContent() {
        return negotiateContent;
    }

    public void setNegotiateContent(boolean negotiateContent) {
        this.negotiateContent = negotiateContent;
    }

    public boolean isReadable() {
        return readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }
}
