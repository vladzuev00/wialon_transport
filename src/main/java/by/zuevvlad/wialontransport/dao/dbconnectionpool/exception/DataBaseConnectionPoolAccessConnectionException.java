package by.zuevvlad.wialontransport.dao.dbconnectionpool.exception;

public final class DataBaseConnectionPoolAccessConnectionException extends DataBaseConnectionPoolException {
    public DataBaseConnectionPoolAccessConnectionException() {
        super();
    }

    public DataBaseConnectionPoolAccessConnectionException(final String description) {
        super(description);
    }

    public DataBaseConnectionPoolAccessConnectionException(final Exception cause) {
        super(cause);
    }

    public DataBaseConnectionPoolAccessConnectionException(final String description, final Exception cause) {
        super(description, cause);
    }
}
