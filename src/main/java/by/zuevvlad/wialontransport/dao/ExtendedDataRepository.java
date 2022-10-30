package by.zuevvlad.wialontransport.dao;

import by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPool;
import by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPoolImplementation;
import by.zuevvlad.wialontransport.dao.foundergeneratedid.FounderGeneratedId;
import by.zuevvlad.wialontransport.dao.foundergeneratedid.FounderGeneratedLongId;
import by.zuevvlad.wialontransport.dao.parameterinjector.entity.EntityInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.parameterinjector.entity.ExtendedDataInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.resultsetmapper.ExtendedDataResultSetMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.ResultSetMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.dao.serializer.AnalogInputsSerializer;
import by.zuevvlad.wialontransport.dao.serializer.ParametersSerializer;
import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;

import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

public final class ExtendedDataRepository extends AbstractEntityRepository<ExtendedDataEntity> {
    private static final String QUERY_TO_SELECT_BY_ID = "SELECT data.id, data.date, data.time, "
            + "data.latitude_degrees, data.latitude_minutes, data.latitude_minute_share, data.latitude_type, "
            + "data.longitude_degrees, data.longitude_minutes, data.longitude_minute_share, data.longitude_type, "
            + "data.speed, data.course, data.height, data.amount_of_satellites, extended_data.reduction_precision, "
            + "extended_data.inputs, extended_data.outputs, extended_data.analog_inputs, "
            + "extended_data.driver_key_code, extended_data.parameters "
            + "FROM data INNER JOIN extended_data ON data.id = extended_data.id "
            + "WHERE data.id = ?";

    private static final String QUERY_TO_SELECT_ALL = "SELECT data.id, data.date, data.time, "
            + "data.latitude_degrees, data.latitude_minutes, data.latitude_minute_share, data.latitude_type, "
            + "data.longitude_degrees, data.longitude_minutes, data.longitude_minute_share, data.longitude_type, "
            + "data.speed, data.course, data.height, data.amount_of_satellites, extended_data.reduction_precision, "
            + "extended_data.inputs, extended_data.outputs, extended_data.analog_inputs, "
            + "extended_data.driver_key_code, extended_data.parameters "
            + "FROM data INNER JOIN extended_data ON data.id = extended_data.id";

    private static final String QUERY_TO_INSERT = "WITH new_data AS ("
            + "INSERT INTO data(date, time, "
            + "data.latitude_degrees, data.latitude_minutes, data.latitude_minute_share, data.latitude_type, "
            + "data.longitude_degrees, data.longitude_minutes, data.longitude_minute_share, data.longitude_type, "
            + "data.speed, data.course, data.height, data.amount_satellites, data.tracker_id) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
            + "RETURNING id"
            + ")"
            + "INSERT INTO extended_data(id, reduction_precision, inputs, outputs, analog_inputs, driver_key_code, parameters) "
            + "VALUES((SELECT id FROM new_data), ?, ?, ?, ?, ?, ?)";
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
    private static final int PARAMETER_INDEX_AMOUNT_OF_SATELLITES_IN_QUERY_TO_INSERT = 14;
    private static final int PARAMETER_INDEX_TRACKER_ID_IN_QUERY_TO_INSERT = 15;
    private static final int PARAMETER_INDEX_REDUCTION_PRECISION_IN_QUERY_TO_INSERT = 16;
    private static final int PARAMETER_INDEX_INPUTS_IN_QUERY_TO_INSERT = 17;
    private static final int PARAMETER_INDEX_OUTPUTS_IN_QUERY_TO_INSERT = 18;
    private static final int PARAMETER_INDEX_ANALOG_INPUTS_IN_QUERY_TO_INSERT = 19;
    private static final int PARAMETER_INDEX_DRIVER_KEY_CODE_IN_QUERY_TO_INSERT = 20;
    private static final int PARAMETER_INDEX_PARAMETERS_IN_QUERY_TO_INSERT = 21;

    private static final String QUERY_TO_UPDATE = "WITH updated_data AS ("
            + "UPDATE data SET date = ?, time = ?, "
            + "latitude_degrees = ?, latitude_minutes = ?, latitude_minute_share = ?, latitude_type = ?, "
            + "longitude_degrees = ?, longitude_minutes = ?, longitude_minute_share = ?, longitude_type = ?, "
            + "speed = ?, course = ?, height = ?, amount_satellites = ?, tracker_id = ? "
            + "WHERE data.id = ? "
            + "RETURNING id"
            + ") "
            + "UPDATE extended_data SET "
            + "reduction_precision = ?, inputs = ?, outputs = ?, analog_inputs = ?, driver_key_code = ?, parameters = ? "
            + "WHERE extended_data.id = (SELECT id FROM updated_data);";
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
    private static final int PARAMETER_INDEX_REDUCTION_PRECISION_IN_QUERY_TO_UPDATE = 17;
    private static final int PARAMETER_INDEX_INPUTS_IN_QUERY_TO_UPDATE = 18;
    private static final int PARAMETER_INDEX_OUTPUTS_IN_QUERY_TO_UPDATE = 19;
    private static final int PARAMETER_INDEX_ANALOG_INPUTS_IN_QUERY_TO_UPDATE = 20;
    private static final int PARAMETER_INDEX_DRIVER_KEY_CODE_IN_QUERY_TO_UPDATE = 21;
    private static final int PARAMETER_INDEX_PARAMETERS_IN_QUERY_TO_UPDATE = 22;

    private static final String QUERY_TO_DELETE = "DELETE FROM data WHERE data.id = ?";

    private ExtendedDataRepository(final DataBaseConnectionPool dataBaseConnectionPool,
                                   final ResultRowMapper<ExtendedDataEntity> extendedDataResultRowMapper,
                                   final ResultSetMapper<ExtendedDataEntity> extendedDataResultSetMapper,
                                   final FounderGeneratedId<Long> founderGeneratedId,
                                   final EntityInjectorInPreparedStatement<ExtendedDataEntity> extendedDataInjectorToInsert,
                                   final EntityInjectorInPreparedStatement<ExtendedDataEntity> extendedDataInjectorToUpdate,
                                   final Logger logger) {
        super(dataBaseConnectionPool, extendedDataResultRowMapper, extendedDataResultSetMapper, founderGeneratedId,
                extendedDataInjectorToInsert, extendedDataInjectorToUpdate, logger);
    }

    public static EntityRepository<ExtendedDataEntity> create() {
        return SingletonHolder.EXTENDED_DATA_REPOSITORY;
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
        return QUERY_TO_DELETE;
    }

    private static final class SingletonHolder {
        private static final EntityInjectorInPreparedStatement<ExtendedDataEntity> EXTENDED_DATA_INJECTOR_TO_INSERT
                = new ExtendedDataInjectorInPreparedStatement(
                AnalogInputsSerializer.create(),
                ParametersSerializer.create(),
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
                PARAMETER_INDEX_AMOUNT_OF_SATELLITES_IN_QUERY_TO_INSERT,
                PARAMETER_INDEX_TRACKER_ID_IN_QUERY_TO_INSERT,
                PARAMETER_INDEX_REDUCTION_PRECISION_IN_QUERY_TO_INSERT,
                PARAMETER_INDEX_INPUTS_IN_QUERY_TO_INSERT,
                PARAMETER_INDEX_OUTPUTS_IN_QUERY_TO_INSERT,
                PARAMETER_INDEX_ANALOG_INPUTS_IN_QUERY_TO_INSERT,
                PARAMETER_INDEX_DRIVER_KEY_CODE_IN_QUERY_TO_INSERT,
                PARAMETER_INDEX_PARAMETERS_IN_QUERY_TO_INSERT
        );

        private static final EntityInjectorInPreparedStatement<ExtendedDataEntity> EXTENDED_DATA_INJECTOR_TO_UPDATE
                = new ExtendedDataInjectorInPreparedStatement(
                AnalogInputsSerializer.create(),
                ParametersSerializer.create(),
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
                PARAMETER_INDEX_TRACKER_ID_IN_QUERY_TO_UPDATE,
                PARAMETER_INDEX_REDUCTION_PRECISION_IN_QUERY_TO_UPDATE,
                PARAMETER_INDEX_INPUTS_IN_QUERY_TO_UPDATE,
                PARAMETER_INDEX_OUTPUTS_IN_QUERY_TO_UPDATE,
                PARAMETER_INDEX_ANALOG_INPUTS_IN_QUERY_TO_UPDATE,
                PARAMETER_INDEX_DRIVER_KEY_CODE_IN_QUERY_TO_UPDATE,
                PARAMETER_INDEX_PARAMETERS_IN_QUERY_TO_UPDATE
        );

        private static final EntityRepository<ExtendedDataEntity> EXTENDED_DATA_REPOSITORY = new ExtendedDataRepository(
                DataBaseConnectionPoolImplementation.create(),
                null,
                //ExtendedDataResultRowMapper.create(),
                ExtendedDataResultSetMapper.create(),
                FounderGeneratedLongId.create(),
                EXTENDED_DATA_INJECTOR_TO_INSERT,
                EXTENDED_DATA_INJECTOR_TO_UPDATE,
                getLogger(ExtendedDataRepository.class.getName())
        );
    }
}
