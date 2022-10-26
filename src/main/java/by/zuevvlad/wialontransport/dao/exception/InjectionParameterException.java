package by.zuevvlad.wialontransport.dao.exception;

public final class InjectionParameterException extends RepositoryException {
    public InjectionParameterException() {
        super();
    }

    public InjectionParameterException(final String description) {
        super(description);
    }

    public InjectionParameterException(final Exception cause) {
        super(cause);
    }

    public InjectionParameterException(final String description, final Exception cause) {
        super(description, cause);
    }
}
