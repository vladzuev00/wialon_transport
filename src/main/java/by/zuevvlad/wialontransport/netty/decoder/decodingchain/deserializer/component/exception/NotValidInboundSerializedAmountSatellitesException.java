package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception;

public final class NotValidInboundSerializedAmountSatellitesException extends RuntimeException {
    public NotValidInboundSerializedAmountSatellitesException() {

    }

    public NotValidInboundSerializedAmountSatellitesException(final String description) {
        super(description);
    }

    public NotValidInboundSerializedAmountSatellitesException(final Exception cause) {
        super(cause);
    }

    public NotValidInboundSerializedAmountSatellitesException(final String description, final Exception cause) {
        super(description, cause);
    }
}
