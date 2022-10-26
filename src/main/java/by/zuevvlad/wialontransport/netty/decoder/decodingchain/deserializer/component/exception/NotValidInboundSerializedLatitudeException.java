package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception;

public final class NotValidInboundSerializedLatitudeException extends RuntimeException {
    public NotValidInboundSerializedLatitudeException() {

    }

    public NotValidInboundSerializedLatitudeException(final String description) {
        super(description);
    }

    public NotValidInboundSerializedLatitudeException(final Exception cause) {
        super(cause);
    }

    public NotValidInboundSerializedLatitudeException(final String description, final Exception cause) {
        super(description, cause);
    }
}
