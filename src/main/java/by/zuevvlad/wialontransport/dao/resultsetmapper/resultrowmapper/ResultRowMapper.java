package by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper;

import java.sql.ResultSet;

@FunctionalInterface
public interface ResultRowMapper<ResultType> {
    ResultType map(final ResultSet resultSet);
}
