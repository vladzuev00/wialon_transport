package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception;

//Indicates error of package structure
public final class NotValidInboundSerializedDataException extends RuntimeException {
    public NotValidInboundSerializedDataException() {

    }

    public NotValidInboundSerializedDataException(final String description) {
        super(description);
    }

    public NotValidInboundSerializedDataException(final Exception cause) {
        super(cause);
    }

    public NotValidInboundSerializedDataException(final String description, final Exception cause) {
        super(description, cause);
    }
}
