package by.zuevvlad.wialontransport.dao;

import static java.time.LocalDateTime.now;
import static org.mockito.ArgumentMatchers.any;
/*
@RunWith(MockitoJUnitRunner.class)
public final class ExtendedDataRepositoryTest {
    private static final String QUERY_TO_SELECT_BY_ID = "SELECT data.id, data.date, data.time, "
            + "data.latitude_degrees, data.latitude_minutes, data.latitude_minute_share, data.latitude_type, "
            + "data.longitude_degrees, data.longitude_minutes, data.longitude_minute_share, data.longitude_type, "
            + "data.speed, data.course, data.height, data.amount_of_satellites, extended_data.reduction_precision, "
            + "extended_data.inputs, extended_data.outputs, extended_data.analog_inputs, extended_data.driver_key_code, "
            + "extended_data.parameters FROM data INNER JOIN extended_data ON data.id = extended_data.id "
            + "WHERE data.id = ?";
    private static final int PARAMETER_INDEX_ID_IN_QUERY_TO_SELECT_BY_ID = 1;

    private static final String QUERY_TO_SELECT_ALL = "SELECT data.id, data.date, data.time, "
            + "data.latitude_degrees, data.latitude_minutes, data.latitude_minute_share, data.latitude_type, "
            + "data.longitude_degrees, data.longitude_minutes, data.longitude_minute_share, data.longitude_type, "
            + "data.speed, data.course, data.height, data.amount_of_satellites, extended_data.reduction_precision, "
            + "extended_data.inputs, extended_data.outputs, extended_data.analog_inputs, extended_data.driver_key_code, "
            + "extended_data.parameters FROM data INNER JOIN extended_data ON data.id = extended_data.id";

    private static final String QUERY_TO_INSERT
            = "{call insert_extended_data(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

    private static final String OUT_ARGUMENT_NAME_GENERATED_ID = "generated_id";

    private static final String QUERY_TO_UPDATE
            = "{call update_extended_data(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

    private static final String QUERY_TO_DELETE_BY_ID = "DELETE FROM data WHERE data.id = ?";
    private static final int PARAMETER_INDEX_ID_IN_QUERY_TO_DELETE_BY_ID = 1;

    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;
    private final Supplier<ParameterBuilder> parameterBuilderSupplier;
    private final Supplier<ExtendedDataBuilder> extendedDataBuilderSupplier;

    @Mock
    private DataBaseConnectionPool mockedDataBaseConnectionPool;

    @Mock
    private ResultRowMapper<ExtendedData> mockedExtendedDataResultRowMapper;

    @Mock
    private ResultSetMapper<ExtendedData> extendedDataResultSetMapper;

    @Mock
    private Connection mockedConnection;

    @Mock
    private PreparedStatement mockedPreparedStatement;

    @Mock
    private ResultSet mockedResultSet;

    @Mock
    private Statement mockedStatement;

    @Mock
    private CallableStatement mockedCallableStatement;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Captor
    private ArgumentCaptor<Connection> connectionArgumentCaptor;

    @Captor
    private ArgumentCaptor<ResultSet> resultSetArgumentCaptor;

    @Captor
    private ArgumentCaptor<Boolean> booleanArgumentCaptor;

    public ExtendedDataRepositoryTest() {
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
        this.parameterBuilderSupplier = ParameterBuilder::new;
        this.extendedDataBuilderSupplier = ExtendedDataBuilder::new;
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<EntityRepository<ExtendedData>> createdRepositories
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdRepositories.put(ExtendedDataRepository.create());
                } catch (InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdRepositories.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfRepositories = createdRepositories.stream().distinct().count();
        final long expectedAmountOfRepositories = 1;
        assertEquals(expectedAmountOfRepositories, actualAmountOfRepositories);
    }

    @Test
    public void extendedDataShouldBeFoundById()
            throws Exception {
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();

        final ExtendedData expected = extendedDataBuilder
                .catalogData(dataBuilder
                        .catalogId(256)
                        .catalogDateTime(now())
                        .catalogLatitude(latitudeBuilder
                                .catalogDegrees(38)
                                .catalogMinutes(39)
                                .catalogMinuteShare(40)
                                .catalogType(NORTH)
                                .build())
                        .catalogLongitude(longitudeBuilder
                                .catalogDegrees(41)
                                .catalogMinutes(42)
                                .catalogMinuteShare(43)
                                .catalogType(EAST)
                                .build())
                        .catalogSpeed(44)
                        .catalogHeight(45)
                        .catalogAmountSatellites(46)
                        .build())
                .catalogReductionPrecision(47.)
                .catalogInputs(48)
                .catalogOutputs(49)
                .catalogAnalogInputs(50)
                .catalogDriverKeyCode("second driver key code")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("first parameter")
                                .catalogValueType(DOUBLE)
                                .catalogValue(51)
                                .build(),
                        parameterBuilder
                                .catalogName("second parameter")
                                .catalogValueType(NOT_DEFINED)
                                .catalogValue("52")
                                .build()
                )).build();

        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString())).thenReturn(this.mockedPreparedStatement);
        when(this.mockedPreparedStatement.executeQuery()).thenReturn(this.mockedResultSet);
        when(this.mockedResultSet.next()).thenReturn(true);
        when(this.mockedExtendedDataResultRowMapper.map(any(ResultSet.class))).thenReturn(expected);

        final EntityRepository<ExtendedData> extendedDataEntityRepository = this.createExtendedDataRepository();
        final Optional<ExtendedData> optionalActual = extendedDataEntityRepository.findById(expected.getId());
        assertTrue(optionalActual.isPresent());
        final ExtendedData actual = optionalActual.get();
        assertEquals(expected, actual);

        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();
        verify(this.mockedConnection, times(1)).prepareStatement(this.stringArgumentCaptor.capture());
        assertEquals(QUERY_TO_SELECT_BY_ID, this.stringArgumentCaptor.getValue());

        verify(this.mockedPreparedStatement, times(1))
                .setLong(this.integerArgumentCaptor.capture(), this.longArgumentCaptor.capture());
        assertEquals(PARAMETER_INDEX_ID_IN_QUERY_TO_SELECT_BY_ID, this.integerArgumentCaptor.getValue().intValue());
        assertEquals(expected.getId(), this.longArgumentCaptor.getValue().longValue());

        verify(this.mockedPreparedStatement, times(1)).executeQuery();
        verify(this.mockedResultSet, times(1)).next();
        verify(this.mockedExtendedDataResultRowMapper, times(1)).map(any(ResultSet.class));

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = NoAvailableConnectionInPoolException.class)
    public void extendedDataShouldNotBeFoundByIdBecauseOfNoAvailableConnection()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(empty());
        final EntityRepository<ExtendedData> extendedDataEntityRepository = this.createExtendedDataRepository();
        final long id = 5;
        extendedDataEntityRepository.findById(id);
    }

    @Test(expected = FindingEntityException.class)
    public void extendedDataShouldNotBeFoundByIdBecauseOfExceptionDuringPreparingStatement()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString())).thenThrow(SQLException.class);
        final EntityRepository<ExtendedData> extendedDataEntityRepository = this.createExtendedDataRepository();
        final long id = 5;
        extendedDataEntityRepository.findById(id);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = FindingEntityException.class)
    public void extendedDataShouldNotBeFoundByIdBecauseOfExceptionDuringSettingIdParameter()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString())).thenReturn(this.mockedPreparedStatement);
        doThrow(SQLException.class).when(this.mockedPreparedStatement).setLong(anyInt(), anyLong());

        final EntityRepository<ExtendedData> extendedDataEntityRepository = this.createExtendedDataRepository();
        final long id = 5;
        extendedDataEntityRepository.findById(id);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = FindingEntityException.class)
    public void extendedDataShouldNotBeFoundByIdBecauseOfExceptionDuringExecutionQuery()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString())).thenReturn(this.mockedPreparedStatement);
        when(this.mockedPreparedStatement.executeQuery()).thenThrow(SQLException.class);

        final EntityRepository<ExtendedData> extendedDataEntityRepository = this.createExtendedDataRepository();
        final long id = 5;
        extendedDataEntityRepository.findById(id);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = FindingEntityException.class)
    public void extendedDataShouldNotBeFoundByIdBecauseOfExceptionDuringCallingNextOnResultSet()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString())).thenReturn(this.mockedPreparedStatement);
        when(this.mockedPreparedStatement.executeQuery()).thenReturn(this.mockedResultSet);
        doThrow(SQLException.class).when(this.mockedResultSet).next();

        final EntityRepository<ExtendedData> extendedDataEntityRepository = this.createExtendedDataRepository();
        final long id = 5;
        extendedDataEntityRepository.findById(id);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test
    public void extendedDataListShouldBeFound()
            throws Exception {
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();

        final List<ExtendedData> expected = List.of(
                extendedDataBuilder
                        .catalogData(dataBuilder
                                .catalogId(255)
                                .catalogDateTime(now())
                                .catalogLatitude(latitudeBuilder
                                        .catalogDegrees(38)
                                        .catalogMinutes(39)
                                        .catalogMinuteShare(40)
                                        .catalogType(NORTH)
                                        .build())
                                .catalogLongitude(longitudeBuilder
                                        .catalogDegrees(41)
                                        .catalogMinutes(42)
                                        .catalogMinuteShare(43)
                                        .catalogType(EAST)
                                        .build())
                                .catalogSpeed(44)
                                .catalogHeight(45)
                                .catalogAmountSatellites(46)
                                .build())
                        .catalogReductionPrecision(47.)
                        .catalogInputs(48)
                        .catalogOutputs(49)
                        .catalogAnalogInputs(50)
                        .catalogDriverKeyCode("second driver key code")
                        .catalogParameters(List.of(
                                parameterBuilder
                                        .catalogName("first parameter")
                                        .catalogValueType(DOUBLE)
                                        .catalogValue(51)
                                        .build(),
                                parameterBuilder
                                        .catalogName("second parameter")
                                        .catalogValueType(NOT_DEFINED)
                                        .catalogValue("52")
                                        .build()
                        )).build(),
                extendedDataBuilder
                        .catalogData(dataBuilder
                                .catalogId(256)
                                .catalogDateTime(now())
                                .catalogLatitude(latitudeBuilder
                                        .catalogDegrees(53)
                                        .catalogMinutes(54)
                                        .catalogMinuteShare(55)
                                        .catalogType(NORTH)
                                        .build())
                                .catalogLongitude(longitudeBuilder
                                        .catalogDegrees(56)
                                        .catalogMinutes(57)
                                        .catalogMinuteShare(58)
                                        .catalogType(EAST)
                                        .build())
                                .catalogSpeed(59)
                                .catalogHeight(60)
                                .catalogAmountSatellites(61)
                                .build())
                        .catalogReductionPrecision(62.)
                        .catalogInputs(63)
                        .catalogOutputs(64)
                        .catalogAnalogInputs(65)
                        .catalogDriverKeyCode("second driver key code")
                        .catalogParameters(List.of(
                                parameterBuilder
                                        .catalogName("first parameter")
                                        .catalogValueType(DOUBLE)
                                        .catalogValue(66)
                                        .build(),
                                parameterBuilder
                                        .catalogName("second parameter")
                                        .catalogValueType(NOT_DEFINED)
                                        .catalogValue("67")
                                        .build()
                        )).build()
        );

        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.createStatement()).thenReturn(this.mockedStatement);
        when(this.mockedStatement.executeQuery(anyString())).thenReturn(this.mockedResultSet);
        when(this.extendedDataResultSetMapper.map(any(ResultSet.class))).thenReturn(expected);

        final EntityRepository<ExtendedData> extendedDataEntityRepository = this.createExtendedDataRepository();
        final Collection<ExtendedData> actual = extendedDataEntityRepository.findAll();
        assertEquals(expected, actual);

        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();
        verify(this.mockedConnection, times(1)).createStatement();

        verify(this.mockedStatement, times(1)).executeQuery(this.stringArgumentCaptor.capture());
        assertEquals(QUERY_TO_SELECT_ALL, this.stringArgumentCaptor.getValue());

        verify(this.extendedDataResultSetMapper, times(1)).map(this.resultSetArgumentCaptor.capture());
        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = NoAvailableConnectionInPoolException.class)
    public void allExtendedDataShouldNotBeFoundBecauseOfNoAvailableConnection()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(empty());
        final EntityRepository<ExtendedData> extendedDataEntityRepository = this.createExtendedDataRepository();
        extendedDataEntityRepository.findAll();
    }

    @Test(expected = FindingEntitiesException.class)
    public void allExtendedDataShouldNotBeFoundBecauseOfExceptionDuringCreatingStatement()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.createStatement()).thenThrow(SQLException.class);

        final EntityRepository<ExtendedData> extendedDataEntityRepository = this.createExtendedDataRepository();
        extendedDataEntityRepository.findAll();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = FindingEntitiesException.class)
    public void allExtendedDataShouldNotBeFoundBecauseOfExceptionDuringExecutionQuery()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.createStatement()).thenReturn(this.mockedStatement);
        when(this.mockedStatement.executeQuery(anyString())).thenThrow(SQLException.class);

        final EntityRepository<ExtendedData> extendedDataEntityRepository = this.createExtendedDataRepository();
        extendedDataEntityRepository.findAll();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = ResultSetMappingException.class)
    public void allExtendedDataShouldNotBeFoundBecauseOfExceptionDuringMappingResultSet()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.createStatement()).thenReturn(this.mockedStatement);
        when(this.mockedStatement.executeQuery(anyString())).thenReturn(this.mockedResultSet);
        when(this.extendedDataResultSetMapper.map(any(ResultSet.class))).thenThrow(ResultSetMappingException.class);

        final EntityRepository<ExtendedData> extendedDataEntityRepository = this.createExtendedDataRepository();
        extendedDataEntityRepository.findAll();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test
    public void extendedDataShouldBeInserted()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString())).thenReturn(this.mockedCallableStatement);

        final long generatedId = 255L;
        when(this.mockedCallableStatement.getLong(anyInt())).thenReturn(generatedId);

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedData mockedExtendedData = this.createMockedExtendedData(now(), latitudeBuilder.build(),
                longitudeBuilder.build(), 0, 0, 0, 0, 0., 0, 0, 0, "driver key code",
                List.of(
                        parameterBuilder.build(),
                        parameterBuilder.build(),
                        parameterBuilder.build()
                ));

        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(mockedExtendedData);

        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();

        verify(this.mockedConnection, times(1)).prepareCall(this.stringArgumentCaptor.capture());
        assertEquals(QUERY_TO_INSERT, this.stringArgumentCaptor.getValue());

        verify(this.mockedConnection, times(2)).setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommits = List.of(false, true);
        assertEquals(expectedCapturedAutoCommits, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedCallableStatement, times(1))
                .registerOutParameter(this.stringArgumentCaptor.capture(), this.integerArgumentCaptor.capture());
        assertEquals(OUT_ARGUMENT_NAME_GENERATED_ID, this.stringArgumentCaptor.getValue());
        assertEquals(BIGINT, this.integerArgumentCaptor.getValue().intValue());

        verify(this.mockedCallableStatement, times(1)).executeUpdate();

        verify(this.mockedCallableStatement, times(1)).getLong(this.stringArgumentCaptor.capture());
        assertEquals(OUT_ARGUMENT_NAME_GENERATED_ID, this.stringArgumentCaptor.getValue());

        verify(mockedExtendedData, times(1)).setId(this.longArgumentCaptor.capture());
        assertEquals(generatedId, this.longArgumentCaptor.getValue().longValue());

        verify(this.mockedConnection, times(1)).commit();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = NoAvailableConnectionInPoolException.class)
    public void extendedDataShouldNotBeInsertedBecauseOfNoAvailableConnection()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(empty());

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData insertedExtendedData = extendedDataBuilder.build();
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);
    }

    @Test(expected = InsertingEntityException.class)
    public void extendedDataShouldNotBeInsertedBecauseOfPreparingCall()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString())).thenThrow(SQLException.class);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData insertedExtendedData = extendedDataBuilder.build();
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = InsertingEntityException.class)
    public void extendedDataShouldNotBeInsertedBecauseOfExceptionDuringSettingFalseAutoCommit()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString())).thenReturn(this.mockedCallableStatement);
        doThrow(SQLException.class).when(this.mockedConnection).setAutoCommit(anyBoolean());

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData insertedExtendedData = extendedDataBuilder.build();
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = InsertingEntityException.class)
    public void extendedDataShouldNotBeInsertedBecauseOfExceptionDuringRegistrationOutParameter()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString())).thenReturn(this.mockedCallableStatement);
        doThrow(SQLException.class).when(this.mockedCallableStatement).registerOutParameter(anyInt(), anyInt());

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData insertedExtendedData = extendedDataBuilder.build();
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedConnection, times(2)).setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommits = List.of(false, true);
        assertEquals(expectedCapturedAutoCommits, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = InsertingEntityException.class)
    public void extendedDataShouldNotBeInsertedBecauseOfExceptionDuringExecutionUpdate()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString())).thenReturn(this.mockedCallableStatement);
        doThrow(SQLException.class).when(this.mockedCallableStatement).executeUpdate();

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData insertedExtendedData = extendedDataBuilder.build();
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedConnection, times(2)).setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommits = List.of(false, true);
        assertEquals(expectedCapturedAutoCommits, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = InsertingEntityException.class)
    public void extendedDataShouldNotBeInsertedBecauseOfExceptionDuringGettingGeneratedId()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString())).thenReturn(this.mockedCallableStatement);
        when(this.mockedCallableStatement.getLong(anyInt())).thenThrow(SQLException.class);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData insertedExtendedData = extendedDataBuilder.build();
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedConnection, times(2)).setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommits = List.of(false, true);
        assertEquals(expectedCapturedAutoCommits, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = InsertingEntityException.class)
    public void extendedDataShouldNotBeInsertedBecauseOfExceptionDuringCommitting()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString())).thenReturn(this.mockedCallableStatement);

        final long generatedId = 255L;
        when(this.mockedCallableStatement.getLong(anyString())).thenReturn(generatedId);

        doThrow(SQLException.class).when(this.mockedConnection).commit();

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData insertedExtendedData = extendedDataBuilder.build();
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedConnection, times(2)).setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommits = List.of(false, true);
        assertEquals(expectedCapturedAutoCommits, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = InsertingEntityException.class)
    public void extendedDataShouldNotBeInsertedBecauseOfExceptionDuringRegistrationOutParameterAndRollback()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString())).thenReturn(this.mockedCallableStatement);
        doThrow(SQLException.class).when(this.mockedCallableStatement).registerOutParameter(anyInt(), anyInt());
        doThrow(SQLException.class).when(this.mockedConnection).rollback();

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData insertedExtendedData = extendedDataBuilder.build();
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedConnection, times(2))
                .setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommits = List.of(false, true);
        assertEquals(expectedCapturedAutoCommits, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test
    public void listExtendedDataShouldBeInserted()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString())).thenReturn(this.mockedCallableStatement);

        final List<Long> generatedId = List.of(255L, 256L);
        when(this.mockedCallableStatement.getLong(anyString()))
                .thenReturn(generatedId.get(0))
                .thenReturn(generatedId.get(1));

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final List<ExtendedData> insertedExtendedData = List.of(
                this.createMockedExtendedData(now(), latitudeBuilder.build(), longitudeBuilder.build(),
                        0, 0, 0, 0, 0., 0, 0,
                        0, "first driver key code", List.of(
                                parameterBuilder.build(),
                                parameterBuilder.build(),
                                parameterBuilder.build()
                        )),
                this.createMockedExtendedData(now(), latitudeBuilder.build(), longitudeBuilder.build(),
                        1, 1, 1, 1, 1., 1, 1,
                        1, "second driver key code", List.of(
                                parameterBuilder.build(),
                                parameterBuilder.build(),
                                parameterBuilder.build()
                        )));

        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();

        verify(this.mockedConnection, times(1)).prepareCall(this.stringArgumentCaptor.capture());

        verify(this.mockedConnection, times(2)).setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommits = List.of(false, true);
        assertEquals(expectedCapturedAutoCommits, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedCallableStatement, times(1))
                .registerOutParameter(this.stringArgumentCaptor.capture(), this.integerArgumentCaptor.capture());
        assertEquals(BIGINT, this.integerArgumentCaptor.getValue().intValue());

        verify(this.mockedCallableStatement, times(insertedExtendedData.size())).executeUpdate();

        verify(this.mockedCallableStatement, times(insertedExtendedData.size()))
                .getLong(this.stringArgumentCaptor.capture());
        final List<String> expectedStringArguments
                = List.of(QUERY_TO_INSERT, OUT_ARGUMENT_NAME_GENERATED_ID, OUT_ARGUMENT_NAME_GENERATED_ID,
                OUT_ARGUMENT_NAME_GENERATED_ID);
        assertEquals(expectedStringArguments, this.stringArgumentCaptor.getAllValues());

        insertedExtendedData.forEach(
                mockedData -> verify(mockedData, times(1))
                        .setId(this.longArgumentCaptor.capture()));
        assertEquals(generatedId, this.longArgumentCaptor.getAllValues());

        verify(this.mockedConnection, times(1)).commit();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = NoAvailableConnectionInPoolException.class)
    public void listExtendedDataShouldNotBeInsertedBecauseOfNoAvailableConnection()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(empty());

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final List<ExtendedData> insertedExtendedData = List.of(
                extendedDataBuilder.build(), extendedDataBuilder.build());
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);
    }

    @Test(expected = InsertingEntitiesException.class)
    public void listExtendedDataShouldNotBeInsertedBecauseOfExceptionDuringPreparingCall()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString()))
                .thenThrow(SQLException.class);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final List<ExtendedData> insertedExtendedData = List.of(
                extendedDataBuilder.build(), extendedDataBuilder.build());
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = InsertingEntitiesException.class)
    public void listExtendedDataShouldNotBeInsertedBecauseOfExceptionDuringSettingFalseAutoCommit()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString()))
                .thenReturn(this.mockedCallableStatement);
        doThrow(SQLException.class).when(this.mockedConnection).setAutoCommit(anyBoolean());

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final List<ExtendedData> insertedExtendedData = List.of(
                extendedDataBuilder.build(), extendedDataBuilder.build());
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = InsertingEntitiesException.class)
    public void listExtendedDataShouldNotInsertedBecauseOfExceptionDuringRegistrationOutParameter()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString()))
                .thenReturn(this.mockedCallableStatement);
        doThrow(SQLException.class).when(this.mockedCallableStatement)
                .registerOutParameter(anyInt(), anyInt());

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final List<ExtendedData> insertedExtendedData = List.of(
                extendedDataBuilder.build(), extendedDataBuilder.build());
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedConnection, times(2)).setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommits = List.of(false, true);
        assertEquals(expectedCapturedAutoCommits, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = InsertingEntityException.class)
    public void extendedDataListShouldNotBeInsertedBecauseOfExceptionDuringExecutionUpdate()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString()))
                .thenReturn(this.mockedCallableStatement);
        doThrow(SQLException.class).when(this.mockedCallableStatement).executeUpdate();

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final List<ExtendedData> insertedExtendedData = List.of(
                extendedDataBuilder.build(), extendedDataBuilder.build());
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedConnection, times(2)).setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommits = List.of(false, true);
        assertEquals(expectedCapturedAutoCommits, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = InsertingEntityException.class)
    public void extendedDataListShouldBeInsertedBecauseOfExceptionDuringGettingGeneratedId()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString()))
                .thenReturn(this.mockedCallableStatement);
        when(this.mockedCallableStatement.getLong(anyInt())).thenThrow(SQLException.class);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final List<ExtendedData> insertedExtendedData = List.of(
                extendedDataBuilder.build(), extendedDataBuilder.build());
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedConnection, times(2))
                .setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommits = List.of(false, true);
        assertEquals(expectedCapturedAutoCommits, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = InsertingEntitiesException.class)
    public void extendedDataListShouldNotBeInsertedBecauseOfExceptionDuringCommitting()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString()))
                .thenReturn(this.mockedCallableStatement);

        final long generatedId = 255;
        when(this.mockedCallableStatement.getLong(anyString())).thenReturn(generatedId);

        doThrow(SQLException.class).when(this.mockedConnection).commit();

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final List<ExtendedData> insertedExtendedData = List.of(
                extendedDataBuilder.build(), extendedDataBuilder.build());
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedConnection, times(2))
                .setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommits = List.of(false, true);
        assertEquals(expectedCapturedAutoCommits, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = InsertingEntitiesException.class)
    public void extendedDataListShouldNotBeInsertedBecauseOfExceptionDuringRegistrationOutParameterAndRollback()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString()))
                .thenReturn(this.mockedCallableStatement);
        doThrow(SQLException.class).when(this.mockedCallableStatement)
                .registerOutParameter(anyInt(), anyInt());
        doThrow(SQLException.class).when(this.mockedConnection).rollback();

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final List<ExtendedData> insertedExtendedData = List.of(
                extendedDataBuilder.build(), extendedDataBuilder.build());
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.insert(insertedExtendedData);

        verify(this.mockedConnection, times(2))
                .setAutoCommit(this.booleanArgumentCaptor.capture());
        final List<Boolean> expectedCapturedAutoCommits = List.of(false, true);
        assertEquals(expectedCapturedAutoCommits, this.booleanArgumentCaptor.getAllValues());

        verify(this.mockedConnection, times(1)).rollback();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test
    public void extendedDataShouldBeUpdated()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString())).thenReturn(this.mockedCallableStatement);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData updatedExtendedData = extendedDataBuilder.build();
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.update(updatedExtendedData);

        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();

        verify(this.mockedConnection, times(1))
                .prepareCall(this.stringArgumentCaptor.capture());
        assertEquals(QUERY_TO_UPDATE, this.stringArgumentCaptor.getValue());

        verify(this.mockedCallableStatement, times(1)).executeUpdate();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = NoAvailableConnectionInPoolException.class)
    public void extendedDataShouldNotBeUpdatedBecauseOfNoAvailableConnectionInPool()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(empty());

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData updatedExtendedData = extendedDataBuilder.build();
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.update(updatedExtendedData);
    }

    @Test(expected = UpdatingEntityException.class)
    public void extendedDataShouldNotBeUpdatedBecauseOfExceptionDuringPreparationCall()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString())).thenThrow(SQLException.class);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData updatedExtendedData = extendedDataBuilder.build();
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.update(updatedExtendedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = UpdatingEntityException.class)
    public void extendedDataShouldNotBeUpdatedBecauseOfExceptionDuringExecutionUpdate()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareCall(anyString())).thenReturn(this.mockedCallableStatement);
        doThrow(SQLException.class).when(this.mockedCallableStatement).executeUpdate();

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData updatedExtendedData = extendedDataBuilder.build();
        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.update(updatedExtendedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test
    public void extendedDataShouldBeDeleted()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString()))
                .thenReturn(this.mockedPreparedStatement);

        final long idExtendedData = 255;
        final ExtendedData mockedExtendedData = mock(ExtendedData.class);
        when(mockedExtendedData.getId()).thenReturn(idExtendedData);

        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.delete(mockedExtendedData);

        verify(this.mockedDataBaseConnectionPool, times(1)).findAvailableConnection();

        verify(this.mockedConnection, times(1))
                .prepareStatement(this.stringArgumentCaptor.capture());
        assertEquals(QUERY_TO_DELETE_BY_ID, this.stringArgumentCaptor.getValue());

        verify(this.mockedPreparedStatement, times(1))
                .setLong(this.integerArgumentCaptor.capture(), this.longArgumentCaptor.capture());
        assertEquals(PARAMETER_INDEX_ID_IN_QUERY_TO_DELETE_BY_ID, this.integerArgumentCaptor.getValue().intValue());
        assertEquals(idExtendedData, this.longArgumentCaptor.getValue().longValue());

        verify(this.mockedPreparedStatement, times(1)).executeUpdate();

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = NoAvailableConnectionInPoolException.class)
    public void extendedDataShouldNotBeDeletedBecauseNoAvailableConnectionInPool()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection()).thenReturn(empty());

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData deletedExtendedData = extendedDataBuilder.build();

        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.delete(deletedExtendedData);
    }

    @Test(expected = DeletingEntityException.class)
    public void extendedDataShouldNotBeDeletedBecauseOfExceptionDuringPreparingStatement()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString()))
                .thenThrow(SQLException.class);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData deletedExtendedData = extendedDataBuilder.build();

        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.delete(deletedExtendedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = DeletingEntityException.class)
    public void extendedDataShouldNotBeDeletedBecauseOfExceptionDuringExecutionUpdating()
            throws Exception {
        when(this.mockedDataBaseConnectionPool.findAvailableConnection())
                .thenReturn(Optional.of(this.mockedConnection));
        when(this.mockedConnection.prepareStatement(anyString()))
                .thenReturn(this.mockedPreparedStatement);
        doThrow(SQLException.class).when(this.mockedPreparedStatement).executeUpdate();

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ExtendedData deletedExtendedData = extendedDataBuilder.build();

        final EntityRepository<ExtendedData> extendedDataRepository = this.createExtendedDataRepository();
        extendedDataRepository.delete(deletedExtendedData);

        verify(this.mockedDataBaseConnectionPool, times(1))
                .returnConnectionToPool(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    private EntityRepository<ExtendedData> createExtendedDataRepository()
            throws Exception {
        final Class<? extends EntityRepository<ExtendedData>> extendedDataRepositoryClass
                = ExtendedDataRepository.class;
        final Constructor<? extends EntityRepository<ExtendedData>> extendedDataRepositoryConstructor
                = extendedDataRepositoryClass.getDeclaredConstructor(DataBaseConnectionPool.class,
                ResultRowMapper.class, ResultSetMapper.class);
        extendedDataRepositoryConstructor.setAccessible(true);
        try {
            return extendedDataRepositoryConstructor.newInstance(this.mockedDataBaseConnectionPool,
                    this.mockedExtendedDataResultRowMapper, this.extendedDataResultSetMapper);
        } finally {
            extendedDataRepositoryConstructor.setAccessible(false);
        }
    }

    @SuppressWarnings("all")
    private ExtendedData createMockedExtendedData(final LocalDateTime dateTime, final Latitude latitude,
                                                  final Longitude longitude, final int speed, final int course,
                                                  final int height, final int amountSatellites,
                                                  final double reductionPrecision, final int inputs, final int outputs,
                                                  final int analogInputs, final String driverKeyCode,
                                                  final List<ExtendedData.Parameter> parameters) {
        final ExtendedData mockedExtendedData = mock(ExtendedData.class);
        when(mockedExtendedData.getDateTime()).thenReturn(dateTime);
        when(mockedExtendedData.getLatitude()).thenReturn(latitude);
        when(mockedExtendedData.getLongitude()).thenReturn(longitude);
        when(mockedExtendedData.getSpeed()).thenReturn(speed);
        when(mockedExtendedData.getCourse()).thenReturn(course);
        when(mockedExtendedData.getHeight()).thenReturn(height);
        when(mockedExtendedData.getAmountSatellites()).thenReturn(amountSatellites);
        when(mockedExtendedData.getReductionPrecision()).thenReturn(reductionPrecision);
        when(mockedExtendedData.getInputs()).thenReturn(inputs);
        when(mockedExtendedData.getOutputs()).thenReturn(outputs);
        when(mockedExtendedData.getAnalogInputs()).thenReturn(analogInputs);
        when(mockedExtendedData.getDriverKeyCode()).thenReturn(driverKeyCode);
        when(mockedExtendedData.getParameters()).thenReturn(parameters);
        return mockedExtendedData;
    }
}
*/
