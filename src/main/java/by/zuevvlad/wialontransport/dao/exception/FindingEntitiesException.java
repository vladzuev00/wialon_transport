package by.zuevvlad.wialontransport.dao.exception;

public final class FindingEntitiesException extends RepositoryException {
    public FindingEntitiesException() {
        super();
    }

    public FindingEntitiesException(final String description) {
        super(description);
    }

    public FindingEntitiesException(final Exception cause) {
        super(cause);
    }

    public FindingEntitiesException(final String description, final Exception cause) {
        super(description, cause);
    }
}
