package by.zuevvlad.wialontransport.dao;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPool;
import by.zuevvlad.wialontransport.dao.exception.*;
import by.zuevvlad.wialontransport.dao.foundergeneratedid.FounderGeneratedId;
import by.zuevvlad.wialontransport.dao.foundergeneratedid.exception.FindingGeneratedIdByDataBaseException;
import by.zuevvlad.wialontransport.dao.exception.InsertingEntitiesException;
import by.zuevvlad.wialontransport.dao.resultsetmapper.ResultSetMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.exception.ResultSetMappingException;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.entity.DataEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Constructor;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.DataEntity.Longitude.Type.EAST;
import static java.time.LocalDateTime.now;
import static java.util.stream.IntStream.range;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static java.util.Optional.empty;
import static java.lang.Long.MIN_VALUE;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude;
import static by.zuevvlad.wialontransport.entity.DataEntity.Longitude;

@RunWith(MockitoJUnitRunner.class)
public final class DataRepositoryTest {
    private static final String QUERY_TO_SELECT_DATA_BY_ID = "SELECT data.id, data.date, data.time, "
            + "data.latitude_degrees, data.latitude_minutes, data.latitude_minute_share, data.latitude_type, "
            + "data.longitude_degrees, data.longitude_minutes, data.longitude_minute_share, data.longitude_type, "
            + "data.speed, data.course, data.height, data.amount_of_satellites "
            + "FROM data WHERE data.id = ?";
    private static final int PARAMETER_INDEX_DATA_ID_IN_QUERY_TO_SELECT_BY_ID = 1;

    private static final String QUERY_TO_SELECT_ALL_DATA = "SELECT data.id, data.date, data.time, "
            + "data.latitude_degrees, data.latitude_minutes, data.latitude_minute_share, data.latitude_type, "
            + "data.longitude_degrees, data.longitude_minutes, data.longitude_minute_share, data.longitude_type, "
            + "data.speed, data.course, data.height, data.amount_of_satellites FROM data";

    private static final String QUERY_TO_INSERT_DATE = "INSERT INTO data(date, time, "
            + "latitude_degrees, latitude_minutes, latitude_minute_share, latitude_type, "
            + "longitude_degrees, longitude_minutes, longitude_minute_share, longitude_type, "
            + "speed, course, height, amount_of_satellites) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final int PARAMETER_INDEX_DATA_DATE_IN_QUERY_TO_INSERT = 1;
    private static final int PARAMETER_INDEX_DATA_TIME_IN_QUERY_TO_INSERT = 2;
    private static final int PARAMETER_INDEX_DATA_LATITUDE_DEGREES_IN_QUERY_TO_INSERT = 3;
    private static final int PARAMETER_INDEX_DATA_LATITUDE_MINUTES_IN_QUERY_TO_INSERT = 4;
    private static final int PARAMETER_INDEX_DATA_LATITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT = 5;
    private static final int PARAMETER_INDEX_DATA_LATITUDE_TYPE_IN_QUERY_TO_INSERT = 6;
    private static final int PARAMETER_INDEX_DATA_LONGITUDE_DEGREES_IN_QUERY_TO_INSERT = 7;
    private static final int PARAMETER_INDEX_DATA_LONGITUDE_MINUTES_IN_QUERY_TO_INSERT = 8;
    private static final int PARAMETER_INDEX_DATA_LONGITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT = 9;
    private static final int PARAMETER_INDEX_DATA_LONGITUDE_TYPE_IN_QUERY_TO_INSERT = 10;
    private static final int PARAMETER_INDEX_DATA_SPEED_IN_QUERY_TO_INSERT = 11;
    private static final int PARAMETER_INDEX_DATA_COURSE_IN_QUERY_TO_INSERT = 12;
    private static final int PARAMETER_INDEX_DATA_HEIGHT_IN_QUERY_TO_INSERT = 13;
    private static final int PARAMETER_INDEX_DATA_AMOUNT_OF_SATELLITES_IN_QUERY_TO_INSERT = 14;

    private static final String COLUMN_NAME_ID = "id";

    private static final String QUERY_TO_UPDATE_DATE = "UPDATE data SET "
            + "date = ?, "
            + "time = ?, "
            + "latitude_degrees = ?, "
            + "latitude_minutes = ?, "
            + "latitude_minute_share = ?, "
            + "latitude_type = ?, "
            + "longitude_degrees = ?, "
            + "longitude_minutes = ?, "
            + "longitude_minute_share = ?, "
            + "longitude_type = ?, "
            + "speed = ?, "
            + "course = ?, "
            + "height = ?, "
            + "amount_of_satellites = ? "
            + "WHERE data.id = ?";
    private static final int PARAMETER_INDEX_DATA_DATE_IN_QUERY_TO_UPDATE = 1;
    private static final int PARAMETER_INDEX_DATA_TIME_IN_QUERY_TO_UPDATE = 2;
    private static final int PARAMETER_INDEX_DATA_LATITUDE_DEGREES_IN_QUERY_TO_UPDATE = 3;
    private static final int PARAMETER_INDEX_DATA_LATITUDE_MINUTES_IN_QUERY_TO_UPDATE = 4;
    private static final int PARAMETER_INDEX_DATA_LATITUDE_MINUTE_SHARE_IN_QUERY_TO_UPDATE = 5;
    private static final int PARAMETER_INDEX_DATA_LATITUDE_TYPE_IN_QUERY_TO_UPDATE = 6;
    private static final int PARAMETER_INDEX_DATA_LONGITUDE_DEGREES_IN_QUERY_TO_UPDATE = 7;
    private static final int PARAMETER_INDEX_DATA_LONGITUDE_MINUTES_IN_QUERY_TO_UPDATE = 8;
    private static final int PARAMETER_INDEX_DATA_LONGITUDE_MINUTE_SHARE_IN_QUERY_TO_UPDATE = 9;
    private static final int PARAMETER_INDEX_DATA_LONGITUDE_TYPE_IN_QUERY_TO_UPDATE = 10;
    private static final int PARAMETER_INDEX_DATA_SPEED_IN_QUERY_TO_UPDATE = 11;
    private static final int PARAMETER_INDEX_DATA_COURSE_IN_QUERY_TO_UPDATE = 12;
    private static final int PARAMETER_INDEX_DATA_HEIGHT_IN_QUERY_TO_UPDATE = 13;
    private static final int PARAMETER_INDEX_DATA_AMOUNT_OF_SATELLITES_IN_QUERY_TO_UPDATE = 14;
    private static final int PARAMETER_INDEX_DATA_ID_IN_QUERY_TO_UPDATE = 15;

    private static final String QUERY_TO_DELETE_DATA_BY_ID = "DELETE FROM data WHERE data.id = ?";
    private static final int PARAMETER_INDEX_DATA_ID_IN_QUERY_TO_DELETE = 1;

    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;

    @Mock
    private DataBaseConnectionPool mockedDataBaseConnectionPool;

    @Mock
    private ResultRowMapper<DataEntity> mockedDataResultRowMapper;

    @Mock
    private ResultSetMapper<DataEntity> mockedDataResultSetMapper;

    @Mock
    private FounderGeneratedId<Long> mockedFounderGeneratedLongId;

    @Mock
    private Connection mockedConnection;

    @Mock
    private PreparedStatement mockedPreparedStatement;

    @Mock
    private ResultSet mockedResultSet;

    @Mock
    private Statement mockedStatement;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Captor
    private ArgumentCaptor<Connection> connectionArgumentCaptor;

    @Captor
    private ArgumentCaptor<PreparedStatement> preparedStatementArgumentCaptor;

    @Captor
    private ArgumentCaptor<Boolean> booleanArgumentCaptor;

    @Captor
    private ArgumentCaptor<ResultSet> resultSetArgumentCaptor;

    public DataRepositoryTest() {
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
    }

//    @Test
//    public void singletonShouldBeLazyThreadSafe() {
//        final int startedThreadAmount = 50;
//        final BlockingQueue<EntityRepository<Data>> createdRepositories
//                = new ArrayBlockingQueue<>(startedThreadAmount);
//        rangeClosed(1, startedThreadAmount).forEach(i -> {
//            final Thread startedThread = new Thread(() -> {
//                try {
//                    createdRepositories.put(DataRepository.create());
//                } catch (InterruptedException cause) {
//                    throw new RuntimeException(cause);
//                }
//            });
//            startedThread.start();
//        });
//        while (createdRepositories.size() < startedThreadAmount) {
//            Thread.yield();
//        }
//        final long actualAmountOfRepositories = createdRepositories.stream().distinct().count();
//        final long expectedAmountOfRepositories = 1;
//        assertEquals(expectedAmountOfRepositories, actualAmountOfRepositories);
//    }

    @Test
    public void dataShouldBeFoundById()
            throws Exception {
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity expected = dataBuilder
                .catalogId(255)
                .catalogDateTime(now())
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(23)
                        .catalogMinutes(24)
                        .catalogMinuteShare(25)
                        .catalogType(NORTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(26)
                        .catalogMinutes(27)
                        .catalogMinuteShare(28)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(29)
                .catalogHeight(30)
                .catalogAmountSatellites(31)
                .build();

        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString())).thenReturn(this.mockedPreparedStatement);
        when(this.mockedPreparedStatement.executeQuery()).thenReturn(this.mockedResultSet);
        when(this.mockedResultSet.next()).thenReturn(true);
        when(this.mockedDataResultRowMapper.map(any(ResultSet.class))).thenReturn(expected);

        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        final Optional<DataEntity> optionalActual = dataRepository.findById(expected.getId());
        assertTrue(optionalActual.isPresent());
        final DataEntity actual = optionalActual.get();
        assertEquals(expected, actual);

        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();
        verify(this.mockedConnection, times(1))
                .prepareStatement(this.stringArgumentCaptor.capture());
        verify(this.mockedPreparedStatement, times(1))
                .setLong(this.integerArgumentCaptor.capture(), this.longArgumentCaptor.capture());
        verify(this.mockedPreparedStatement, times(1)).executeQuery();
        verify(this.mockedResultSet, times(1)).next();
        verify(this.mockedDataResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());

        assertEquals(QUERY_TO_SELECT_DATA_BY_ID, this.stringArgumentCaptor.getValue());
        assertEquals(PARAMETER_INDEX_DATA_ID_IN_QUERY_TO_SELECT_BY_ID,
                this.integerArgumentCaptor.getValue().intValue());
        assertEquals(expected.getId(), this.longArgumentCaptor.getValue().longValue());
        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = NoAvailableConnectionInPoolException.class)
    public void dataShouldNotBeFoundByIdBecauseOfNoAvailableConnectionInPool()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(empty());

        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.findById(MIN_VALUE);

        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();
    }
    //TODO: начинать отсюда
    @Test(expected = FindingEntityException.class)
    public void dataShouldNotBeFoundByIdBecauseOfExceptionDuringPreparingStatement()
            throws Exception {
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
        dataRepository.findById(MIN_VALUE);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = FindingEntityException.class)
    public void dataShouldNotBeFoundBecauseOfExceptionDuringSettingIdParameter()
            throws Exception {
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString())).thenReturn(this.mockedPreparedStatement);
        doThrow(SQLException.class).when(this.mockedPreparedStatement).setLong(anyInt(), anyLong());
        dataRepository.findById(MIN_VALUE);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test
    public void dataShouldNotBeFoundByIdBecauseOfResultSetDoesntHaveNext()
            throws Exception {
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString())).thenReturn(this.mockedPreparedStatement);
        when(this.mockedPreparedStatement.executeQuery()).thenReturn(this.mockedResultSet);
        when(this.mockedResultSet.next()).thenReturn(false);
        final Optional<DataEntity> optionalData = dataRepository.findById(MIN_VALUE);
        assertTrue(optionalData.isEmpty());

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test
    public void dataListShouldBeFound()
            throws Exception {
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();

        final List<DataEntity> expected = List.of(
                dataBuilder
                        .catalogId(255)
                        .catalogDateTime(now())
                        .catalogLatitude(latitudeBuilder
                                .catalogDegrees(23)
                                .catalogMinutes(24)
                                .catalogMinuteShare(25)
                                .catalogType(NORTH)
                                .build())
                        .catalogLongitude(longitudeBuilder
                                .catalogDegrees(26)
                                .catalogMinutes(27)
                                .catalogMinuteShare(28)
                                .catalogType(EAST)
                                .build())
                        .catalogSpeed(29)
                        .catalogHeight(30)
                        .catalogAmountSatellites(31)
                        .build(),
                dataBuilder
                        .catalogId(256)
                        .catalogDateTime(now())
                        .catalogLatitude(latitudeBuilder
                                .catalogDegrees(32)
                                .catalogMinutes(33)
                                .catalogMinuteShare(34)
                                .catalogType(NORTH)
                                .build())
                        .catalogLongitude(longitudeBuilder
                                .catalogDegrees(35)
                                .catalogMinutes(36)
                                .catalogMinuteShare(37)
                                .catalogType(EAST)
                                .build())
                        .catalogSpeed(38)
                        .catalogHeight(39)
                        .catalogAmountSatellites(40)
                        .build()
        );

        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.createStatement()).thenReturn(this.mockedStatement);
        when(this.mockedStatement.executeQuery(anyString())).thenReturn(this.mockedResultSet);
        when(this.mockedDataResultSetMapper.map(any(ResultSet.class))).thenReturn(expected);

        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        final Collection<DataEntity> actual = dataRepository.findAll();
        assertEquals(expected, actual);

        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();
        verify(this.mockedConnection, times(1)).createStatement();

        verify(this.mockedStatement, times(1)).executeQuery(this.stringArgumentCaptor.capture());
        assertEquals(QUERY_TO_SELECT_ALL_DATA, this.stringArgumentCaptor.getValue());

        verify(this.mockedDataResultSetMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = NoAvailableConnectionInPoolException.class)
    public void dataListShouldNotBeFoundBecauseOfNoAvailableConnectionInPool()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(empty());
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.findAll();
    }

    @Test(expected = FindingEntitiesException.class)
    public void dataListShouldNotBeFoundBecauseOfExceptionDuringCreatingStatement()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.createStatement()).thenThrow(SQLException.class);
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.findAll();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = FindingEntitiesException.class)
    public void dataListShouldNotBeFoundBecauseOfExceptionDuringQueryExecution()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.createStatement()).thenReturn(this.mockedStatement);
        when(this.mockedStatement.executeQuery(anyString())).thenThrow(SQLException.class);
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.findAll();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = ResultSetMappingException.class)
    public void dataListShouldNotBeFoundBecauseOfExceptionDuringMappingResultSetExecution()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.createStatement()).thenReturn(this.mockedStatement);
        when(this.mockedStatement.executeQuery(anyString())).thenReturn(this.mockedResultSet);
        when(this.mockedDataResultSetMapper.map(any(ResultSet.class)))
                .thenThrow(ResultSetMappingException.class);
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.findAll();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @SuppressWarnings("all")
    @Test
    public void dataShouldBeInserted()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString(), anyInt()))
                .thenReturn(this.mockedPreparedStatement);

        final Long expectedGeneratedId = 255L;
        when(this.mockedFounderGeneratedLongId.findLastGeneratedId(any(Statement.class), anyString()))
                .thenReturn(Optional.of(expectedGeneratedId));

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataEntity mockedData = this.createMockedData(now(), latitudeBuilder.build(), longitudeBuilder.build(),
                0, 0, 0, 0);

        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(mockedData);

        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();

        verify(this.mockedConnection, times(1)).prepareStatement(
                this.stringArgumentCaptor.capture(), this.integerArgumentCaptor.capture());
        assertEquals(QUERY_TO_INSERT_DATE, this.stringArgumentCaptor.getValue());
        assertEquals(RETURN_GENERATED_KEYS, this.integerArgumentCaptor.getValue().intValue());

        verify(this.mockedConnection, times(2))
                .setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedAutoCommitArguments = List.of(false, true);
        assertEquals(expectedAutoCommitArguments, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedPreparedStatement, times(1)).executeUpdate();

        verify(this.mockedFounderGeneratedLongId, times(1))
                .findLastGeneratedId(this.preparedStatementArgumentCaptor.capture(),
                        this.stringArgumentCaptor.capture());
        assertSame(this.mockedPreparedStatement, this.preparedStatementArgumentCaptor.getValue());
        assertEquals(COLUMN_NAME_ID, this.stringArgumentCaptor.getValue());

        verify(mockedData, times(1)).setId(this.longArgumentCaptor.capture());
        assertEquals(expectedGeneratedId, this.longArgumentCaptor.getValue());

        verify(this.mockedConnection, times(1)).commit();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = NoAvailableConnectionInPoolException.class)
    public void dataShouldNotBeInsertedBecauseOfNoAvailableConnectionsInPool()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(empty());

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity insertedData = dataBuilder.build();
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(insertedData);
    }

    @SuppressWarnings("all")
    @Test(expected = InsertingEntityException.class)
    public void dataShouldNotBeInsertedBecauseOfExceptionDuringPreparationStatement()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString(), anyInt()))
                .thenThrow(SQLException.class);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity insertedData = dataBuilder.build();

        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(insertedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @SuppressWarnings("all")
    @Test(expected = InsertingEntityException.class)
    public void dataShouldNotBeInsertedBecauseOfExceptionDuringSettingAutoCommit()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString(), anyInt()))
                .thenReturn(this.mockedPreparedStatement);
        doThrow(SQLException.class).when(this.mockedConnection).setAutoCommit(anyBoolean());

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity insertedData = dataBuilder.build();

        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(insertedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @SuppressWarnings("all")
    @Test(expected = InsertingEntityException.class)
    public void dataShouldNotBeInsertedBecauseOfExceptionDuringExecutionUpdate()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString(), anyInt()))
                .thenReturn(this.mockedPreparedStatement);
        when(this.mockedPreparedStatement.executeUpdate()).thenThrow(SQLException.class);

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataEntity mockedData = this.createMockedData(now(), latitudeBuilder.build(), longitudeBuilder.build(),
                2, 2, 2, 2);

        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(mockedData);

        verify(this.mockedConnection, times(2))
                .setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedAutoCommitArguments = List.of(false, true);
        assertEquals(expectedAutoCommitArguments, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @SuppressWarnings("all")
    @Test(expected = FindingGeneratedIdByDataBaseException.class)
    public void transactionShouldBeRollbackBecauseOfExceptionDuringFindingGeneratedId()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString(), anyInt()))
                .thenReturn(this.mockedPreparedStatement);
        when(this.mockedFounderGeneratedLongId.findLastGeneratedId(any(Statement.class), anyString()))
                .thenThrow(FindingGeneratedIdByDataBaseException.class);

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataEntity mockedData = this.createMockedData(now(), latitudeBuilder.build(), longitudeBuilder.build(),
                3, 3, 3, 3);

        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(mockedData);

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedConnection, times(2))
                .setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommitArguments = List.of(false, true);
        assertEquals(expectedCapturedAutoCommitArguments, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @SuppressWarnings("all")
    @Test(expected = NoGeneratedIdException.class)
    public void dataShouldNotBeInsertedBecauseOfNoGeneratedId()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString(), anyInt()))
                .thenReturn(this.mockedPreparedStatement);
        when(this.mockedFounderGeneratedLongId.findLastGeneratedId(any(Statement.class), anyString()))
                .thenReturn(empty());

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity insertedData = dataBuilder.build();
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(insertedData);

        verify(this.mockedConnection, times(2))
                .setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommitArguments = List.of(false, true);
        assertEquals(expectedCapturedAutoCommitArguments, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @SuppressWarnings("all")
    @Test(expected = InsertingEntityException.class)
    public void dataShouldNotBeInsertedBecauseOfExceptionDuringCommittingTransaction()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString(), anyInt())).thenReturn(this.mockedPreparedStatement);

        final long generatedId = 255;
        when(this.mockedFounderGeneratedLongId.findLastGeneratedId(any(Statement.class), anyString()))
                .thenReturn(Optional.of(generatedId));

        doThrow(SQLException.class).when(this.mockedConnection).commit();

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity insertedData = dataBuilder.build();
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(insertedData);

        verify(this.mockedConnection, times(2))
                .setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommitArguments = List.of(false, true);
        assertEquals(expectedCapturedAutoCommitArguments, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @SuppressWarnings("all")
    @Test
    public void dataListShouldBeInserted()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString(), anyInt()))
                .thenReturn(this.mockedPreparedStatement);

        final List<Long> expectedGeneratedIds = List.of(1L, 2L, 3L);
        when(this.mockedFounderGeneratedLongId.findAllGeneratedIds(any(Statement.class), anyString()))
                .thenReturn(expectedGeneratedIds);

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final List<DataEntity> insertedData = List.of(
                this.createMockedData(now(), latitudeBuilder.build(), longitudeBuilder.build(), 1, 2, 3, 4),
                this.createMockedData(now(), latitudeBuilder.build(), longitudeBuilder.build(), 5, 6, 7, 8),
                this.createMockedData(now(), latitudeBuilder.build(), longitudeBuilder.build(), 10, 11, 12, 13)
        );

        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(insertedData);

        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();

        verify(this.mockedConnection, times(1))
                .prepareStatement(this.stringArgumentCaptor.capture(), this.integerArgumentCaptor.capture());
        assertEquals(QUERY_TO_INSERT_DATE, this.stringArgumentCaptor.getValue());
        assertEquals(RETURN_GENERATED_KEYS, this.integerArgumentCaptor.getValue().intValue());

        verify(this.mockedConnection, times(2))
                .setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommitArguments = List.of(false, true);
        assertEquals(expectedCapturedAutoCommitArguments, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedPreparedStatement, times(3)).addBatch();
        verify(this.mockedPreparedStatement, times(3)).clearParameters();
        verify(this.mockedPreparedStatement, times(1)).executeBatch();

        range(0, insertedData.size())
                .forEach(i -> verify(insertedData.get(i), times(1))
                        .setId(this.longArgumentCaptor.capture()));
        assertEquals(expectedGeneratedIds, this.longArgumentCaptor.getAllValues());

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = NoAvailableConnectionInPoolException.class)
    public void dataListShouldNotBeInsertedBecauseOfNoAvailableConnectionInPool()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(empty());
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final List<DataEntity> insertedData = List.of(
                dataBuilder.build(),
                dataBuilder.build(),
                dataBuilder.build());
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(insertedData);
    }

    @SuppressWarnings("all")
    @Test(expected = InsertingEntitiesException.class)
    public void dataListShouldNotBeInsertedBecauseOfExceptionDuringPreparingStatement()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString(), anyInt()))
                .thenThrow(SQLException.class);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final List<DataEntity> insertedData = List.of(
                dataBuilder.build(),
                dataBuilder.build(),
                dataBuilder.build());
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(insertedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @SuppressWarnings("all")
    @Test(expected = InsertingEntitiesException.class)
    public void dataListShouldNotBeInsertedBecauseOfExceptionDuringSetFalseAutoCommit()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString(), anyInt()))
                .thenReturn(this.mockedPreparedStatement);
        doThrow(SQLException.class).when(this.mockedConnection).setAutoCommit(anyBoolean());

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final List<DataEntity> insertedData = List.of(
                dataBuilder.build(),
                dataBuilder.build(),
                dataBuilder.build());
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(insertedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @SuppressWarnings("all")
    @Test(expected = InsertingEntitiesException.class)
    public void dataListShouldNotBeInsertedBecauseOfExceptionDuringAddingBatch()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString(), anyInt()))
                .thenReturn(this.mockedPreparedStatement);
        doThrow(SQLException.class).when(this.mockedPreparedStatement).addBatch();

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final List<DataEntity> insertedData = List.of(
                dataBuilder.build(),
                dataBuilder.build(),
                dataBuilder.build());
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(insertedData);

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedConnection, times(2))
                .setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommitArguments = List.of(false, true);
        assertEquals(expectedCapturedAutoCommitArguments, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @SuppressWarnings("all")
    @Test(expected = InsertingEntitiesException.class)
    public void dataListShouldNotBeInsertedBecauseOfExceptionDuringClearingParameters()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString(), anyInt()))
                .thenReturn(this.mockedPreparedStatement);
        doThrow(SQLException.class).when(this.mockedPreparedStatement).clearParameters();

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final List<DataEntity> insertedData = List.of(
                dataBuilder.build(),
                dataBuilder.build(),
                dataBuilder.build());
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(insertedData);

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedConnection, times(2))
                .setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommitArguments = List.of(false, true);
        assertEquals(expectedCapturedAutoCommitArguments, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @SuppressWarnings("all")
    @Test(expected = InsertingEntitiesException.class)
    public void dataListShouldNotBeInsertedBecauseOfExceptionDuringCommitting()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString(), anyInt()))
                .thenReturn(this.mockedPreparedStatement);
        doThrow(SQLException.class).when(this.mockedConnection).commit();

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final List<DataEntity> insertedData = List.of(
                dataBuilder.build(),
                dataBuilder.build(),
                dataBuilder.build());
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.insert(insertedData);

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedConnection, times(2))
                .setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommitArguments = List.of(false, true);
        assertEquals(expectedCapturedAutoCommitArguments, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test
    public void dataShouldBeUpdated()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString()))
                .thenReturn(this.mockedPreparedStatement);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity updatedData = dataBuilder.build();
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.update(updatedData);

        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();

        verify(this.mockedConnection, times(1))
                .prepareStatement(this.stringArgumentCaptor.capture());
        assertEquals(QUERY_TO_UPDATE_DATE, this.stringArgumentCaptor.getValue());

        verify(this.mockedPreparedStatement, times(1)).executeUpdate();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = NoAvailableConnectionInPoolException.class)
    public void dataShouldNotBeUpdatedBecauseOfNoAvailableConnectionInPool()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(empty());

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity updatedData = dataBuilder.build();
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.update(updatedData);
    }

    @Test(expected = UpdatingEntityException.class)
    public void dataShouldNotBeUpdatedBecauseOfExceptionDuringPreparingStatement()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString()))
                .thenThrow(SQLException.class);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity updatedData = dataBuilder.build();
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.update(updatedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = UpdatingEntityException.class)
    public void dataShouldNotBeUpdatedBecauseOfExceptionDuringExecutionUpdating()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString()))
                .thenReturn(this.mockedPreparedStatement);
        doThrow(SQLException.class).when(this.mockedPreparedStatement).executeUpdate();

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity updatedData = dataBuilder.build();
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.update(updatedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test
    public void dataShouldBeDeleted()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString())).thenReturn(this.mockedPreparedStatement);

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final long idDeletedData = 255;
        final DataEntity deletedData = this.createMockedData(idDeletedData, now(), latitudeBuilder.build(),
                longitudeBuilder.build(), 0, 1, 2, 3);

        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.delete(deletedData);

        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();

        verify(this.mockedConnection, times(1))
                .prepareStatement(this.stringArgumentCaptor.capture());
        assertEquals(QUERY_TO_DELETE_DATA_BY_ID, this.stringArgumentCaptor.getValue());

        verify(this.mockedPreparedStatement, times(1))
                .setLong(this.integerArgumentCaptor.capture(), this.longArgumentCaptor.capture());
        assertEquals(PARAMETER_INDEX_DATA_ID_IN_QUERY_TO_DELETE, this.integerArgumentCaptor.getValue().intValue());
        assertEquals(deletedData.getId(), this.longArgumentCaptor.getValue().longValue());

        verify(this.mockedPreparedStatement, times(1)).executeUpdate();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = NoAvailableConnectionInPoolException.class)
    public void dataShouldNotBeDeletedBecauseOfNoAvailableConnections()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(empty());
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity deletedData = dataBuilder.build();
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.delete(deletedData);
    }

    @Test(expected = DeletingEntityException.class)
    public void dataShouldNotBeDeletedBecauseOfExceptionDuringPreparingStatement()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString()))
                .thenThrow(SQLException.class);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity deletedData = dataBuilder.build();
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.delete(deletedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = DeletingEntityException.class)
    public void dataShouldNotBeDeletedBecauseOfExceptionDuringSettingIdParameter()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString()))
                .thenReturn(this.mockedPreparedStatement);
        doThrow(SQLException.class).when(this.mockedPreparedStatement).setLong(anyInt(), anyLong());

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity deletedData = dataBuilder.build();
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.delete(deletedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = DeletingEntityException.class)
    public void dataShouldBeDeletedBecauseOfExceptionDuringExecutionUpdate()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString()))
                .thenReturn(this.mockedPreparedStatement);
        doThrow(SQLException.class).when(this.mockedPreparedStatement).executeUpdate();

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity deletedData = dataBuilder.build();
        final EntityRepository<DataEntity> dataRepository = this.createDataRepository();
        dataRepository.delete(deletedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    private EntityRepository<DataEntity> createDataRepository()
            throws Exception {
        final Class<? extends EntityRepository<DataEntity>> dataRepositoryClass = null;
        final Constructor<? extends EntityRepository<DataEntity>> dataRepositoryConstructor
                = dataRepositoryClass.getDeclaredConstructor(
                DataBaseConnectionPool.class, ResultRowMapper.class, ResultSetMapper.class,
                FounderGeneratedId.class);
        dataRepositoryConstructor.setAccessible(true);
        try {
            return dataRepositoryConstructor.newInstance(this.mockedDataBaseConnectionPool,
                    this.mockedDataResultRowMapper, this.mockedDataResultSetMapper,
                    this.mockedFounderGeneratedLongId);
        } finally {
            dataRepositoryConstructor.setAccessible(false);
        }
    }

    @SuppressWarnings("all")
    private DataEntity createMockedData(final long id, final LocalDateTime dateTime, final Latitude latitude,
                                        final Longitude longitude, final int speed, final int course, final int height,
                                        final int amountSatellites) {
        final DataEntity mockedData = this.createMockedData(dateTime, latitude, longitude, speed, course, height,
                amountSatellites);
        when(mockedData.getId()).thenReturn(id);
        return mockedData;
    }

    private DataEntity createMockedData(final LocalDateTime dateTime, final Latitude latitude, final Longitude longitude,
                                        final int speed, final int course, final int height, final int amountSatellites) {
        final DataEntity mockedData = mock(DataEntity.class);
        when(mockedData.getDateTime()).thenReturn(dateTime);
        when(mockedData.getLatitude()).thenReturn(latitude);
        when(mockedData.getLongitude()).thenReturn(longitude);
        when(mockedData.getSpeed()).thenReturn(speed);
        when(mockedData.getCourse()).thenReturn(course);
        when(mockedData.getHeight()).thenReturn(height);
        when(mockedData.getAmountSatellites()).thenReturn(amountSatellites);
        return mockedData;
    }
}
