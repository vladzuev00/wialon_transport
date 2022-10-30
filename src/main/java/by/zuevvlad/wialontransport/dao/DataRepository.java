package by.zuevvlad.wialontransport.dao;

import by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPool;
import by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPoolImplementation;
import by.zuevvlad.wialontransport.dao.foundergeneratedid.FounderGeneratedId;
import by.zuevvlad.wialontransport.dao.foundergeneratedid.FounderGeneratedLongId;
import by.zuevvlad.wialontransport.dao.parameterinjector.entity.DataInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.parameterinjector.entity.EntityInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.resultsetmapper.DataResultSetMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.ResultSetMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.DataResultRowMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.entity.DataEntity;

import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

public final class DataRepository extends AbstractEntityRepository<DataEntity> {
    private static final String QUERY_TO_SELECT_BY_ID = "SELECT data.id, data.date, data.time, "
            + "data.latitude_degrees, data.latitude_minutes, data.latitude_minute_share, data.latitude_type, "
            + "data.longitude_degrees, data.longitude_minutes, data.longitude_minute_share, data.longitude_type, "
            + "data.speed, data.course, data.height, data.amount_satellites, data.tracker_id FROM data "
            + "WHERE data.id = ?";

    private static final String QUERY_TO_SELECT_ALL = "SELECT data.id, data.date, data.time, "
            + "data.latitude_degrees, data.latitude_minutes, data.latitude_minute_share, data.latitude_type, "
            + "data.longitude_degrees, data.longitude_minutes, data.longitude_minute_share, data.longitude_type, "
            + "data.speed, data.course, data.height, data.amount_satellites, data.tracker_id FROM data";

    private static final String QUERY_TO_INSERT = "INSERT INTO data(date, time, "
            + "latitude_degrees, latitude_minutes, latitude_minute_share, latitude_type, "
            + "longitude_degrees, longitude_minutes, longitude_minute_share, longitude_type, "
            + "speed, course, height, amount_satellites, tracker_id) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final int PARAMETER_INDEX_DATE_IN_QUERY_TO_INSERT = 1;
    private static final int PARAMETER_INDEX_TIME_IN_QUERY_TO_INSERT = 2;
    private static final int PARAMETER_INDEX_LATITUDE_DEGREES_IN_QUERY_TO_INSERT = 3;
    private static final int PARAMETER_INDEX_LATITUDE_MINUTES_IN_QUERY_TO_INSERT = 4;
    private static final int PARAMETER_INDEX_LATITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT = 5;
    private static final int PARAMETER_INDEX_LATITUDE_TYPE_IN_QUERY_TO_INSERT = 6;
    private static final int PARAMETER_INDEX_LONGITUDE_DEGREES_IN_QUERY_TO_INSERT = 7;
    private static final int PARAMETER_INDEX_LONGITUDE_MINUTES_IN_QUERY_TO_INSERT = 8;
    private static final int PARAMETER_INDEX_LONGITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT = 9;
    private static final int PARAMETER_INDEX_LONGITUDE_TYPE_IN_QUERY_TO_INSERT = 10;
    private static final int PARAMETER_INDEX_SPEED_IN_QUERY_TO_INSERT = 11;
    private static final int PARAMETER_INDEX_COURSE_IN_QUERY_TO_INSERT = 12;
    private static final int PARAMETER_INDEX_HEIGHT_IN_QUERY_TO_INSERT = 13;
    private static final int PARAMETER_INDEX_AMOUNT_SATELLITES_IN_QUERY_TO_INSERT = 14;
    private static final int PARAMETER_INDEX_TRACKER_ID_IN_QUERY_TO_INSERT = 15;

    private static final String QUERY_TO_UPDATE = "UPDATE data SET date = ?, time = ?, "
            + "latitude_degrees = ?, latitude_minutes = ?, latitude_minute_share = ?, latitude_type = ?, "
            + "longitude_degrees = ?, longitude_minutes = ?, longitude_minute_share = ?, longitude_type = ?, "
            + "speed = ?, course = ?, height = ?, amount_satellites = ?, tracker_id = ? "
            + "WHERE data.id = ?";
    private static final int PARAMETER_INDEX_DATE_IN_QUERY_TO_UPDATE = 1;
    private static final int PARAMETER_INDEX_TIME_IN_QUERY_TO_UPDATE = 2;
    private static final int PARAMETER_INDEX_LATITUDE_DEGREES_IN_QUERY_TO_UPDATE = 3;
    private static final int PARAMETER_INDEX_LATITUDE_MINUTES_IN_QUERY_TO_UPDATE = 4;
    private static final int PARAMETER_INDEX_LATITUDE_MINUTE_SHARE_IN_QUERY_TO_UPDATE = 5;
    private static final int PARAMETER_INDEX_LATITUDE_TYPE_IN_QUERY_TO_UPDATE = 6;
    private static final int PARAMETER_INDEX_LONGITUDE_DEGREES_IN_QUERY_TO_UPDATE = 7;
    private static final int PARAMETER_INDEX_LONGITUDE_MINUTES_IN_QUERY_TO_UPDATE = 8;
    private static final int PARAMETER_INDEX_LONGITUDE_MINUTE_SHARE_IN_QUERY_TO_UPDATE = 9;
    private static final int PARAMETER_INDEX_LONGITUDE_TYPE_IN_QUERY_TO_UPDATE = 10;
    private static final int PARAMETER_INDEX_SPEED_IN_QUERY_TO_UPDATE = 11;
    private static final int PARAMETER_INDEX_COURSE_IN_QUERY_TO_UPDATE = 12;
    private static final int PARAMETER_INDEX_HEIGHT_IN_QUERY_TO_UPDATE = 13;
    private static final int PARAMETER_INDEX_AMOUNT_SATELLITES_IN_QUERY_TO_UPDATE = 14;
    private static final int PARAMETER_INDEX_TRACKER_ID_IN_QUERY_TO_UPDATE = 15;
    private static final int PARAMETER_INDEX_ID_IN_QUERY_TO_UPDATE = 16;

    private static final String QUERY_TO_DELETE_BY_ID = "DELETE FROM data WHERE data.id = ?";

    private DataRepository(final DataBaseConnectionPool dataBaseConnectionPool,
                           final ResultRowMapper<DataEntity> dataResultRowMapper,
                           final ResultSetMapper<DataEntity> dataResultSetMapper,
                           final FounderGeneratedId<Long> founderGeneratedId,
                           final EntityInjectorInPreparedStatement<DataEntity> dataInjectorToInsert,
                           final EntityInjectorInPreparedStatement<DataEntity> dataInjectorToUpdate,
                           final Logger logger) {
        super(dataBaseConnectionPool, dataResultRowMapper, dataResultSetMapper, founderGeneratedId,
                dataInjectorToInsert, dataInjectorToUpdate, logger);
    }

    public static EntityRepository<DataEntity> create() {
        return SingletonHolder.DATA_REPOSITORY;
    }

    @Override
    protected String findQueryToSelectById() {
        return QUERY_TO_SELECT_BY_ID;
    }

    @Override
    protected String findQueryToSelectAll() {
        return QUERY_TO_SELECT_ALL;
    }

    @Override
    protected String findQueryToInsertEntity() {
        return QUERY_TO_INSERT;
    }

    @Override
    protected String findQueryToUpdateEntity() {
        return QUERY_TO_UPDATE;
    }

    @Override
    protected String findQueryToDeleteEntityById() {
        return QUERY_TO_DELETE_BY_ID;
    }

    private static final class SingletonHolder {
        private static final EntityRepository<DataEntity> DATA_REPOSITORY = new DataRepository(
                DataBaseConnectionPoolImplementation.create(), DataResultRowMapper.create(),
                DataResultSetMapper.create(), FounderGeneratedLongId.create(),
                new DataInjectorInPreparedStatement(
                        PARAMETER_INDEX_DATE_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_TIME_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_LATITUDE_DEGREES_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_LATITUDE_MINUTES_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_LATITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_LATITUDE_TYPE_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_LONGITUDE_DEGREES_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_LONGITUDE_MINUTES_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_LONGITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_LONGITUDE_TYPE_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_SPEED_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_COURSE_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_HEIGHT_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_AMOUNT_SATELLITES_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_TRACKER_ID_IN_QUERY_TO_INSERT
                ),
                new DataInjectorInPreparedStatement(
                        PARAMETER_INDEX_ID_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_DATE_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_TIME_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_LATITUDE_DEGREES_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_LATITUDE_MINUTES_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_LATITUDE_MINUTE_SHARE_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_LATITUDE_TYPE_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_LONGITUDE_DEGREES_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_LONGITUDE_MINUTES_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_LONGITUDE_MINUTE_SHARE_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_LONGITUDE_TYPE_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_SPEED_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_COURSE_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_HEIGHT_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_AMOUNT_SATELLITES_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_TRACKER_ID_IN_QUERY_TO_UPDATE
                ),
                getLogger(DataRepository.class.getName()));
    }
}
