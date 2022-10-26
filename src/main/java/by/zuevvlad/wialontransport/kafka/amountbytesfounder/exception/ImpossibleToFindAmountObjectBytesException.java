package by.zuevvlad.wialontransport.kafka.amountbytesfounder.exception;

public final class ImpossibleToFindAmountObjectBytesException extends RuntimeException {
    public ImpossibleToFindAmountObjectBytesException() {
        super();
    }

    public ImpossibleToFindAmountObjectBytesException(final String description) {
        super(description);
    }

    public ImpossibleToFindAmountObjectBytesException(final Exception cause) {
        super(cause);
    }

    public ImpossibleToFindAmountObjectBytesException(final String description, final Exception cause) {
        super(description, cause);
    }
}
