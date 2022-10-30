package by.zuevvlad.wialontransport.dao.resultsetmapper;

import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.DataResultRowMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.entity.DataEntity;

public final class DataResultSetMapper extends ResultSetMapper<DataEntity> {
    public static ResultSetMapper<DataEntity> create() {
        return SingletonHolder.DATA_RESULT_SET_MAPPER;
    }

    private DataResultSetMapper(final ResultRowMapper<DataEntity> dataResultRowMapper) {
        super(dataResultRowMapper);
    }

    private static final class SingletonHolder {
        public static final ResultSetMapper<DataEntity> DATA_RESULT_SET_MAPPER
                = new DataResultSetMapper(DataResultRowMapper.create());
    }
}
