package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception;

public final class NotValidInboundSerializedCourseException extends RuntimeException {
    public NotValidInboundSerializedCourseException() {

    }

    public NotValidInboundSerializedCourseException(final String description) {
        super(description);
    }

    public NotValidInboundSerializedCourseException(final Exception cause) {
        super(cause);
    }

    public NotValidInboundSerializedCourseException(final String description, final Exception cause) {
        super(description, cause);
    }
}
