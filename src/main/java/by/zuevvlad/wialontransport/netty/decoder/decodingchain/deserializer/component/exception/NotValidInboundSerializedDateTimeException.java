package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception;

public final class NotValidInboundSerializedDateTimeException extends RuntimeException {
    public NotValidInboundSerializedDateTimeException() {

    }

    public NotValidInboundSerializedDateTimeException(final String description) {
        super(description);
    }

    public NotValidInboundSerializedDateTimeException(final Exception cause) {
        super(cause);
    }

    public NotValidInboundSerializedDateTimeException(final String description, final Exception cause) {
        super(description, cause);
    }
}
