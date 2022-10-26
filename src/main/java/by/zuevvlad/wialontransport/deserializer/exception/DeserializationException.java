package by.zuevvlad.wialontransport.deserializer.exception;

public final class DeserializationException extends RuntimeException {
    public DeserializationException() {
        super();
    }

    public DeserializationException(final String description) {
        super(description);
    }

    public DeserializationException(final Exception cause) {
        super(cause);
    }

    public DeserializationException(final String description, final Exception cause) {
        super(description, cause);
    }
}
