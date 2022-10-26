package by.zuevvlad.wialontransport.dao.foundergeneratedid;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public interface FounderGeneratedId<IdType> {
    Optional<IdType> findLastGeneratedId(final Statement statement, final String nameColumnId);
    List<IdType> findAllGeneratedIds(final Statement statement, final String nameColumnId);
}
