package by.zuevvlad.wialontransport.dao.dbconnectionpool.exception;

public final class DataBaseConnectionPoolFindingAmountAvailableConnectionsException
        extends DataBaseConnectionPoolException {
    public DataBaseConnectionPoolFindingAmountAvailableConnectionsException() {
        super();
    }

    public DataBaseConnectionPoolFindingAmountAvailableConnectionsException(final String description) {
        super(description);
    }

    public DataBaseConnectionPoolFindingAmountAvailableConnectionsException(final Exception cause) {
        super(cause);
    }

    public DataBaseConnectionPoolFindingAmountAvailableConnectionsException(final String description,
                                                                            final Exception cause) {
        super(description, cause);
    }
}
