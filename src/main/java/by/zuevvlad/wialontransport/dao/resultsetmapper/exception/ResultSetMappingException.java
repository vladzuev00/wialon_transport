package by.zuevvlad.wialontransport.dao.resultsetmapper.exception;

public final class ResultSetMappingException extends RuntimeException {
    public ResultSetMappingException() {
        super();
    }

    public ResultSetMappingException(final String description) {
        super(description);
    }

    public ResultSetMappingException(final Exception cause) {
        super(cause);
    }

    public ResultSetMappingException(final String description, final Exception cause) {
        super(description, cause);
    }
}
