package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception;

public final class NotValidInboundSerializedLongitudeException extends RuntimeException {
    public NotValidInboundSerializedLongitudeException() {

    }

    public NotValidInboundSerializedLongitudeException(final String description) {
        super(description);
    }

    public NotValidInboundSerializedLongitudeException(final Exception cause) {
        super(cause);
    }

    public NotValidInboundSerializedLongitudeException(final String description, final Exception cause) {
        super(description, cause);
    }
}
