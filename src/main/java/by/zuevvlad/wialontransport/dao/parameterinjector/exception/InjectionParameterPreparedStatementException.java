package by.zuevvlad.wialontransport.dao.parameterinjector.exception;

public final class InjectionParameterPreparedStatementException extends RuntimeException {
    public InjectionParameterPreparedStatementException() {

    }

    public InjectionParameterPreparedStatementException(final String description) {
        super(description);
    }

    public InjectionParameterPreparedStatementException(final Exception cause) {
        super(cause);
    }

    public InjectionParameterPreparedStatementException(final String description, final Exception cause) {
        super(description, cause);
    }
}
