package by.zuevvlad.wialontransport.dao;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPool;
import by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPoolImplementation;
import by.zuevvlad.wialontransport.dao.exception.NoAvailableConnectionInPoolException;
import by.zuevvlad.wialontransport.entity.DataEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

import static java.sql.Timestamp.from;
import static java.time.Instant.now;
import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude;
import static by.zuevvlad.wialontransport.entity.DataEntity.Longitude;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static java.lang.Long.MIN_VALUE;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.LongStream.rangeClosed;
import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.DataEntity.Longitude.Type.EAST;

public final class DataRepositoryIntegrationTest {
    private static final String QUERY_TO_INSERT = "INSERT INTO data(id, date, time, "
            + "latitude_degrees, latitude_minutes, latitude_minute_share, latitude_type, "
            + "longitude_degrees, longitude_minutes, longitude_minute_share, longitude_type, "
            + "speed, course, height, amount_of_satellites) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final int PARAMETER_INDEX_DATA_ID_IN_QUERY_TO_INSERT = 1;
    private static final int PARAMETER_INDEX_DATA_DATE_IN_QUERY_TO_INSERT = 2;
    private static final int PARAMETER_INDEX_DATA_TIME_IN_QUERY_TO_INSERT = 3;
    private static final int PARAMETER_INDEX_DATA_LATITUDE_DEGREES_IN_QUERY_TO_INSERT = 4;
    private static final int PARAMETER_INDEX_DATA_LATITUDE_MINUTES_IN_QUERY_TO_INSERT = 5;
    private static final int PARAMETER_INDEX_DATA_LATITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT = 6;
    private static final int PARAMETER_INDEX_DATA_LATITUDE_TYPE_IN_QUERY_TO_INSERT = 7;
    private static final int PARAMETER_INDEX_DATA_LONGITUDE_DEGREES_IN_QUERY_TO_INSERT = 8;
    private static final int PARAMETER_INDEX_DATA_LONGITUDE_MINUTES_IN_QUERY_TO_INSERT = 9;
    private static final int PARAMETER_INDEX_DATA_LONGITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT = 10;
    private static final int PARAMETER_INDEX_DATA_LONGITUDE_TYPE_IN_QUERY_TO_INSERT = 11;
    private static final int PARAMETER_INDEX_DATA_SPEED_IN_QUERY_TO_INSERT = 12;
    private static final int PARAMETER_INDEX_DATA_COURSE_IN_QUERY_TO_INSERT = 13;
    private static final int PARAMETER_INDEX_DATA_HEIGHT_IN_QUERY_TO_INSERT = 14;
    private static final int PARAMETER_INDEX_DATA_AMOUNT_OF_SATELLITES_IN_QUERY_TO_INSERT = 15;

    private static final int DATA_AMOUNT_FOR_TEST = 10;

    private static final String QUERY_TO_DELETE_ALL_DATA = "DELETE FROM data";

    private static final long VALUE_NOT_EXISTING_ID = MIN_VALUE;

    private final EntityRepository<DataEntity> dataRepository;
    private final DataBaseConnectionPool dataBaseConnectionPool;
    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;

    public DataRepositoryIntegrationTest() {
        this.dataRepository = null;
        this.dataBaseConnectionPool = DataBaseConnectionPoolImplementation.create();
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
    }

    @Before
    public void insertDataForTest()
            throws SQLException {
        final Optional<Connection> optionalConnection = this.dataBaseConnectionPool.findAvailableConnection();
        final Connection connection = optionalConnection.orElseThrow(NoAvailableConnectionInPoolException::new);
        try (final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_TO_INSERT)) {
            for (int i = 1; i <= DATA_AMOUNT_FOR_TEST; i++) {
                preparedStatement.setLong(PARAMETER_INDEX_DATA_ID_IN_QUERY_TO_INSERT, i);
                preparedStatement.setTimestamp(PARAMETER_INDEX_DATA_DATE_IN_QUERY_TO_INSERT, from(now()));
                preparedStatement.setTimestamp(PARAMETER_INDEX_DATA_TIME_IN_QUERY_TO_INSERT, from(now()));

                preparedStatement.setInt(PARAMETER_INDEX_DATA_LATITUDE_DEGREES_IN_QUERY_TO_INSERT, 1);
                preparedStatement.setInt(PARAMETER_INDEX_DATA_LATITUDE_MINUTES_IN_QUERY_TO_INSERT, 2);
                preparedStatement.setInt(PARAMETER_INDEX_DATA_LATITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT, 3);
                preparedStatement.setString(PARAMETER_INDEX_DATA_LATITUDE_TYPE_IN_QUERY_TO_INSERT,
                        Character.toString(Latitude.Type.NORTH.getValue()));

                preparedStatement.setInt(PARAMETER_INDEX_DATA_LONGITUDE_DEGREES_IN_QUERY_TO_INSERT, 4);
                preparedStatement.setInt(PARAMETER_INDEX_DATA_LONGITUDE_MINUTES_IN_QUERY_TO_INSERT, 5);
                preparedStatement.setInt(PARAMETER_INDEX_DATA_LONGITUDE_MINUTE_SHARE_IN_QUERY_TO_INSERT, 6);
                preparedStatement.setString(PARAMETER_INDEX_DATA_LONGITUDE_TYPE_IN_QUERY_TO_INSERT,
                        Character.toString(Longitude.Type.EAST.getValue()));

                preparedStatement.setInt(PARAMETER_INDEX_DATA_SPEED_IN_QUERY_TO_INSERT, 7);
                preparedStatement.setInt(PARAMETER_INDEX_DATA_COURSE_IN_QUERY_TO_INSERT, 8);
                preparedStatement.setInt(PARAMETER_INDEX_DATA_HEIGHT_IN_QUERY_TO_INSERT, 9);
                preparedStatement.setInt(PARAMETER_INDEX_DATA_AMOUNT_OF_SATELLITES_IN_QUERY_TO_INSERT, 10);

                preparedStatement.executeUpdate();
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
            statement.executeUpdate(QUERY_TO_DELETE_ALL_DATA);
        } finally {
            this.dataBaseConnectionPool.returnConnectionToPool(connection);
        }
    }

    @Test
    public void dataShouldBeFoundById() {
        final long id = 5;
        final Optional<DataEntity> optionalData = this.dataRepository.findById(id);
        assertTrue(optionalData.isPresent());
        final DataEntity data = optionalData.get();
        assertEquals(id, data.getId());
    }

    @Test
    public void dataShouldNotBeFoundByNotExistingId() {
        final Optional<DataEntity> optionalData = this.dataRepository.findById(VALUE_NOT_EXISTING_ID);
        assertTrue(optionalData.isEmpty());
    }

    @Test
    public void dataListShouldBeFound() {
        final Collection<DataEntity> data = this.dataRepository.findAll();
        final Set<Long> actualIds = data.stream()
                .map(DataEntity::getId)
                .collect(toSet());
        final Set<Long> expectedIds = rangeClosed(1, DATA_AMOUNT_FOR_TEST)
                .boxed()
                .collect(toSet());
        assertEquals(expectedIds, actualIds);
    }

    @Test
    public void dataShouldBeInserted() {
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity insertedData = dataBuilder
                .catalogDateTime(LocalDateTime.of(2022, 11, 5, 1, 4, 3))
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

        this.dataRepository.insert(insertedData);
        final long generatedId = insertedData.getId();

        final Optional<DataEntity> optionalInsertedDataFromRepository
                = this.dataRepository.findById(generatedId);
        assertTrue(optionalInsertedDataFromRepository.isPresent());

        final DataEntity insertedDataFromRepository = optionalInsertedDataFromRepository.orElseThrow();
        assertEquals(insertedData, insertedDataFromRepository);
    }

    @Test
    public void dataListShouldBeInserted() {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final List<DataEntity> insertedData = List.of(
                dataBuilder
                        .catalogDateTime(LocalDateTime.of(2022, 11, 5, 1, 4, 3))
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
                        .catalogDateTime(LocalDateTime.of(2022, 11, 5, 1, 4, 3))
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
                        .build()
        );
        this.dataRepository.insert(insertedData);

        insertedData.forEach(data -> {
            final Optional<DataEntity> optionalDataFromRepository = this.dataRepository.findById(data.getId());
            assertTrue(optionalDataFromRepository.isPresent());
            final DataEntity dataFromRepository = optionalDataFromRepository.get();
            assertEquals(data, dataFromRepository);
        });
    }

    @Test
    public void dataShouldBeUpdated() {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataEntity updatedData = dataBuilder
                .catalogId(5)
                .catalogDateTime(LocalDateTime.of(2022, 11, 5, 1, 4, 3))
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
        this.dataRepository.update(updatedData);
        final Optional<DataEntity> optionalUpdatedDataFromRepository = this.dataRepository.findById(updatedData.getId());
        assertTrue(optionalUpdatedDataFromRepository.isPresent());
        final DataEntity updatedDataFromRepository = optionalUpdatedDataFromRepository.get();
        assertEquals(updatedData, updatedDataFromRepository);
    }

    @Test
    public void dataShouldBeDeletedById() {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataEntity deletedData = dataBuilder
                .catalogId(5)
                .catalogDateTime(LocalDateTime.of(2022, 11, 5, 1, 4, 3))
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
        this.dataRepository.delete(deletedData);
        final Optional<DataEntity> optionalDeletedDataFromRepository = this.dataRepository.findById(deletedData.getId());
        assertTrue(optionalDeletedDataFromRepository.isEmpty());
    }
}
