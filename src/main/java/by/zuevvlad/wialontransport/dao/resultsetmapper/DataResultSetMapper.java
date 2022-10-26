package by.zuevvlad.wialontransport.dao.resultsetmapper;

import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.DataResultRowMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.entity.Data;

public final class DataResultSetMapper extends ResultSetMapper<Data> {
    public static ResultSetMapper<Data> create() {
        return SingletonHolder.DATA_RESULT_SET_MAPPER;
    }

    private DataResultSetMapper(final ResultRowMapper<Data> dataResultRowMapper) {
        super(dataResultRowMapper);
    }

    private static final class SingletonHolder {
        public static final ResultSetMapper<Data> DATA_RESULT_SET_MAPPER
                = new DataResultSetMapper(DataResultRowMapper.create());
    }
}
