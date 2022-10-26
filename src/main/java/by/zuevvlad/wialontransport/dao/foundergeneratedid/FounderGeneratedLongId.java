package by.zuevvlad.wialontransport.dao.foundergeneratedid;

import by.zuevvlad.wialontransport.dao.foundergeneratedid.exception.FindingGeneratedIdByDataBaseException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;
import static java.util.Optional.empty;

public final class FounderGeneratedLongId implements FounderGeneratedId<Long> {

    public static FounderGeneratedId<Long> create() {
        return SingletonHolder.FOUNDER_GENERATED_LONG_ID;
    }

    private FounderGeneratedLongId() {

    }

    @Override
    public Optional<Long> findLastGeneratedId(final Statement statement, final String nameColumnId) {
        try (final ResultSet generatedKeysResultSet = statement.getGeneratedKeys()) {
            return generatedKeysResultSet.next() ? of(generatedKeysResultSet.getLong(nameColumnId)) : empty();
        } catch (final SQLException cause) {
            throw new FindingGeneratedIdByDataBaseException(cause);
        }
    }

    @Override
    public List<Long> findAllGeneratedIds(final Statement statement, final String nameColumnId) {
        try (final ResultSet generatedKeysResultSet = statement.getGeneratedKeys()) {
            final List<Long> generatedIds = new ArrayList<>();
            long generatedId;
            while (generatedKeysResultSet.next()) {
                generatedId = generatedKeysResultSet.getLong(nameColumnId);
                generatedIds.add(generatedId);
            }
            return generatedIds;
        } catch (final SQLException cause) {
            throw new FindingGeneratedIdByDataBaseException(cause);
        }
    }

    private static final class SingletonHolder {
        private static final FounderGeneratedId<Long> FOUNDER_GENERATED_LONG_ID = new FounderGeneratedLongId();
    }
}
