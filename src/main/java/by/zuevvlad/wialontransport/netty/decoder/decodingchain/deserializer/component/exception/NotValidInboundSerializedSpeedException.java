package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception;

public final class NotValidInboundSerializedSpeedException extends RuntimeException {
    public NotValidInboundSerializedSpeedException() {

    }

    public NotValidInboundSerializedSpeedException(final String description) {
        super(description);
    }

    public NotValidInboundSerializedSpeedException(final Exception cause) {
        super(cause);
    }

    public NotValidInboundSerializedSpeedException(final String description, final Exception cause) {
        super(description, cause);
    }
}
