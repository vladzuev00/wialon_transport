package by.zuevvlad.wialontransport.dao.dbconnectionpool.exception;

public final class DataBaseConnectionPoolClosingException extends DataBaseConnectionPoolException {
    public DataBaseConnectionPoolClosingException() {
        super();
    }

    public DataBaseConnectionPoolClosingException(final String description) {
        super(description);
    }

    public DataBaseConnectionPoolClosingException(final Exception cause) {
        super(cause);
    }

    public DataBaseConnectionPoolClosingException(final String description, final Exception cause) {
        super(description, cause);
    }
}
