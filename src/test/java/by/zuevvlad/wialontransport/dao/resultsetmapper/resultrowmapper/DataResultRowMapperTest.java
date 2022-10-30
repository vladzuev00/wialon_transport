package by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.dao.resultsetmapper.exception.ResultSetMappingException;
import by.zuevvlad.wialontransport.entity.DataEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.DataEntity.Longitude.Type.EAST;
import static java.lang.Class.forName;
import static java.sql.Timestamp.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude;
import static by.zuevvlad.wialontransport.entity.DataEntity.Longitude;

@RunWith(MockitoJUnitRunner.class)
public final class DataResultRowMapperTest {
    private static final String CLASS_NAME_DATE_TIME_RESULT_ROW_MAPPER
            = "by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.DataResultRowMapper$DateTimeResultRowMapper";
    private static final String CLASS_NAME_LATITUDE_RESULT_ROW_MAPPER
            = "by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.DataResultRowMapper$LatitudeResultRowMapper";
    private static final String CLASS_NAME_LONGITUDE_RESULT_ROW_MAPPER
            = "by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.DataResultRowMapper$LongitudeResultRowMapper";

    private static final String COLUMN_NAME_DATE = "date";
    private static final String COLUMN_NAME_TIME = "time";

    private static final String COLUMN_NAME_LATITUDE_DEGREES = "latitude_degrees";
    private static final String COLUMN_NAME_LATITUDE_MINUTES = "latitude_minutes";
    private static final String COLUMN_NAME_LATITUDE_MINUTE_SHARE = "latitude_minute_share";
    private static final String COLUMN_NAME_LATITUDE_TYPE = "latitude_type";

    private static final String COLUMN_NAME_LONGITUDE_DEGREES = "longitude_degrees";
    private static final String COLUMN_NAME_LONGITUDE_MINUTES = "longitude_minutes";
    private static final String COLUMN_NAME_LONGITUDE_MINUTE_SHARE = "longitude_minute_share";
    private static final String COLUMN_NAME_LONGITUDE_TYPE = "longitude_type";

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_SPEED = "speed";
    private static final String COLUMN_NAME_COURSE = "course";
    private static final String COLUMN_NAME_HEIGHT = "height";
    private static final String COLUMN_NAME_AMOUNT_SATELLITES = "amount_of_satellites";

    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;

    @Mock
    private ResultSet mockedResultSet;

    @Mock
    private Supplier<LatitudeBuilder> mockedLatitudeBuilderSupplier;

    @Mock
    private LatitudeBuilder mockedLatitudeBuilder;

    @Mock
    private Supplier<LongitudeBuilder> mockedLongitudeBuilderSupplier;

    @Mock
    private LongitudeBuilder mockedLongitudeBuilder;

    @Mock
    private Supplier<DataBuilder> mockedDataBuilderSupplier;

    @Mock
    private DataBuilder mockedDataBuilder;

    @Mock
    private ResultRowMapper<LocalDateTime> mockedLocalDateTimeResultRowMapper;

    @Mock
    private ResultRowMapper<Latitude> mockedLatitudeResultRowMapper;

    @Mock
    private ResultRowMapper<Longitude> mockedLongitudeResultRowMapper;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Captor
    private ArgumentCaptor<Latitude.Type> latitudeTypeArgumentCaptor;

    @Captor
    private ArgumentCaptor<Longitude.Type> longitudeTypeArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Captor
    private ArgumentCaptor<LocalDateTime> localDateTimeArgumentCaptor;

    @Captor
    private ArgumentCaptor<Latitude> latitudeArgumentCaptor;

    @Captor
    private ArgumentCaptor<Longitude> longitudeArgumentCaptor;

    @Captor
    private ArgumentCaptor<ResultSet> resultSetArgumentCaptor;

    public DataResultRowMapperTest() {
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
    }

    @Test
    public void dateTimeShouldBeMapped()
            throws Exception {
        final ResultRowMapper<LocalDateTime> dateTimeResultRowMapper = this.createDateTimeResultRowMapper();
        final LocalDateTime expected = LocalDateTime.of(2000, 11, 15, 11, 55, 4);
        when(this.mockedResultSet.getTimestamp(anyString()))
                .thenReturn(valueOf(expected))
                .thenReturn(valueOf(expected));
        final LocalDateTime actual = dateTimeResultRowMapper.map(this.mockedResultSet);
        assertEquals(expected, actual);

        verify(this.mockedResultSet, times(2)).getTimestamp(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments = List.of(COLUMN_NAME_DATE, COLUMN_NAME_TIME);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void dateTimeShouldNotBeMappedBecauseOfDate()
            throws Exception {
        final ResultRowMapper<LocalDateTime> dateTimeResultRowMapper = this.createDateTimeResultRowMapper();
        when(this.mockedResultSet.getTimestamp(anyString()))
                .thenThrow(SQLException.class);
        dateTimeResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getTimestamp(this.stringArgumentCaptor.capture());
        assertEquals(COLUMN_NAME_DATE, this.stringArgumentCaptor.getValue());
    }

    @Test(expected = ResultSetMappingException.class)
    public void dateTimeShouldNotBeMappedBecauseOfTime()
            throws Exception {
        final ResultRowMapper<LocalDateTime> dateTimeResultRowMapper = this.createDateTimeResultRowMapper();
        when(this.mockedResultSet.getTimestamp(anyString()))
                .thenReturn(valueOf(now()))
                .thenThrow(SQLException.class);
        dateTimeResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(2)).getTimestamp(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments = List.of(COLUMN_NAME_DATE, COLUMN_NAME_TIME);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test
    public void latitudeShouldBeMapped()
            throws Exception {
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final Latitude expected = latitudeBuilder
                .catalogDegrees(40)
                .catalogMinutes(50)
                .catalogMinuteShare(60)
                .catalogType(NORTH)
                .build();
        final ResultRowMapper<Latitude> latitudeResultRowMapper = this.createLatitudeResultRowMapper();
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(expected.getDegrees())
                .thenReturn(expected.getMinutes())
                .thenReturn(expected.getMinuteShare());
        when(this.mockedResultSet.getString(anyString())).thenReturn(Character.toString(NORTH.getValue()));
        when(this.mockedLatitudeBuilder.build()).thenReturn(expected);

        final Latitude actual = latitudeResultRowMapper.map(this.mockedResultSet);
        assertEquals(expected, actual);

        verify(this.mockedResultSet, times(3)).getInt(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getString(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_LATITUDE_DEGREES, COLUMN_NAME_LATITUDE_MINUTES, COLUMN_NAME_LATITUDE_MINUTE_SHARE,
                COLUMN_NAME_LATITUDE_TYPE);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());

        verify(this.mockedLatitudeBuilderSupplier, times(1)).get();

        verify(this.mockedLatitudeBuilder, times(1))
                .catalogDegrees(this.integerArgumentCaptor.capture());
        verify(this.mockedLatitudeBuilder, times(1))
                .catalogMinutes(this.integerArgumentCaptor.capture());
        verify(this.mockedLatitudeBuilder, times(1))
                .catalogMinuteShare(this.integerArgumentCaptor.capture());
        final List<Integer> expectedCapturedIntegerArguments
                = List.of(expected.getDegrees(), expected.getMinutes(), expected.getMinuteShare());
        assertEquals(expectedCapturedIntegerArguments, this.integerArgumentCaptor.getAllValues());

        verify(this.mockedLatitudeBuilder, times(1))
                .catalogType(this.latitudeTypeArgumentCaptor.capture());
        assertSame(expected.getType(), this.latitudeTypeArgumentCaptor.getValue());

        verify(this.mockedLatitudeBuilder, times(1)).build();
    }

    @Test(expected = ResultSetMappingException.class)
    public void latitudeShouldNotBeMappedBecauseOfDegrees()
            throws Exception {
        final ResultRowMapper<Latitude> latitudeResultRowMapper = this.createLatitudeResultRowMapper();
        when(this.mockedResultSet.getInt(anyString())).thenThrow(SQLException.class);
        latitudeResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getInt(this.stringArgumentCaptor.capture());
        assertEquals(COLUMN_NAME_LATITUDE_DEGREES, this.stringArgumentCaptor.getValue());
    }

    @Test(expected = ResultSetMappingException.class)
    public void latitudeShouldNotBeMappedBecauseOfMinutes()
            throws Exception {
        final ResultRowMapper<Latitude> latitudeResultRowMapper = this.createLatitudeResultRowMapper();
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenThrow(SQLException.class);
        latitudeResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(2)).getInt(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_LATITUDE_DEGREES, COLUMN_NAME_LATITUDE_MINUTES);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void latitudeShouldNotBeMappedBecauseOfMinuteShare()
            throws Exception {
        final ResultRowMapper<Latitude> latitudeResultRowMapper = this.createLatitudeResultRowMapper();
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenReturn(0)
                .thenThrow(SQLException.class);
        latitudeResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(3)).getInt(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments =
                List.of(COLUMN_NAME_LATITUDE_DEGREES, COLUMN_NAME_LATITUDE_MINUTES, COLUMN_NAME_LATITUDE_MINUTE_SHARE);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void latitudeShouldNotBeMappedBecauseOfType()
            throws Exception {
        final ResultRowMapper<Latitude> latitudeResultRowMapper = this.createLatitudeResultRowMapper();
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenReturn(0)
                .thenReturn(0);
        when(this.mockedResultSet.getString(anyString())).thenThrow(SQLException.class);
        latitudeResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(3)).getInt(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getString(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments =
                List.of(COLUMN_NAME_LATITUDE_DEGREES, COLUMN_NAME_LATITUDE_MINUTES, COLUMN_NAME_LATITUDE_MINUTE_SHARE,
                        COLUMN_NAME_LATITUDE_TYPE);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test
    public void longitudeShouldBeMapped()
            throws Exception {
        final ResultRowMapper<Longitude> longitudeResultRowMapper = this.createLongitudeResultRowMapper();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Longitude expected = longitudeBuilder
                .catalogDegrees(40)
                .catalogMinutes(50)
                .catalogMinuteShare(60)
                .catalogType(EAST)
                .build();
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(expected.getDegrees())
                .thenReturn(expected.getMinutes())
                .thenReturn(expected.getMinuteShare());
        when(this.mockedResultSet.getString(anyString())).thenReturn(Character.toString(EAST.getValue()));
        when(this.mockedLongitudeBuilder.build()).thenReturn(expected);
        final Longitude actual = longitudeResultRowMapper.map(this.mockedResultSet);
        assertEquals(expected, actual);

        verify(this.mockedResultSet, times(3)).getInt(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getString(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_LONGITUDE_DEGREES, COLUMN_NAME_LONGITUDE_MINUTES,
                COLUMN_NAME_LONGITUDE_MINUTE_SHARE, COLUMN_NAME_LONGITUDE_TYPE);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());

        verify(this.mockedLongitudeBuilderSupplier, times(1)).get();

        verify(this.mockedLongitudeBuilder, times(1))
                .catalogDegrees(this.integerArgumentCaptor.capture());
        verify(this.mockedLongitudeBuilder, times(1))
                .catalogMinutes(this.integerArgumentCaptor.capture());
        verify(this.mockedLongitudeBuilder, times(1))
                .catalogMinuteShare(this.integerArgumentCaptor.capture());
        final List<Integer> expectedCapturedIntegerArguments =
                List.of(expected.getDegrees(), expected.getMinutes(), expected.getMinuteShare());
        assertEquals(expectedCapturedIntegerArguments, this.integerArgumentCaptor.getAllValues());

        verify(this.mockedLongitudeBuilder, times(1))
                .catalogType(this.longitudeTypeArgumentCaptor.capture());
        assertSame(expected.getType(), this.longitudeTypeArgumentCaptor.getValue());

        verify(this.mockedLongitudeBuilder, times(1)).build();
    }

    @Test(expected = ResultSetMappingException.class)
    public void longitudeShouldNotBeMappedBecauseOfDegrees()
            throws Exception {
        final ResultRowMapper<Longitude> longitudeResultRowMapper = this.createLongitudeResultRowMapper();
        when(this.mockedResultSet.getInt(anyString())).thenThrow(SQLException.class);
        longitudeResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getInt(this.stringArgumentCaptor.capture());
        assertEquals(COLUMN_NAME_LONGITUDE_DEGREES, this.stringArgumentCaptor.getValue());
    }

    @Test(expected = ResultSetMappingException.class)
    public void longitudeShouldNotBeMappedBecauseOfMinutes()
            throws Exception {
        final ResultRowMapper<Longitude> longitudeResultRowMapper = this.createLongitudeResultRowMapper();
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenThrow(SQLException.class);
        longitudeResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(2)).getInt(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_LONGITUDE_DEGREES, COLUMN_NAME_LONGITUDE_MINUTES);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void longitudeShouldNotBeMappedBecauseOfMinuteShare()
            throws Exception {
        final ResultRowMapper<Longitude> longitudeResultRowMapper = this.createLongitudeResultRowMapper();
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenReturn(0)
                .thenThrow(SQLException.class);
        longitudeResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(3)).getInt(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_LONGITUDE_DEGREES, COLUMN_NAME_LONGITUDE_MINUTES,
                COLUMN_NAME_LONGITUDE_MINUTE_SHARE);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void longitudeShouldNotBeMappedBecauseOfType()
            throws Exception {
        final ResultRowMapper<Longitude> longitudeResultRowMapper = this.createLongitudeResultRowMapper();
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenReturn(0)
                .thenReturn(0);
        when(this.mockedResultSet.getString(anyString())).thenThrow(SQLException.class);
        longitudeResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(3)).getInt(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getString(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_LONGITUDE_DEGREES, COLUMN_NAME_LONGITUDE_MINUTES,
                COLUMN_NAME_LONGITUDE_MINUTE_SHARE, COLUMN_NAME_LONGITUDE_TYPE);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test
    public void dataShouldBeMapped()
            throws Exception {
        final ResultRowMapper<DataEntity> dataResultRowMapper = this.createDataResultRowMapper();

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity expected = dataBuilder
                .catalogId(255)
                .catalogDateTime(LocalDateTime.of(2022, 10, 10, 10, 10, 10))
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

        when(this.mockedResultSet.getLong(anyString())).thenReturn(expected.getId());
        when(this.mockedLocalDateTimeResultRowMapper.map(any(ResultSet.class))).thenReturn(expected.getDateTime());
        when(this.mockedLatitudeResultRowMapper.map(any(ResultSet.class))).thenReturn(expected.getLatitude());
        when(this.mockedLongitudeResultRowMapper.map(any(ResultSet.class))).thenReturn(expected.getLongitude());
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(expected.getSpeed())
                .thenReturn(expected.getCourse())
                .thenReturn(expected.getHeight())
                .thenReturn(expected.getAmountSatellites());
        when(this.mockedDataBuilder.build()).thenReturn(expected);

        final DataEntity actual = dataResultRowMapper.map(this.mockedResultSet);
        assertEquals(expected, actual);

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());

        verify(this.mockedLocalDateTimeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());

        verify(this.mockedLatitudeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());

        verify(this.mockedLongitudeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());

        verify(this.mockedResultSet, times(4)).getInt(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments =
                List.of(COLUMN_NAME_ID, COLUMN_NAME_SPEED, COLUMN_NAME_COURSE, COLUMN_NAME_HEIGHT,
                        COLUMN_NAME_AMOUNT_SATELLITES);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());

        verify(this.mockedDataBuilderSupplier, times(1)).get();

        verify(this.mockedDataBuilder, times(1)).catalogId(this.longArgumentCaptor.capture());
        assertEquals(expected.getId(), this.longArgumentCaptor.getValue().longValue());

        verify(this.mockedDataBuilder, times(1))
                .catalogDateTime(this.localDateTimeArgumentCaptor.capture());
        assertEquals(expected.getDateTime(), this.localDateTimeArgumentCaptor.getValue());

        verify(this.mockedDataBuilder, times(1))
                .catalogLatitude(this.latitudeArgumentCaptor.capture());
        assertEquals(expected.getLatitude(), this.latitudeArgumentCaptor.getValue());

        verify(this.mockedDataBuilder, times(1))
                .catalogLongitude(this.longitudeArgumentCaptor.capture());
        assertEquals(expected.getLongitude(), this.longitudeArgumentCaptor.getValue());

        verify(this.mockedDataBuilder, times(1))
                .catalogSpeed(this.integerArgumentCaptor.capture());
        verify(this.mockedDataBuilder, times(1))
                .catalogCourse(this.integerArgumentCaptor.capture());
        verify(this.mockedDataBuilder, times(1))
                .catalogHeight(this.integerArgumentCaptor.capture());
        verify(this.mockedDataBuilder, times(1))
                .catalogAmountSatellites(this.integerArgumentCaptor.capture());
        final List<Integer> expectedCapturedIntegerArguments
                = List.of(expected.getSpeed(), expected.getCourse(), expected.getHeight(),
                expected.getAmountSatellites());
        assertEquals(expectedCapturedIntegerArguments, this.integerArgumentCaptor.getAllValues());

        verify(this.mockedDataBuilder, times(1)).build();
    }

    @Test(expected = ResultSetMappingException.class)
    public void dataShouldNotBeMappedBecauseOfId()
            throws Exception {
        final ResultRowMapper<DataEntity> dataResultRowMapper = this.createDataResultRowMapper();
        when(this.mockedResultSet.getLong(anyString())).thenThrow(SQLException.class);
        dataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());
        assertEquals(COLUMN_NAME_ID, this.stringArgumentCaptor.getValue());
    }

    @Test(expected = ResultSetMappingException.class)
    public void dataShouldNotBeMappedBecauseOfDateTime()
            throws Exception {
        final ResultRowMapper<DataEntity> dataResultRowMapper = this.createDataResultRowMapper();
        when(this.mockedResultSet.getLong(anyString())).thenReturn(0L);
        when(this.mockedLocalDateTimeResultRowMapper.map(any(ResultSet.class)))
                .thenThrow(ResultSetMappingException.class);
        dataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());
        assertEquals(COLUMN_NAME_ID, this.stringArgumentCaptor.getValue());

        verify(this.mockedLocalDateTimeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        assertEquals(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());
    }

    @Test(expected = ResultSetMappingException.class)
    public void dataShouldNotBeMappedBecauseOfLatitude()
            throws Exception {
        final ResultRowMapper<DataEntity> dataResultRowMapper = this.createDataResultRowMapper();

        when(this.mockedResultSet.getLong(anyString())).thenReturn(0L);
        when(this.mockedLocalDateTimeResultRowMapper.map(any(ResultSet.class))).thenReturn(now());
        when(this.mockedLatitudeResultRowMapper.map(any(ResultSet.class))).thenThrow(ResultSetMappingException.class);

        dataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());
        assertEquals(COLUMN_NAME_ID, this.stringArgumentCaptor.getValue());

        verify(this.mockedLocalDateTimeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedLatitudeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        final List<ResultSet> expectedCapturedResultSetArguments = List.of(this.mockedResultSet, this.mockedResultSet);
        assertEquals(expectedCapturedResultSetArguments, this.resultSetArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void dataShouldNotBeMappedBecauseOfLongitude()
            throws Exception {
        final ResultRowMapper<DataEntity> dataResultRowMapper = this.createDataResultRowMapper();

        when(this.mockedResultSet.getLong(anyString())).thenReturn(0L);
        when(this.mockedLocalDateTimeResultRowMapper.map(any(ResultSet.class))).thenReturn(now());

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        when(this.mockedLatitudeResultRowMapper.map(any(ResultSet.class))).thenReturn(latitudeBuilder.build());

        when(this.mockedLongitudeResultRowMapper.map(any(ResultSet.class))).thenThrow(ResultSetMappingException.class);

        dataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());
        assertEquals(COLUMN_NAME_ID, this.stringArgumentCaptor.getValue());

        verify(this.mockedLocalDateTimeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedLatitudeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedLongitudeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        final List<ResultSet> expectedCapturedResultSetArguments
                = List.of(this.mockedResultSet, this.mockedResultSet, this.mockedResultSet);
        assertEquals(expectedCapturedResultSetArguments, this.resultSetArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void dataShouldNotBeMappedBecauseOfSpeed()
            throws Exception {
        when(this.mockedResultSet.getLong(anyString())).thenReturn(0L);
        when(this.mockedLocalDateTimeResultRowMapper.map(any(ResultSet.class))).thenReturn(now());

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        when(this.mockedLatitudeResultRowMapper.map(any(ResultSet.class))).thenReturn(latitudeBuilder.build());

        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        when(this.mockedLongitudeResultRowMapper.map(any(ResultSet.class))).thenReturn(longitudeBuilder.build());

        when(this.mockedResultSet.getInt(anyString())).thenThrow(SQLException.class);

        final ResultRowMapper<DataEntity> dataResultRowMapper = this.createDataResultRowMapper();
        dataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getInt(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments = List.of(COLUMN_NAME_ID, COLUMN_NAME_SPEED);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());

        verify(this.mockedLocalDateTimeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedLatitudeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedLongitudeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        final List<ResultSet> expectedCapturedResultSetArguments
                = List.of(this.mockedResultSet, this.mockedResultSet, this.mockedResultSet);
        assertEquals(expectedCapturedResultSetArguments, this.resultSetArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void dataShouldNotBeMappedBecauseOfCourse()
            throws Exception {
        when(this.mockedResultSet.getLong(anyString())).thenReturn(0L);
        when(this.mockedLocalDateTimeResultRowMapper.map(any(ResultSet.class))).thenReturn(now());

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        when(this.mockedLatitudeResultRowMapper.map(any(ResultSet.class))).thenReturn(latitudeBuilder.build());

        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        when(this.mockedLongitudeResultRowMapper.map(any(ResultSet.class))).thenReturn(longitudeBuilder.build());

        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenThrow(SQLException.class);

        final ResultRowMapper<DataEntity> dataResultRowMapper = this.createDataResultRowMapper();
        dataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(2)).getInt(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_ID, COLUMN_NAME_SPEED, COLUMN_NAME_COURSE);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());

        verify(this.mockedLocalDateTimeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedLatitudeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedLongitudeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        final List<ResultSet> expectedCapturedResultSetArguments
                = List.of(this.mockedResultSet, this.mockedResultSet, this.mockedResultSet);
        assertEquals(expectedCapturedResultSetArguments, this.resultSetArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void dataShouldNotBeMappedBecauseOfHeight()
            throws Exception {
        when(this.mockedResultSet.getLong(anyString())).thenReturn(0L);
        when(this.mockedLocalDateTimeResultRowMapper.map(any(ResultSet.class))).thenReturn(now());

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        when(this.mockedLatitudeResultRowMapper.map(any(ResultSet.class))).thenReturn(latitudeBuilder.build());

        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        when(this.mockedLongitudeResultRowMapper.map(any(ResultSet.class))).thenReturn(longitudeBuilder.build());

        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenReturn(0)
                .thenThrow(SQLException.class);

        final ResultRowMapper<DataEntity> dataResultRowMapper = this.createDataResultRowMapper();
        dataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(3)).getInt(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_ID, COLUMN_NAME_SPEED, COLUMN_NAME_COURSE, COLUMN_NAME_HEIGHT);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());

        verify(this.mockedLocalDateTimeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedLatitudeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedLongitudeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        final List<ResultSet> expectedCapturedResultSetArguments
                = List.of(this.mockedResultSet, this.mockedResultSet, this.mockedResultSet);
        assertEquals(expectedCapturedResultSetArguments, this.resultSetArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void dataShouldNotBeMappedBecauseOfAmountSatellites()
            throws Exception {
        when(this.mockedResultSet.getLong(anyString())).thenReturn(0L);
        when(this.mockedLocalDateTimeResultRowMapper.map(any(ResultSet.class))).thenReturn(now());

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        when(this.mockedLatitudeResultRowMapper.map(any(ResultSet.class))).thenReturn(latitudeBuilder.build());

        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        when(this.mockedLongitudeResultRowMapper.map(any(ResultSet.class))).thenReturn(longitudeBuilder.build());

        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenReturn(0)
                .thenReturn(0)
                .thenThrow(SQLException.class);

        final ResultRowMapper<DataEntity> dataResultRowMapper = this.createDataResultRowMapper();
        dataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(4)).getInt(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_ID, COLUMN_NAME_SPEED, COLUMN_NAME_COURSE, COLUMN_NAME_HEIGHT,
                COLUMN_NAME_AMOUNT_SATELLITES);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());

        verify(this.mockedLocalDateTimeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedLatitudeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedLongitudeResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        final List<ResultSet> expectedCapturedResultSetArguments
                = List.of(this.mockedResultSet, this.mockedResultSet, this.mockedResultSet);
        assertEquals(expectedCapturedResultSetArguments, this.resultSetArgumentCaptor.getAllValues());
    }

    @SuppressWarnings("unchecked")
    private ResultRowMapper<LocalDateTime> createDateTimeResultRowMapper()
            throws Exception {
        final Class<?> rowMapperClass = forName(CLASS_NAME_DATE_TIME_RESULT_ROW_MAPPER);
        final Constructor<?> rowMapperConstructor = rowMapperClass.getDeclaredConstructor();
        rowMapperConstructor.setAccessible(true);
        try {
            return (ResultRowMapper<LocalDateTime>) rowMapperConstructor.newInstance();
        } finally {
            rowMapperConstructor.setAccessible(false);
        }
    }

    @SuppressWarnings("unchecked")
    private ResultRowMapper<Latitude> createLatitudeResultRowMapper()
            throws Exception {
        when(this.mockedLatitudeBuilderSupplier.get()).thenReturn(this.mockedLatitudeBuilder);
        when(this.mockedLatitudeBuilder.catalogDegrees(anyInt())).thenReturn(this.mockedLatitudeBuilder);
        when(this.mockedLatitudeBuilder.catalogMinutes(anyInt())).thenReturn(this.mockedLatitudeBuilder);
        when(this.mockedLatitudeBuilder.catalogMinuteShare(anyInt())).thenReturn(this.mockedLatitudeBuilder);
        when(this.mockedLatitudeBuilder.catalogType(any(Latitude.Type.class))).thenReturn(this.mockedLatitudeBuilder);
        final Class<?> rowMapperClass = forName(CLASS_NAME_LATITUDE_RESULT_ROW_MAPPER);
        final Constructor<?> rowMapperConstructor = rowMapperClass.getConstructor(Supplier.class);
        return (ResultRowMapper<Latitude>) rowMapperConstructor.newInstance(this.mockedLatitudeBuilderSupplier);
    }

    @SuppressWarnings("unchecked")
    private ResultRowMapper<Longitude> createLongitudeResultRowMapper()
            throws Exception {
        when(this.mockedLongitudeBuilderSupplier.get()).thenReturn(this.mockedLongitudeBuilder);
        when(this.mockedLongitudeBuilder.catalogDegrees(anyInt())).thenReturn(this.mockedLongitudeBuilder);
        when(this.mockedLongitudeBuilder.catalogMinutes(anyInt())).thenReturn(this.mockedLongitudeBuilder);
        when(this.mockedLongitudeBuilder.catalogMinuteShare(anyInt())).thenReturn(this.mockedLongitudeBuilder);
        when(this.mockedLongitudeBuilder.catalogType(any(Longitude.Type.class)))
                .thenReturn(this.mockedLongitudeBuilder);
        final Class<?> rowMapperClass = forName(CLASS_NAME_LONGITUDE_RESULT_ROW_MAPPER);
        final Constructor<?> rowMapperConstructor = rowMapperClass.getConstructor(Supplier.class);
        return (ResultRowMapper<Longitude>) rowMapperConstructor.newInstance(this.mockedLongitudeBuilderSupplier);
    }

    private ResultRowMapper<DataEntity> createDataResultRowMapper()
            throws Exception {
        final Class<? extends ResultRowMapper<DataEntity>> rowMapperClass = DataResultRowMapper.class;
        final Constructor<? extends ResultRowMapper<DataEntity>> rowMapperConstructor
                = rowMapperClass.getDeclaredConstructor(ResultRowMapper.class, ResultRowMapper.class,
                ResultRowMapper.class, Supplier.class);

        when(this.mockedDataBuilderSupplier.get()).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogId(anyLong())).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogDateTime(any(LocalDateTime.class))).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogLatitude(any(Latitude.class))).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogLongitude(any(Longitude.class))).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogSpeed(anyInt())).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogCourse(anyInt())).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogHeight(anyInt())).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogAmountSatellites(anyInt())).thenReturn(this.mockedDataBuilder);

        rowMapperConstructor.setAccessible(true);
        try {
            return rowMapperConstructor.newInstance(this.mockedLocalDateTimeResultRowMapper,
                    this.mockedLatitudeResultRowMapper, this.mockedLongitudeResultRowMapper,
                    this.mockedDataBuilderSupplier);
        } finally {
            rowMapperConstructor.setAccessible(false);
        }
    }
}
