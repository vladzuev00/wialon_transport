package by.zuevvlad.wialontransport.dao.exception;

public final class NoAvailableConnectionInPoolException extends RepositoryException {
    public NoAvailableConnectionInPoolException() {
        super();
    }

    public NoAvailableConnectionInPoolException(final String description) {
        super(description);
    }

    public NoAvailableConnectionInPoolException(final Exception cause) {
        super(cause);
    }

    public NoAvailableConnectionInPoolException(final String description, final Exception cause) {
        super(description, cause);
    }
}
