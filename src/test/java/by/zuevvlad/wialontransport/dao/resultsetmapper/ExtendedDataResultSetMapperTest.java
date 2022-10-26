package by.zuevvlad.wialontransport.dao.resultsetmapper;

import by.zuevvlad.wialontransport.builder.ParameterBuilder;
import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.entity.ExtendedDataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.dao.resultsetmapper.exception.ResultSetMappingException;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.entity.ExtendedData;
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

import static by.zuevvlad.wialontransport.entity.Data.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.EAST;
import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertEquals;
import static java.util.List.of;
import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType.INTEGER;
import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType.STRING;
import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType.DOUBLE;
import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType.NOT_DEFINED;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public final class ExtendedDataResultSetMapperTest {
    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;
    private final Supplier<ParameterBuilder> parameterBuilderSupplier;
    private final Supplier<ExtendedDataBuilder> extendedDataBuilderSupplier;

    @Mock
    private ResultSet mockedResultSet;

    @Mock
    private ResultRowMapper<ExtendedData> mockedExtendedDataResultRowMapper;

    @Captor
    private ArgumentCaptor<ResultSet> resultSetArgumentCaptor;

    public ExtendedDataResultSetMapperTest() {
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
        this.parameterBuilderSupplier = ParameterBuilder::new;
        this.extendedDataBuilderSupplier = ExtendedDataBuilder::new;
    }

    @Test
    public void extendedDataShouldBeMapped()
            throws Exception {
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();

        final List<ExtendedData> expected = of(
                extendedDataBuilder
                        .catalogData(dataBuilder
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
                                .build())
                        .catalogReductionPrecision(32.)
                        .catalogInputs(33)
                        .catalogOutputs(34)
                        .catalogAnalogInputs(new double[0])
                        .catalogDriverKeyCode("first driver key code")
                        .catalogParameters(of(
                                parameterBuilder
                                        .catalogName("first parameter")
                                        .catalogValueType(INTEGER)
                                        .catalogValue(36)
                                        .build(),
                                parameterBuilder
                                        .catalogName("second parameter")
                                        .catalogValueType(STRING)
                                        .catalogValue("37")
                                        .build()
                        )).build(),
                extendedDataBuilder
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
                        .catalogAnalogInputs(new double[0])
                        .catalogDriverKeyCode("second driver key code")
                        .catalogParameters(of(
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
                        )).build()
        );

        when(this.mockedResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        when(this.mockedExtendedDataResultRowMapper.map(any(ResultSet.class)))
                .thenReturn(expected.get(0))
                .thenReturn(expected.get(1));

        final ResultSetMapper<ExtendedData> extendedDataResultSetMapper = this.createResultSetMapper();
        final List<ExtendedData> actual = extendedDataResultSetMapper.map(this.mockedResultSet);
        assertEquals(expected, actual);

        verify(this.mockedResultSet, times(3)).next();

        verify(this.mockedExtendedDataResultRowMapper, times(2))
                .map(this.resultSetArgumentCaptor.capture());
        final List<ResultSet> expectedCapturedResultSet = of(this.mockedResultSet, this.mockedResultSet);
        assertEquals(expectedCapturedResultSet, this.resultSetArgumentCaptor.getAllValues());
    }

    @Test
    public void mappedDataListShouldBeEmpty()
            throws Exception {
        when(this.mockedResultSet.next()).thenReturn(false);
        final ResultSetMapper<ExtendedData> extendedDataResultSetMapper = this.createResultSetMapper();
        final List<ExtendedData> mappedExtendedDataList = extendedDataResultSetMapper.map(this.mockedResultSet);
        assertTrue(mappedExtendedDataList.isEmpty());

        verify(this.mockedResultSet, times(1)).next();
    }

    @Test(expected = ResultSetMappingException.class)
    public void extendedDataListShouldNotBeMappedBecauseOfExceptionDuringCallingNextOnResultSet()
            throws Exception {
        when(this.mockedResultSet.next()).thenThrow(SQLException.class);
        final ResultSetMapper<ExtendedData> extendedDataResultSetMapper = this.createResultSetMapper();
        extendedDataResultSetMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).next();
    }

    private ResultSetMapper<ExtendedData> createResultSetMapper()
            throws Exception {
        final Class<? extends ResultSetMapper<ExtendedData>> resultSetMapperClass = ExtendedDataResultSetMapper.class;
        final Constructor<? extends ResultSetMapper<ExtendedData>> resultSetMapperConstructor
                = resultSetMapperClass.getDeclaredConstructor(ResultRowMapper.class);
        resultSetMapperConstructor.setAccessible(true);
        try {
            return resultSetMapperConstructor.newInstance(this.mockedExtendedDataResultRowMapper);
        } finally {
            resultSetMapperConstructor.setAccessible(false);
        }
    }
}
