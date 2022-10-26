package by.zuevvlad.wialontransport.dao.dbconnectionpool.exception;


public class DataBaseConnectionPoolException extends RuntimeException {
    public DataBaseConnectionPoolException() {
        super();
    }

    public DataBaseConnectionPoolException(final String description) {
        super(description);
    }

    public DataBaseConnectionPoolException(final Exception cause) {
        super(cause);
    }

    public DataBaseConnectionPoolException(final String description, final Exception cause) {
        super(description, cause);
    }
}
