package by.zuevvlad.wialontransport.propertyfilereader.exception;

public final class PropertyFileReadingException extends RuntimeException {
    public PropertyFileReadingException() {
        super();
    }

    public PropertyFileReadingException(final String description) {
        super(description);
    }

    public PropertyFileReadingException(final Exception cause) {
        super(cause);
    }

    public PropertyFileReadingException(final String description, final Exception cause) {
        super(description, cause);
    }
}
