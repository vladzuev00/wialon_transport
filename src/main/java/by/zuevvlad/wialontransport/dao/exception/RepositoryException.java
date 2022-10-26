package by.zuevvlad.wialontransport.dao.exception;

public class RepositoryException extends RuntimeException {
    public RepositoryException() {
        super();
    }

    public RepositoryException(final String description) {
        super(description);
    }

    public RepositoryException(final Exception cause) {
        super(cause);
    }

    public RepositoryException(final String description, final Exception cause) {
        super(description, cause);
    }
}
