package by.zuevvlad.wialontransport.dao.exception;

import by.zuevvlad.wialontransport.dao.exception.RepositoryException;

public final class InsertingEntitiesException extends RepositoryException {
    public InsertingEntitiesException() {
        super();
    }

    public InsertingEntitiesException(final String description) {
        super(description);
    }

    public InsertingEntitiesException(final Exception cause) {
        super(cause);
    }

    public InsertingEntitiesException(final String description, final Exception cause) {
        super(description, cause);
    }
}
