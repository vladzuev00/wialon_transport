package by.zuevvlad.wialontransport.dao.resultsetmapper;

import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.TrackerResultRowMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.entity.TrackerEntity;

public final class TrackerResultSetMapper extends ResultSetMapper<TrackerEntity> {
    public static ResultSetMapper<TrackerEntity> create() {
        return SingletonHolder.TRACKER_RESULT_SET_MAPPER;
    }

    private TrackerResultSetMapper(final ResultRowMapper<TrackerEntity> trackerResultRowMapper) {
        super(trackerResultRowMapper);
    }

    private static final class SingletonHolder {
        private static final ResultSetMapper<TrackerEntity> TRACKER_RESULT_SET_MAPPER = new TrackerResultSetMapper(
                TrackerResultRowMapper.create());
    }
}
