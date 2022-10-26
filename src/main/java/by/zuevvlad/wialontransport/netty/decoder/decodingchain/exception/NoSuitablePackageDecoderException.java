package by.zuevvlad.wialontransport.netty.decoder.decodingchain.exception;

public final class NoSuitablePackageDecoderException extends RuntimeException {
    public NoSuitablePackageDecoderException() {
        super();
    }

    public NoSuitablePackageDecoderException(final String description) {
        super(description);
    }

    public NoSuitablePackageDecoderException(final Exception cause) {
        super(cause);
    }

    public NoSuitablePackageDecoderException(final String description, final Exception cause) {
        super(description, cause);
    }
}
