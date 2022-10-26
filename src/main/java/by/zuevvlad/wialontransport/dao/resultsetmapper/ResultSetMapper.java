package by.zuevvlad.wialontransport.dao.resultsetmapper;

import by.zuevvlad.wialontransport.dao.resultsetmapper.exception.ResultSetMappingException;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class ResultSetMapper<ComponentResultType> {
    private final ResultRowMapper<ComponentResultType> resultRowMapper;

    public ResultSetMapper(final ResultRowMapper<ComponentResultType> resultRowMapper) {
        this.resultRowMapper = resultRowMapper;
    }

    public final List<ComponentResultType> map(final ResultSet resultSet) {
        try {
            final List<ComponentResultType> result = new ArrayList<>();
            ComponentResultType currentMappedComponent;
            while (resultSet.next()) {
                currentMappedComponent = this.resultRowMapper.map(resultSet);
                result.add(currentMappedComponent);
            }
            return result;
        } catch (final SQLException cause) {
            throw new ResultSetMappingException(cause);
        }
    }
}
