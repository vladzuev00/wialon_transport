package by.zuevvlad.wialontransport.dao.dbconnectionpool.exception;

public final class DataBaseConnectionPoolFullException extends DataBaseConnectionPoolException {
    public DataBaseConnectionPoolFullException() {
        super();
    }

    public DataBaseConnectionPoolFullException(final String description) {
        super(description);
    }

    public DataBaseConnectionPoolFullException(final Exception cause) {
        super(cause);
    }

    public DataBaseConnectionPoolFullException(final String description, final Exception cause) {
        super(description, cause);
    }
}
