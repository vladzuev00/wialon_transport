package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception;

public final class NotValidInboundSerializedHeightException extends RuntimeException {
    public NotValidInboundSerializedHeightException() {

    }

    public NotValidInboundSerializedHeightException(final String description) {
        super(description);
    }

    public NotValidInboundSerializedHeightException(final Exception cause) {
        super(cause);
    }

    public NotValidInboundSerializedHeightException(final String description, final Exception cause) {
        super(description, cause);
    }
}
