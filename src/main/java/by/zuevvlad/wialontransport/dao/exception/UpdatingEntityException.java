package by.zuevvlad.wialontransport.dao.exception;

public final class UpdatingEntityException extends RepositoryException {
    public UpdatingEntityException() {
        super();
    }

    public UpdatingEntityException(final String description) {
        super(description);
    }

    public UpdatingEntityException(final Exception cause) {
        super(cause);
    }

    public UpdatingEntityException(final String description, final Exception cause) {
        super(description, cause);
    }
}
