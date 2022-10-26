package by.zuevvlad.wialontransport.dao.dbconnectionpool.exception;

import static java.lang.Integer.MIN_VALUE;

public final class DataBaseConnectionPoolFullingException extends DataBaseConnectionPoolException {
    private final int lastFulledConnectionIndex;

    public DataBaseConnectionPoolFullingException(final int lastFulledConnectionIndex) {
        this.lastFulledConnectionIndex = lastFulledConnectionIndex;
    }

    public DataBaseConnectionPoolFullingException(final Exception cause) {
        super(cause);
        this.lastFulledConnectionIndex = MIN_VALUE;
    }

    public DataBaseConnectionPoolFullingException(final int lastFulledConnectionIndex, final String description) {
        super(description);
        this.lastFulledConnectionIndex = lastFulledConnectionIndex;
    }

    public DataBaseConnectionPoolFullingException(final int lastFulledConnectionIndex, final Exception cause) {
        super(cause);
        this.lastFulledConnectionIndex = lastFulledConnectionIndex;
    }

    public DataBaseConnectionPoolFullingException(final int lastFulledConnectionIndex, final String description,
                                                  final Exception cause) {
        super(description, cause);
        this.lastFulledConnectionIndex = lastFulledConnectionIndex;
    }

    public int getLastFulledConnectionIndex() {
        return this.lastFulledConnectionIndex;
    }
}
