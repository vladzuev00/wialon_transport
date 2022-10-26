package by.zuevvlad.wialontransport.dao.dbconnectionpool.exception;

public final class DataBaseConnectionPoolCreatingException extends DataBaseConnectionPoolException {
    public DataBaseConnectionPoolCreatingException() {
        super();
    }

    public DataBaseConnectionPoolCreatingException(final String description) {
        super(description);
    }

    public DataBaseConnectionPoolCreatingException(final Exception cause) {
        super(cause);
    }

    public DataBaseConnectionPoolCreatingException(final String description, final Exception cause) {
        super(description, cause);
    }
}
