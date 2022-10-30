package by.zuevvlad.wialontransport.dao;

import by.zuevvlad.wialontransport.dao.cryptographer.StringToStringCryptographer;
import by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPool;
import by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPoolImplementation;
import by.zuevvlad.wialontransport.dao.exception.FindingEntityException;
import by.zuevvlad.wialontransport.dao.exception.NoAvailableConnectionInPoolException;
import by.zuevvlad.wialontransport.dao.foundergeneratedid.FounderGeneratedId;
import by.zuevvlad.wialontransport.dao.foundergeneratedid.FounderGeneratedLongId;
import by.zuevvlad.wialontransport.dao.parameterinjector.entity.EntityInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.parameterinjector.entity.TrackerInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.resultsetmapper.ResultSetMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.TrackerResultSetMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.TrackerResultRowMapper;
import by.zuevvlad.wialontransport.entity.TrackerEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class TrackerRepository extends AbstractEntityRepository<TrackerEntity> {
    private static final String QUERY_TO_SELECT_BY_ID = "SELECT trackers.id, trackers.imei, "
            + "trackers.encrypted_password, trackers.phone_number, trackers.user_id FROM trackers "
            + "WHERE trackers.id = ?";

    private static final String QUERY_TO_SELECT_ALL = "SELECT trackers.id, trackers.imei, trackers.encrypted_password, "
            + "trackers.phone_number, trackers.user_id FROM trackers";

    private static final String QUERY_TO_INSERT = "INSERT INTO trackers(imei, encrypted_password, phone_number, user_id) "
            + "VALUES(?, ?, ?, ?)";
    private static final int PARAMETER_INDEX_IMEI_IN_QUERY_TO_INSERT = 1;
    private static final int PARAMETER_INDEX_ENCRYPTED_PASSWORD_IN_QUERY_TO_INSERT = 2;
    private static final int PARAMETER_INDEX_PHONE_NUMBER_IN_QUERY_TO_INSERT = 3;
    private static final int PARAMETER_INDEX_USER_ID_IN_QUERY_TO_INSERT = 4;

    private static final String QUERY_TO_UPDATE = "UPDATE trackers SET imei = ?, encrypted_password = ?, "
            + "phone_number = ?, user_id = ? WHERE trackers.id = ?";
    private static final int PARAMETER_INDEX_IMEI_IN_QUERY_TO_UPDATE = 1;
    private static final int PARAMETER_INDEX_ENCRYPTED_PASSWORD_IN_QUERY_TO_UPDATE = 2;
    private static final int PARAMETER_INDEX_PHONE_NUMBER_IN_QUERY_TO_UPDATE = 3;
    private static final int PARAMETER_INDEX_USER_ID_IN_QUERY_TO_UPDATE = 4;
    private static final int PARAMETER_INDEX_ID_IN_QUERY_TO_UPDATE = 5;

    private static final String QUERY_TO_DELETE = "DELETE FROM trackers WHERE trackers.id = ?";

    private static final String QUERY_TO_SELECT_BY_IMEI = "SELECT trackers.id, trackers.imei, "
            + "trackers.encrypted_password, trackers.phone_number, trackers.user_id "
            + "FROM trackers "
            + "WHERE trackers.imei = ?";
    private static final int PARAMETER_INDEX_IMEI_IN_QUERY_TO_SELECT_BY_IMEI = 1;

    private TrackerRepository(final DataBaseConnectionPool dataBaseConnectionPool,
                              final ResultRowMapper<TrackerEntity> trackerResultRowMapper,
                              final ResultSetMapper<TrackerEntity> trackerResultSetMapper,
                              final FounderGeneratedId<Long> founderGeneratedId,
                              final EntityInjectorInPreparedStatement<TrackerEntity> entityInjectorToInsert,
                              final EntityInjectorInPreparedStatement<TrackerEntity> entityInjectorToUpdate,
                              final Logger logger) {
        super(dataBaseConnectionPool, trackerResultRowMapper, trackerResultSetMapper, founderGeneratedId,
                entityInjectorToInsert, entityInjectorToUpdate, logger);
    }

    public static EntityRepository<TrackerEntity> create() {
        return SingletonHolder.TRACKER_REPOSITORY;
    }

    public Optional<TrackerEntity> findByImei(final String imei) {
        try {
            final Optional<Connection> optionalConnection = super.dataBaseConnectionPool.findAvailableConnection();
            final Connection connection = optionalConnection.orElseThrow(NoAvailableConnectionInPoolException::new);
            try (final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_TO_SELECT_BY_IMEI)) {
                preparedStatement.setString(PARAMETER_INDEX_IMEI_IN_QUERY_TO_SELECT_BY_IMEI, imei);
                final ResultSet resultSet = preparedStatement.executeQuery();
                this.logger.info(format(MESSAGE_QUERY_TEMPLATE, QUERY_TO_SELECT_BY_IMEI));
                return resultSet.next() ? of(super.entityResultRowMapper.map(resultSet)) : empty();
            } finally {
                super.dataBaseConnectionPool.returnConnectionToPool(connection);
            }
        } catch (final SQLException cause) {
            throw new FindingEntityException(cause);
        }
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
        private static final EntityInjectorInPreparedStatement<TrackerEntity> TRACKER_INJECTOR_TO_INSERT
                = new TrackerInjectorInPreparedStatement(
                PARAMETER_INDEX_IMEI_IN_QUERY_TO_INSERT,
                PARAMETER_INDEX_ENCRYPTED_PASSWORD_IN_QUERY_TO_INSERT,
                PARAMETER_INDEX_PHONE_NUMBER_IN_QUERY_TO_INSERT,
                PARAMETER_INDEX_USER_ID_IN_QUERY_TO_INSERT,
                StringToStringCryptographer.create());

        private static final EntityInjectorInPreparedStatement<TrackerEntity> TRACKER_INJECTOR_TO_UPDATE
                = new TrackerInjectorInPreparedStatement(
                PARAMETER_INDEX_ID_IN_QUERY_TO_UPDATE,
                PARAMETER_INDEX_IMEI_IN_QUERY_TO_UPDATE,
                PARAMETER_INDEX_ENCRYPTED_PASSWORD_IN_QUERY_TO_UPDATE,
                PARAMETER_INDEX_PHONE_NUMBER_IN_QUERY_TO_UPDATE,
                PARAMETER_INDEX_USER_ID_IN_QUERY_TO_UPDATE,
                StringToStringCryptographer.create());

        private static final EntityRepository<TrackerEntity> TRACKER_REPOSITORY = new TrackerRepository(
                DataBaseConnectionPoolImplementation.create(),
                TrackerResultRowMapper.create(),
                TrackerResultSetMapper.create(),
                FounderGeneratedLongId.create(),
                TRACKER_INJECTOR_TO_INSERT,
                TRACKER_INJECTOR_TO_UPDATE,
                Logger.getLogger(TrackerRepository.class.getName()));
    }
}
