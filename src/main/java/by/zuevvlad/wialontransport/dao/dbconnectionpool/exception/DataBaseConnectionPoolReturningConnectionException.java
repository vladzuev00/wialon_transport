package by.zuevvlad.wialontransport.dao.dbconnectionpool.exception;

public final class DataBaseConnectionPoolReturningConnectionException extends DataBaseConnectionPoolException {
    public DataBaseConnectionPoolReturningConnectionException() {
        super();
    }

    public DataBaseConnectionPoolReturningConnectionException(final String description) {
        super(description);
    }

    public DataBaseConnectionPoolReturningConnectionException(final Exception cause) {
        super(cause);
    }

    public DataBaseConnectionPoolReturningConnectionException(final String description, final Exception cause) {
        super(description, cause);
    }
}
