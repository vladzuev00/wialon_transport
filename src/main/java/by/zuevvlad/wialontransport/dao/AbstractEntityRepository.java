package by.zuevvlad.wialontransport.dao;

import by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPool;
import by.zuevvlad.wialontransport.dao.exception.*;
import by.zuevvlad.wialontransport.dao.foundergeneratedid.FounderGeneratedId;
import by.zuevvlad.wialontransport.dao.parameterinjector.entity.EntityInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.resultsetmapper.ResultSetMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.entity.Entity;

import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.IntStream.range;

public abstract class AbstractEntityRepository<EntityType extends Entity> implements EntityRepository<EntityType> {
    protected static final String MESSAGE_QUERY_TEMPLATE = "DAO: %s";
    protected static final String MESSAGE_TEMPLATE_QUERY_BY_BATCH_TEMPLATE = "DAO: %s by batch with size %d.";

    private static final int PARAMETER_INDEX_ENTITY_ID_IN_QUERY_TO_SELECT_BY_ID = 1;
    private static final int PARAMETER_INDEX_ENTITY_ID_IN_QUERY_TO_DELETE_BY_ID = 1;
    private static final String COLUMN_NAME_ID = "id";

    protected final DataBaseConnectionPool dataBaseConnectionPool;
    protected final ResultRowMapper<EntityType> entityResultRowMapper;
    private final ResultSetMapper<EntityType> entityResultSetMapper;
    private final FounderGeneratedId<Long> founderGeneratedId;
    private final EntityInjectorInPreparedStatement<EntityType> entityInjectorToInsert;
    private final EntityInjectorInPreparedStatement<EntityType> entityInjectorToUpdate;
    protected final Logger logger;

    public AbstractEntityRepository(final DataBaseConnectionPool dataBaseConnectionPool,
                                    final ResultRowMapper<EntityType> entityResultRowMapper,
                                    final ResultSetMapper<EntityType> entityResultSetMapper,
                                    final FounderGeneratedId<Long> founderGeneratedId,
                                    final EntityInjectorInPreparedStatement<EntityType> entityInjectorToInsert,
                                    final EntityInjectorInPreparedStatement<EntityType> entityInjectorToUpdate,
                                    final Logger logger) {
        this.dataBaseConnectionPool = dataBaseConnectionPool;
        this.entityResultRowMapper = entityResultRowMapper;
        this.entityResultSetMapper = entityResultSetMapper;
        this.founderGeneratedId = founderGeneratedId;
        this.entityInjectorToInsert = entityInjectorToInsert;
        this.entityInjectorToUpdate = entityInjectorToUpdate;
        this.logger = logger;
    }

    @Override
    public final Optional<EntityType> findById(final long id) {
        try {
            final String queryToSelectById = this.findQueryToSelectById();
            final Optional<Connection> optionalConnection = this.dataBaseConnectionPool.findAvailableConnection();
            final Connection connection = optionalConnection.orElseThrow(NoAvailableConnectionInPoolException::new);
            try (final PreparedStatement preparedStatement = connection.prepareStatement(queryToSelectById)) {
                preparedStatement.setLong(PARAMETER_INDEX_ENTITY_ID_IN_QUERY_TO_SELECT_BY_ID, id);
                final ResultSet resultSet = preparedStatement.executeQuery();
                this.logger.info(format(MESSAGE_QUERY_TEMPLATE, queryToSelectById));
                return resultSet.next() ? of(this.entityResultRowMapper.map(resultSet)) : empty();
            } finally {
                this.dataBaseConnectionPool.returnConnectionToPool(connection);
            }
        } catch (final SQLException cause) {
            throw new FindingEntityException(cause);
        }
    }

    @Override
    public final Collection<EntityType> findAll() {
        try {
            final Optional<Connection> optionalConnection = this.dataBaseConnectionPool.findAvailableConnection();
            final Connection connection = optionalConnection.orElseThrow(NoAvailableConnectionInPoolException::new);
            try (final Statement statement = connection.createStatement()) {
                final String queryToSelectAll = this.findQueryToSelectAll();
                final ResultSet resultSet = statement.executeQuery(queryToSelectAll);
                this.logger.info(format(MESSAGE_QUERY_TEMPLATE, queryToSelectAll));
                return this.entityResultSetMapper.map(resultSet);
            } finally {
                this.dataBaseConnectionPool.returnConnectionToPool(connection);
            }
        } catch (final SQLException cause) {
            throw new FindingEntitiesException(cause);
        }
    }

    @Override
    public final void insert(final EntityType insertedEntity) {
        try {
            final String queryToInsertEntity = this.findQueryToInsertEntity();
            final Optional<Connection> optionalConnection = this.dataBaseConnectionPool.findAvailableConnection();
            final Connection connection = optionalConnection.orElseThrow(NoAvailableConnectionInPoolException::new);
            try (final PreparedStatement preparedStatement = connection.prepareStatement(
                    queryToInsertEntity, RETURN_GENERATED_KEYS)) {
                connection.setAutoCommit(false);
                try {
                    this.entityInjectorToInsert.inject(insertedEntity, preparedStatement);
                    preparedStatement.executeUpdate();
                    this.injectGeneratedId(preparedStatement, insertedEntity);
                    connection.commit();
                    this.logger.info(format(MESSAGE_QUERY_TEMPLATE, queryToInsertEntity));
                } catch (final Exception exception) {
                    connection.rollback();
                    throw exception;
                } finally {
                    connection.setAutoCommit(true);
                }
            } finally {
                this.dataBaseConnectionPool.returnConnectionToPool(connection);
            }
        } catch (final SQLException cause) {
            throw new InsertingEntityException(cause);
        }
    }

    @Override
    public final void insert(final List<EntityType> insertedEntities) {
        try {
            final String queryToInsertEntity = this.findQueryToInsertEntity();
            final Optional<Connection> optionalConnection = this.dataBaseConnectionPool.findAvailableConnection();
            final Connection connection = optionalConnection.orElseThrow(NoAvailableConnectionInPoolException::new);
            try (final PreparedStatement preparedStatement = connection.prepareStatement(
                    queryToInsertEntity, RETURN_GENERATED_KEYS)) {
                connection.setAutoCommit(false);
                try {
                    this.prepareBatchInsert(preparedStatement, insertedEntities);
                    preparedStatement.executeBatch();
                    this.injectGeneratedIds(preparedStatement, insertedEntities);
                    connection.commit();
                    this.logger.info(format(MESSAGE_TEMPLATE_QUERY_BY_BATCH_TEMPLATE, queryToInsertEntity,
                            insertedEntities.size()));
                } catch (final Exception exception) {
                    connection.rollback();
                    throw exception;
                } finally {
                    connection.setAutoCommit(true);
                }
            } finally {
                this.dataBaseConnectionPool.returnConnectionToPool(connection);
            }
        } catch (final SQLException cause) {
            throw new InsertingEntitiesException(cause);
        }
    }

    @Override
    public final void update(final EntityType updatedEntity) {
        try {
            final String queryToUpdateEntity = this.findQueryToUpdateEntity();
            final Optional<Connection> optionalConnection = this.dataBaseConnectionPool.findAvailableConnection();
            final Connection connection = optionalConnection.orElseThrow(NoAvailableConnectionInPoolException::new);
            try (final PreparedStatement preparedStatement = connection.prepareStatement(queryToUpdateEntity)) {
                this.entityInjectorToUpdate.inject(updatedEntity, preparedStatement);
                preparedStatement.executeUpdate();
                this.logger.info(format(MESSAGE_QUERY_TEMPLATE, queryToUpdateEntity));
            } finally {
                this.dataBaseConnectionPool.returnConnectionToPool(connection);
            }
        } catch (final SQLException cause) {
            throw new UpdatingEntityException(cause);
        }
    }

    @Override
    public final void delete(final EntityType deletedEntity) {
        try {
            final String queryToDeleteEntityById = this.findQueryToDeleteEntityById();
            final Optional<Connection> optionalConnection = this.dataBaseConnectionPool.findAvailableConnection();
            final Connection connection = optionalConnection.orElseThrow(NoAvailableConnectionInPoolException::new);
            try (final PreparedStatement preparedStatement = connection.prepareStatement(queryToDeleteEntityById)) {
                preparedStatement.setLong(PARAMETER_INDEX_ENTITY_ID_IN_QUERY_TO_DELETE_BY_ID, deletedEntity.getId());
                preparedStatement.executeUpdate();
                this.logger.info(format(MESSAGE_QUERY_TEMPLATE, queryToDeleteEntityById));
            } finally {
                this.dataBaseConnectionPool.returnConnectionToPool(connection);
            }
        } catch (final SQLException cause) {
            throw new DeletingEntityException(cause);
        }
    }

    protected abstract String findQueryToSelectById();

    protected abstract String findQueryToSelectAll();

    protected abstract String findQueryToInsertEntity();

    protected abstract String findQueryToUpdateEntity();

    protected abstract String findQueryToDeleteEntityById();

    private void injectGeneratedId(final PreparedStatement preparedStatement, final EntityType insertedEntity) {
        final Optional<Long> optionalGeneratedId = this.founderGeneratedId.findLastGeneratedId(
                preparedStatement, COLUMN_NAME_ID);
        final Long generatedId = optionalGeneratedId.orElseThrow(NoGeneratedIdException::new);
        insertedEntity.setId(generatedId);
    }

    private void prepareBatchInsert(final PreparedStatement preparedStatement, final List<EntityType> insertedEntities)
            throws SQLException {
        for (final EntityType insertedEntity : insertedEntities) {
            this.entityInjectorToInsert.inject(insertedEntity, preparedStatement);
            preparedStatement.addBatch();
            preparedStatement.clearParameters();
        }
    }

    private void injectGeneratedIds(final PreparedStatement preparedStatement, final List<EntityType> insertedEntities) {
        final List<Long> generatedIds = this.founderGeneratedId.findAllGeneratedIds(preparedStatement, COLUMN_NAME_ID);
        range(0, generatedIds.size()).forEach(i -> {
            final EntityType entity = insertedEntities.get(i);
            final long generatedId = generatedIds.get(i);
            entity.setId(generatedId);
        });
    }
}
