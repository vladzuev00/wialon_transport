package by.zuevvlad.wialontransport.dao;

import static java.sql.Timestamp.valueOf;
import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
/*
public final class ExtendedDataRepositoryIntegrationTest {
    private static final String QUERY_TO_INSERT
            = "{call insert_extended_data(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    private static final int PARAMETER_INDEX_DATA_IN_DATE_TIME_IN_QUERY_TO_INSERT = 1;
    private static final int PARAMETER_INDEX_DATA_IN_LATITUDE_DEGREES_IN_QUERY_TO_INSERT = 2;
    private static final int PARAMETER_INDEX_DATA_IN_LATITUDE_MINUTES_IN_QUERY_TO_INSERT = 3;
    private static final int PARAMETER_INDEX_DATA_IN_LATITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT = 4;
    private static final int PARAMETER_INDEX_DATA_IN_LATITUDE_TYPE_IN_QUERY_TO_INSERT = 5;
    private static final int PARAMETER_INDEX_DATA_IN_LONGITUDE_DEGREES_IN_QUERY_TO_INSERT = 6;
    private static final int PARAMETER_INDEX_DATA_IN_LONGITUDE_MINUTES_IN_QUERY_TO_INSERT = 7;
    private static final int PARAMETER_INDEX_DATA_IN_LONGITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT = 8;
    private static final int PARAMETER_INDEX_DATA_IN_LONGITUDE_TYPE_IN_QUERY_TO_INSERT = 9;
    private static final int PARAMETER_INDEX_DATA_IN_SPEED_IN_QUERY_TO_INSERT = 10;
    private static final int PARAMETER_INDEX_DATA_IN_COURSE_IN_QUERY_TO_INSERT = 11;
    private static final int PARAMETER_INDEX_DATA_IN_HEIGHT_IN_QUERY_TO_INSERT = 12;
    private static final int PARAMETER_INDEX_DATA_IN_AMOUNT_OF_SATELLITES_IN_QUERY_TO_INSERT = 13;

    private static final int PARAMETER_INDEX_IN_REDUCTION_PRECISION_IN_QUERY_TO_INSERT = 14;
    private static final int PARAMETER_INDEX_IN_INPUTS_IN_QUERY_TO_INSERT = 15;
    private static final int PARAMETER_INDEX_IN_OUTPUTS_IN_QUERY_TO_INSERT = 16;
    private static final int PARAMETER_INDEX_IN_ANALOG_INPUTS_IN_QUERY_TO_INSERT = 17;
    private static final int PARAMETER_INDEX_IN_DRIVER_KEY_CODE_IN_QUERY_TO_INSERT = 18;
    private static final int PARAMETER_INDEX_IN_PARAMETERS_IN_QUERY_TO_INSERT = 19;
    private static final int PARAMETER_INDEX_DATA_OUT_ID_IN_QUERY_TO_INSERT = 20;

    private static final int AMOUNT_INSERTED_EXTENDED_DATA_FOR_TEST = 10;

    private static final String QUERY_TO_DELETE_ALL = "DELETE FROM data";

    private static final long VALUE_NOT_EXISTING_ID = MIN_VALUE;

    private final DataBaseConnectionPool dataBaseConnectionPool;

    private final List<Long> generatedIds;

    private final EntityRepository<ExtendedData> extendedDataRepository;

    public ExtendedDataRepositoryIntegrationTest() {
        this.dataBaseConnectionPool = DataBaseConnectionPoolImplementation.create();
        this.generatedIds = new ArrayList<>();
        this.extendedDataRepository = ExtendedDataRepository.create();
    }

    @Before
    public void insertDataForTest()
            throws SQLException {
        final Optional<Connection> optionalConnection = this.dataBaseConnectionPool.findAvailableConnection();
        final Connection connection = optionalConnection.orElseThrow(NoAvailableConnectionInPoolException::new);
        try (final CallableStatement callableStatement = connection.prepareCall(QUERY_TO_INSERT)) {
            callableStatement.registerOutParameter(PARAMETER_INDEX_DATA_OUT_ID_IN_QUERY_TO_INSERT, BIGINT);

            for (int i = 0; i < AMOUNT_INSERTED_EXTENDED_DATA_FOR_TEST; i++) {
                callableStatement.setTimestamp(PARAMETER_INDEX_DATA_IN_DATE_TIME_IN_QUERY_TO_INSERT, valueOf(now()));

                callableStatement.setInt(PARAMETER_INDEX_DATA_IN_LATITUDE_DEGREES_IN_QUERY_TO_INSERT, 1);
                callableStatement.setInt(PARAMETER_INDEX_DATA_IN_LATITUDE_MINUTES_IN_QUERY_TO_INSERT, 2);
                callableStatement.setInt(PARAMETER_INDEX_DATA_IN_LATITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT, 3);
                callableStatement.setString(PARAMETER_INDEX_DATA_IN_LATITUDE_TYPE_IN_QUERY_TO_INSERT,
                        Character.toString(NORTH.getValue()));

                callableStatement.setInt(PARAMETER_INDEX_DATA_IN_LONGITUDE_DEGREES_IN_QUERY_TO_INSERT, 4);
                callableStatement.setInt(PARAMETER_INDEX_DATA_IN_LONGITUDE_MINUTES_IN_QUERY_TO_INSERT, 5);
                callableStatement.setInt(PARAMETER_INDEX_DATA_IN_LONGITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT, 6);
                callableStatement.setString(PARAMETER_INDEX_DATA_IN_LONGITUDE_TYPE_IN_QUERY_TO_INSERT,
                        Character.toString(EAST.getValue()));

                callableStatement.setInt(PARAMETER_INDEX_DATA_IN_SPEED_IN_QUERY_TO_INSERT, 7);
                callableStatement.setInt(PARAMETER_INDEX_DATA_IN_COURSE_IN_QUERY_TO_INSERT, 8);
                callableStatement.setInt(PARAMETER_INDEX_DATA_IN_HEIGHT_IN_QUERY_TO_INSERT, 9);
                callableStatement.setInt(PARAMETER_INDEX_DATA_IN_AMOUNT_OF_SATELLITES_IN_QUERY_TO_INSERT, 10);

                callableStatement.setDouble(PARAMETER_INDEX_IN_REDUCTION_PRECISION_IN_QUERY_TO_INSERT, 11.f);
                callableStatement.setInt(PARAMETER_INDEX_IN_INPUTS_IN_QUERY_TO_INSERT, 12);
                callableStatement.setInt(PARAMETER_INDEX_IN_OUTPUTS_IN_QUERY_TO_INSERT, 13);
                callableStatement.setInt(PARAMETER_INDEX_IN_ANALOG_INPUTS_IN_QUERY_TO_INSERT, 14);
                callableStatement.setString(PARAMETER_INDEX_IN_DRIVER_KEY_CODE_IN_QUERY_TO_INSERT, "driver key code");
                callableStatement.setString(PARAMETER_INDEX_IN_PARAMETERS_IN_QUERY_TO_INSERT,
                        "parameter:2:563434.34453, parameter:3:value");

                callableStatement.executeUpdate();

                final long generatedId = callableStatement.getLong(PARAMETER_INDEX_DATA_OUT_ID_IN_QUERY_TO_INSERT);
                this.generatedIds.add(generatedId);
            }
        } finally {
            this.dataBaseConnectionPool.returnConnectionToPool(connection);
        }
    }

    @After
    public void deleteDataForTest()
            throws SQLException {
        final Optional<Connection> optionalConnection = this.dataBaseConnectionPool.findAvailableConnection();
        final Connection connection = optionalConnection.orElseThrow(NoAvailableConnectionInPoolException::new);
        try (final Statement statement = connection.createStatement()) {
            statement.executeUpdate(QUERY_TO_DELETE_ALL);
        } finally {
            this.dataBaseConnectionPool.returnConnectionToPool(connection);
        }
        this.generatedIds.clear();
    }

    @Test
    public void extendedDataShouldBeFoundById() {
        final long id = this.generatedIds.get(this.generatedIds.size() / 2);
        final Optional<ExtendedData> optionalExtendedData = this.extendedDataRepository.findById(id);
        assertTrue(optionalExtendedData.isPresent());
        final ExtendedData extendedData = optionalExtendedData.get();
        assertEquals(id, extendedData.getId());
    }

    @Test
    public void extendedDataShouldNotBeFoundByNotExistingId() {
        final Optional<ExtendedData> optionalExtendedData = this.extendedDataRepository.findById(VALUE_NOT_EXISTING_ID);
        assertTrue(optionalExtendedData.isEmpty());
    }

    @Test
    public void allExtendedDataShouldBeFound() {
        final Collection<ExtendedData> foundExtendedData = this.extendedDataRepository.findAll();
        final List<Long> actualIds = foundExtendedData.stream()
                .map(ExtendedData::getId)
                .collect(toList());
        assertEquals(this.generatedIds, actualIds);
    }

    @Test
    public void extendedDataShouldBeInserted() {
        throw new RuntimeException();
    }

    @Test
    public void extendedDataShouldBeUpdated() {
        throw new RuntimeException();
    }

    @Test
    public void extendedDataShouldBeDeleted() {
        throw new RuntimeException();
    }
}
*/
