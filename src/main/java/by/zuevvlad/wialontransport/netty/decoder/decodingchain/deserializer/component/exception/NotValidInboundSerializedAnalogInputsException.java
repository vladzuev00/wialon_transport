package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception;

public final class NotValidInboundSerializedAnalogInputsException extends RuntimeException {
    public NotValidInboundSerializedAnalogInputsException() {

    }

    public NotValidInboundSerializedAnalogInputsException(final String description) {
        super(description);
    }

    public NotValidInboundSerializedAnalogInputsException(final Exception cause) {
        super(cause);
    }

    public NotValidInboundSerializedAnalogInputsException(final String description, final Exception cause) {
        super(description, cause);
    }
}
