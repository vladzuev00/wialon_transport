package by.zuevvlad.wialontransport.dao.resultsetmapper;

import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;

public final class ExtendedDataResultSetMapper extends ResultSetMapper<ExtendedDataEntity> {

    public static ResultSetMapper<ExtendedDataEntity> create() {
        return SingletonHolder.EXTENDED_DATA_RESULT_SET_MAPPER;
    }

    private ExtendedDataResultSetMapper(final ResultRowMapper<ExtendedDataEntity> extendedDataResultRowMapper) {
        super(extendedDataResultRowMapper);
    }

    private static final class SingletonHolder {
        private static final ResultSetMapper<ExtendedDataEntity> EXTENDED_DATA_RESULT_SET_MAPPER
                = new ExtendedDataResultSetMapper(null);
    }
}
