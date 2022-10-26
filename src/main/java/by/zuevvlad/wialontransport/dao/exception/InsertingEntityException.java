package by.zuevvlad.wialontransport.dao.exception;

public final class InsertingEntityException extends RepositoryException {
    public InsertingEntityException() {
        super();
    }

    public InsertingEntityException(final String description) {
        super(description);
    }

    public InsertingEntityException(final Exception cause) {
        super(cause);
    }

    public InsertingEntityException(final String description, final Exception cause) {
        super(description, cause);
    }
}
