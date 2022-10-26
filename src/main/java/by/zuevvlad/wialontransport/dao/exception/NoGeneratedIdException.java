package by.zuevvlad.wialontransport.dao.exception;

public final class NoGeneratedIdException extends RepositoryException {
    public NoGeneratedIdException() {
        super();
    }

    public NoGeneratedIdException(final String description) {
        super(description);
    }

    public NoGeneratedIdException(final Exception cause) {
        super(cause);
    }

    public NoGeneratedIdException(final String description, final Exception cause) {
        super(description, cause);
    }
}
