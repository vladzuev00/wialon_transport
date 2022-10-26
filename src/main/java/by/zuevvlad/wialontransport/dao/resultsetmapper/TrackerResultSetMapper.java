package by.zuevvlad.wialontransport.dao.resultsetmapper;

import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.TrackerResultRowMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.entity.Tracker;

public final class TrackerResultSetMapper extends ResultSetMapper<Tracker> {
    public static ResultSetMapper<Tracker> create() {
        return SingletonHolder.TRACKER_RESULT_SET_MAPPER;
    }

    private TrackerResultSetMapper(final ResultRowMapper<Tracker> trackerResultRowMapper) {
        super(trackerResultRowMapper);
    }

    private static final class SingletonHolder {
        private static final ResultSetMapper<Tracker> TRACKER_RESULT_SET_MAPPER = new TrackerResultSetMapper(
                TrackerResultRowMapper.create());
    }
}
