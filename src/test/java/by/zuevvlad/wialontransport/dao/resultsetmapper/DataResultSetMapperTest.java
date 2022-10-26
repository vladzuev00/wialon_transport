package by.zuevvlad.wialontransport.dao.resultsetmapper;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.dao.resultsetmapper.exception.ResultSetMappingException;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.entity.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

import static java.time.LocalDateTime.now;
import static java.util.List.of;
import static by.zuevvlad.wialontransport.entity.Data.Latitude.Type.*;
import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public final class DataResultSetMapperTest {
    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;

    @Mock
    private ResultRowMapper<Data> mockedDataResultRowMapper;

    @Mock
    private ResultSet mockedResultSet;

    @Captor
    private ArgumentCaptor<ResultSet> resultSetArgumentCaptor;

    public DataResultSetMapperTest() {
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
    }

    @Test
    public void dataListShouldBeMapped()
            throws Exception {
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();

        final List<Data> expected = of(
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
                                .catalogType(SOUTH)
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

        when(this.mockedResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(this.mockedDataResultRowMapper.map(any(ResultSet.class)))
                .thenReturn(expected.get(0))
                .thenReturn(expected.get(1));

        final ResultSetMapper<Data> dataResultSetMapper = this.createDataResultSetMapper();
        final List<Data> actual = dataResultSetMapper.map(this.mockedResultSet);
        assertEquals(expected, actual);

        verify(this.mockedResultSet, times(3)).next();

        verify(this.mockedDataResultRowMapper, times(2)).map(this.resultSetArgumentCaptor.capture());
        final List<ResultSet> expectedCapturedResultSets = of(this.mockedResultSet, this.mockedResultSet);
        assertEquals(expectedCapturedResultSets, this.resultSetArgumentCaptor.getAllValues());
    }

    @Test
    public void mappedDataListShouldBeEmpty()
            throws Exception {
        when(this.mockedResultSet.next()).thenReturn(false);
        final ResultSetMapper<Data> dataResultSetMapper = this.createDataResultSetMapper();
        final List<Data> mappedDataList = dataResultSetMapper.map(this.mockedResultSet);
        assertTrue(mappedDataList.isEmpty());

        verify(this.mockedResultSet, times(1)).next();
    }

    @Test(expected = ResultSetMappingException.class)
    public void dataListShouldNotBeMappedBecauseOfSqlException()
            throws Exception {
        when(this.mockedResultSet.next()).thenThrow(SQLException.class);
        final ResultSetMapper<Data> dataResultSetMapper = this.createDataResultSetMapper();
        dataResultSetMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).next();
    }

    private ResultSetMapper<Data> createDataResultSetMapper()
            throws Exception {
        final Class<? extends ResultSetMapper<Data>> resultSetMapperClass = DataResultSetMapper.class;
        final Constructor<? extends ResultSetMapper<Data>> resultSetMapperConstructor
                = resultSetMapperClass.getDeclaredConstructor(ResultRowMapper.class);
        resultSetMapperConstructor.setAccessible(true);
        try {
            return resultSetMapperConstructor.newInstance(this.mockedDataResultRowMapper);
        } finally {
            resultSetMapperConstructor.setAccessible(false);
        }
    }
}
