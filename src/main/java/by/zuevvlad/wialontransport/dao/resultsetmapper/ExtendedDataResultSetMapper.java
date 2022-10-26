package by.zuevvlad.wialontransport.dao.resultsetmapper;

import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ExtendedDataResultRowMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.entity.ExtendedData;

public final class ExtendedDataResultSetMapper extends ResultSetMapper<ExtendedData> {

    public static ResultSetMapper<ExtendedData> create() {
        return SingletonHolder.EXTENDED_DATA_RESULT_SET_MAPPER;
    }

    private ExtendedDataResultSetMapper(final ResultRowMapper<ExtendedData> extendedDataResultRowMapper) {
        super(extendedDataResultRowMapper);
    }

    private static final class SingletonHolder {
        private static final ResultSetMapper<ExtendedData> EXTENDED_DATA_RESULT_SET_MAPPER
                = new ExtendedDataResultSetMapper(ExtendedDataResultRowMapper.create());
    }
}
