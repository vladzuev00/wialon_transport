package by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper;

import by.zuevvlad.wialontransport.builder.ParameterBuilder;
import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.entity.ExtendedDataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.dao.resultsetmapper.exception.ResultSetMappingException;
import by.zuevvlad.wialontransport.entity.Data;
import by.zuevvlad.wialontransport.entity.ExtendedData;
import by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Constructor;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.Data.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.EAST;
import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType.*;
import static java.lang.Class.forName;
import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter;
import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public final class ExtendedDataResultRowMapperTest {
    private static final String CLASS_NAME_FROM_STRING_PARAMETER_DESERIALIZER
            = "by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ExtendedDataResultRowMapper"
            + "$FromStringParametersDeserializer$FromStringParameterDeserializer";

    private static final String CLASS_NAME_FROM_STRING_PARAMETERS_DESERIALIZER
            = "by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ExtendedDataResultRowMapper"
            + "$FromStringParametersDeserializer";

    private static final String COLUMN_NAME_REDUCTION_PRECISION = "reduction_precision";
    private static final String COLUMN_NAME_INPUTS = "inputs";
    private static final String COLUMN_NAME_OUTPUTS = "outputs";
    private static final String COLUMN_NAME_ANALOG_INPUTS = "analog_inputs";
    private static final String COLUMN_NAME_DRIVER_KEY_CODE = "driver_key_code";
    private static final String COLUMN_NAME_PARAMETERS = "parameters";

    private final Supplier<ParameterBuilder> parameterBuilderSupplier;
    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;
    private final Supplier<ExtendedDataBuilder> extendedDataBuilderSupplier;

    @Mock
    private Supplier<ParameterBuilder> mockedParameterBuilderSupplier;

    @Mock
    private ParameterBuilder mockedParameterBuilder;

    @Mock
    private FromStringDeserializer<Parameter> mockedFromStringParameterDeserializer;

    @Mock
    private Supplier<ExtendedDataBuilder> mockedExtendedDataBuilderSupplier;

    @Mock
    private ExtendedDataBuilder mockedExtendedDataBuilder;

    @Mock
    private FromStringDeserializer<List<Parameter>> mockedFromStringParametersDeserializer;

    @Mock
    private ResultSet mockedResultSet;

    @Mock
    private ResultRowMapper<Data> mockedDataResultRowMapper;

    @Mock
    private Array mockedArray;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<ValueType> valueTypeArgumentCaptor;

    @Captor
    private ArgumentCaptor<Object> objectArgumentCaptor;

    @Captor
    private ArgumentCaptor<ResultSet> resultSetArgumentCaptor;

    @Captor
    private ArgumentCaptor<Data> dataArgumentCaptor;

    @Captor
    private ArgumentCaptor<Double> doubleArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Captor
    private ArgumentCaptor<double[]> doubleArrayArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<Parameter>> parametersArgumentCaptor;

    public ExtendedDataResultRowMapperTest() {
        this.parameterBuilderSupplier = ParameterBuilder::new;
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.extendedDataBuilderSupplier = ExtendedDataBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
    }

    @Test
    public void parameterShouldBeDeserialized()
            throws Exception {
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final Parameter expected = parameterBuilder
                .catalogName("parameter")
                .catalogValueType(INTEGER)
                .catalogValue(643435)
                .build();
        when(this.mockedParameterBuilder.build()).thenReturn(expected);

        final FromStringDeserializer<Parameter> fromStringParameterDeserializer
                = this.createFromStringParameterDeserializer();
        final String deserialized = "parameter:1:643435";
        fromStringParameterDeserializer.deserialize(deserialized);

        verify(this.mockedParameterBuilderSupplier, times(1)).get();

        verify(this.mockedParameterBuilder, times(1))
                .catalogName(this.stringArgumentCaptor.capture());
        assertEquals(expected.getName(), this.stringArgumentCaptor.getValue());

        verify(this.mockedParameterBuilder, times(1))
                .catalogValueType(this.valueTypeArgumentCaptor.capture());
        assertSame(expected.getValueType(), this.valueTypeArgumentCaptor.getValue());

        verify(this.mockedParameterBuilder, times(1))
                .catalogValue(this.objectArgumentCaptor.capture());
        assertEquals(expected.getValue(), this.objectArgumentCaptor.getValue());

        verify(this.mockedParameterBuilder, times(1)).build();
    }

    @Test
    public void parametersShouldBeDeserialized()
            throws Exception {
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final List<Parameter> expected = List.of(
                parameterBuilder
                        .catalogName("integer-parameter")
                        .catalogValueType(INTEGER)
                        .catalogValue(643435)
                        .build(),
                parameterBuilder
                        .catalogName("double-parameter")
                        .catalogValueType(DOUBLE)
                        .catalogValue(1.65655)
                        .build(),
                parameterBuilder
                        .catalogName("string-parameter")
                        .catalogValueType(STRING)
                        .catalogValue("value")
                        .build());

        when(this.mockedFromStringParameterDeserializer.deserialize(anyString()))
                .thenReturn(expected.get(0))
                .thenReturn(expected.get(1))
                .thenReturn(expected.get(2));

        final FromStringDeserializer<List<Parameter>> fromStringParametersDeserializer
                = this.createFromStringParametersDeserializer();
        final String deserialized = "integer-parameter:1:643435,double-parameter:2:1.65655,string-parameter:3:value";
        final List<Parameter> actual = fromStringParametersDeserializer.deserialize(deserialized);
        assertEquals(expected, actual);

        verify(this.mockedFromStringParameterDeserializer, times(3))
                .deserialize(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments
                = List.of("integer-parameter:1:643435", "double-parameter:2:1.65655", "string-parameter:3:value");
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test
    public void extendedDataShouldBeMapped()
            throws Exception {
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();

        final Data expectedData = dataBuilder
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
        final ExtendedData expected = extendedDataBuilder
                .catalogData(expectedData)
                .catalogReductionPrecision(32.32)
                .catalogInputs(33)
                .catalogOutputs(34)
                .catalogAnalogInputs(new double[]{1.4, 532.23, -23.334434433434})
                .catalogDriverKeyCode("driver key code")
                .catalogParameters(List.of(
                        new Parameter("parameter", INTEGER, 643435),
                        new Parameter("parameter", DOUBLE, 563434.34453),
                        new Parameter("parameter", STRING, "parameter:value"),
                        new Parameter("parameter", NOT_DEFINED, null)
                ))
                .build();

        when(this.mockedDataResultRowMapper.map(any(ResultSet.class))).thenReturn(expectedData);
        when(this.mockedResultSet.getDouble(anyString())).thenReturn(expected.getReductionPrecision());
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(expected.getInputs())
                .thenReturn(expected.getOutputs());

        when(this.mockedResultSet.getArray(anyString())).thenReturn(this.mockedArray);
        when(this.mockedArray.getArray()).thenReturn(expected.getAnalogInputs());

        when(this.mockedResultSet.getString(anyString()))
                .thenReturn(expected.getDriverKeyCode())
                .thenReturn("parameter:1:643435,     "
                        + "parameter:2:563434.34453      ,parameter:3:parameter:value, parameter:4:parameter:value");
        when(this.mockedFromStringParametersDeserializer.deserialize(anyString())).thenReturn(expected.getParameters());

        when(this.mockedExtendedDataBuilder.build()).thenReturn(expected);

        final ResultRowMapper<ExtendedData> extendedDataResultRowMapper = this.createExtendedDataRowMapper();
        final ExtendedData actual = extendedDataResultRowMapper.map(this.mockedResultSet);
        assertEquals(expected, actual);

        verify(this.mockedDataResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());

        verify(this.mockedResultSet, times(1)).getDouble(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(2)).getInt(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getArray(this.stringArgumentCaptor.capture());
        verify(this.mockedArray, times(1)).getArray();
        verify(this.mockedResultSet, times(2)).getString(this.stringArgumentCaptor.capture());
        verify(this.mockedFromStringParametersDeserializer, times(1))
                .deserialize(this.stringArgumentCaptor.capture());

        verify(this.mockedExtendedDataBuilderSupplier, times(1)).get();
        verify(this.mockedExtendedDataBuilder, times(1))
                .catalogData(this.dataArgumentCaptor.capture());
        verify(this.mockedExtendedDataBuilder, times(1))
                .catalogReductionPrecision(this.doubleArgumentCaptor.capture());
        verify(this.mockedExtendedDataBuilder, times(1))
                .catalogInputs(this.integerArgumentCaptor.capture());
        verify(this.mockedExtendedDataBuilder, times(1))
                .catalogOutputs(this.integerArgumentCaptor.capture());
        verify(this.mockedExtendedDataBuilder, times(1))
                .catalogAnalogInputs(this.doubleArrayArgumentCaptor.capture());
        verify(this.mockedExtendedDataBuilder, times(1))
                .catalogDriverKeyCode(this.stringArgumentCaptor.capture());
        verify(this.mockedExtendedDataBuilder, times(1))
                .catalogParameters(this.parametersArgumentCaptor.capture());
        verify(this.mockedExtendedDataBuilder, times(1)).build();

        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());

        final List<String> expectedCapturedStringArguments = List.of(COLUMN_NAME_REDUCTION_PRECISION,
                COLUMN_NAME_INPUTS, COLUMN_NAME_OUTPUTS, COLUMN_NAME_ANALOG_INPUTS, COLUMN_NAME_DRIVER_KEY_CODE,
                COLUMN_NAME_PARAMETERS, expected.getDriverKeyCode());
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());

        assertSame(expectedData, this.dataArgumentCaptor.getValue());

        assertEquals(expected.getReductionPrecision(), this.doubleArgumentCaptor.getValue(), 0);

        final List<Integer> expectedCapturedIntegerArguments = List.of(expected.getInputs(), expected.getOutputs());
        assertEquals(expectedCapturedIntegerArguments, this.integerArgumentCaptor.getAllValues());

        assertSame(expected.getAnalogInputs(), this.doubleArrayArgumentCaptor.getValue());

        assertSame(expected.getParameters(), this.parametersArgumentCaptor.getValue());
    }

    @Test(expected = ResultSetMappingException.class)
    public void extendedDataShouldNotBeMappedBecauseOfData()
            throws Exception {
        when(this.mockedDataResultRowMapper.map(any(ResultSet.class))).thenThrow(ResultSetMappingException.class);

        final ResultRowMapper<ExtendedData> extendedDataResultRowMapper = this.createExtendedDataRowMapper();
        extendedDataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedDataResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());
    }

    @Test(expected = ResultSetMappingException.class)
    public void extendedDataShouldNotBeMappedBecauseOfReductionPrecision()
            throws Exception {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        when(this.mockedDataResultRowMapper.map(any(ResultSet.class))).thenReturn(dataBuilder.build());
        when(this.mockedResultSet.getDouble(anyString())).thenThrow(SQLException.class);

        final ResultRowMapper<ExtendedData> extendedDataResultRowMapper = this.createExtendedDataRowMapper();
        extendedDataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedDataResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getDouble(this.stringArgumentCaptor.capture());

        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());
        assertEquals(COLUMN_NAME_REDUCTION_PRECISION, this.stringArgumentCaptor.getValue());
    }

    @Test(expected = ResultSetMappingException.class)
    public void extendedDataShouldNotMappedBecauseOfInputs()
            throws Exception {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        when(this.mockedDataResultRowMapper.map(any(ResultSet.class))).thenReturn(dataBuilder.build());
        when(this.mockedResultSet.getDouble(anyString())).thenReturn(0.);
        when(this.mockedResultSet.getInt(anyString())).thenThrow(SQLException.class);

        final ResultRowMapper<ExtendedData> extendedDataResultRowMapper = this.createExtendedDataRowMapper();
        extendedDataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedDataResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getDouble(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getInt(this.stringArgumentCaptor.capture());

        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());

        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_REDUCTION_PRECISION, COLUMN_NAME_INPUTS);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void extendedDataShouldNotBeMappedBecauseOfOutputs()
            throws Exception {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        when(this.mockedDataResultRowMapper.map(any(ResultSet.class))).thenReturn(dataBuilder.build());
        when(this.mockedResultSet.getDouble(anyString())).thenReturn(0.);
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenThrow(SQLException.class);

        final ResultRowMapper<ExtendedData> extendedDataResultRowMapper = this.createExtendedDataRowMapper();
        extendedDataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedDataResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getDouble(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(2)).getInt(this.stringArgumentCaptor.capture());

        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());

        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_REDUCTION_PRECISION, COLUMN_NAME_INPUTS, COLUMN_NAME_OUTPUTS);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void extendedDataShouldNotBeMappedBecauseOfGettingSqlArrayOfAnalogInputs()
            throws Exception {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        when(this.mockedDataResultRowMapper.map(any(ResultSet.class))).thenReturn(dataBuilder.build());
        when(this.mockedResultSet.getDouble(anyString())).thenReturn(0.);
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenReturn(0);
        when(this.mockedResultSet.getArray(anyString())).thenThrow(SQLException.class);

        final ResultRowMapper<ExtendedData> extendedDataResultRowMapper = this.createExtendedDataRowMapper();
        extendedDataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedDataResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getDouble(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(2)).getInt(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getArray(this.stringArgumentCaptor.capture());

        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());

        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_REDUCTION_PRECISION, COLUMN_NAME_INPUTS, COLUMN_NAME_OUTPUTS,
                COLUMN_NAME_ANALOG_INPUTS);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test
    public void extendedDataShouldNotBeMappedBecauseOfGettingAnalogInputs()
            throws Exception {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        when(this.mockedDataResultRowMapper.map(any(ResultSet.class))).thenReturn(dataBuilder.build());
        when(this.mockedResultSet.getDouble(anyString())).thenReturn(0.);
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenReturn(0);
        when(this.mockedResultSet.getArray(anyString())).thenReturn(this.mockedArray);
        when(this.mockedArray.getArray()).thenThrow(SQLException.class);

        final ResultRowMapper<ExtendedData> extendedDataResultRowMapper = this.createExtendedDataRowMapper();
        extendedDataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedDataResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getDouble(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(2)).getInt(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getArray(this.stringArgumentCaptor.capture());
        verify(this.mockedArray, times(1)).getArray();

        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());

        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_REDUCTION_PRECISION, COLUMN_NAME_INPUTS, COLUMN_NAME_OUTPUTS,
                COLUMN_NAME_ANALOG_INPUTS);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void extendedDataShouldNotBeMappedBecauseOfDriverKeyCode()
            throws Exception {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        when(this.mockedDataResultRowMapper.map(any(ResultSet.class))).thenReturn(dataBuilder.build());
        when(this.mockedResultSet.getDouble(anyString())).thenReturn(0.);
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenReturn(0);
        when(this.mockedResultSet.getArray(anyString())).thenReturn(this.mockedArray);
        when(this.mockedArray.getArray()).thenReturn(new double[0]);
        when(this.mockedResultSet.getString(anyString())).thenThrow(SQLException.class);

        final ResultRowMapper<ExtendedData> extendedDataResultRowMapper = this.createExtendedDataRowMapper();
        extendedDataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedDataResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getDouble(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(2)).getInt(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getArray(this.stringArgumentCaptor.capture());
        verify(this.mockedArray, times(1)).getArray();
        verify(this.mockedResultSet, times(1)).getString(this.stringArgumentCaptor.capture());

        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());

        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_REDUCTION_PRECISION, COLUMN_NAME_INPUTS, COLUMN_NAME_OUTPUTS,
                COLUMN_NAME_ANALOG_INPUTS, COLUMN_NAME_DRIVER_KEY_CODE);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void extendedDataShouldNotBeMappedBecauseOfParameters()
            throws Exception {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        when(this.mockedDataResultRowMapper.map(any(ResultSet.class))).thenReturn(dataBuilder.build());
        when(this.mockedResultSet.getDouble(anyString())).thenReturn(0.);
        when(this.mockedResultSet.getInt(anyString()))
                .thenReturn(0)
                .thenReturn(0);
        when(this.mockedResultSet.getArray(anyString())).thenReturn(this.mockedArray);
        when(this.mockedArray.getArray()).thenReturn(new double[0]);
        when(this.mockedResultSet.getString(anyString()))
                .thenReturn("driver key code")
                .thenThrow(SQLException.class);

        final ResultRowMapper<ExtendedData> extendedDataResultRowMapper = this.createExtendedDataRowMapper();
        extendedDataResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedDataResultRowMapper, times(1))
                .map(this.resultSetArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getDouble(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(2)).getInt(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getArray(this.stringArgumentCaptor.capture());
        verify(this.mockedArray, times(1)).getArray();
        verify(this.mockedResultSet, times(2)).getString(this.stringArgumentCaptor.capture());

        assertSame(this.mockedResultSet, this.resultSetArgumentCaptor.getValue());

        final List<String> expectedCapturedStringArguments
                = List.of(COLUMN_NAME_REDUCTION_PRECISION, COLUMN_NAME_INPUTS, COLUMN_NAME_OUTPUTS,
                COLUMN_NAME_ANALOG_INPUTS, COLUMN_NAME_DRIVER_KEY_CODE, COLUMN_NAME_PARAMETERS);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @SuppressWarnings("unchecked")
    private FromStringDeserializer<Parameter> createFromStringParameterDeserializer()
            throws Exception {
        when(this.mockedParameterBuilderSupplier.get()).thenReturn(this.mockedParameterBuilder);
        when(this.mockedParameterBuilder.catalogName(anyString())).thenReturn(this.mockedParameterBuilder);
        when(this.mockedParameterBuilder.catalogValueType(any(ValueType.class)))
                .thenReturn(this.mockedParameterBuilder);
        when(this.mockedParameterBuilder.catalogValue(any(Object.class))).thenReturn(this.mockedParameterBuilder);

        final Class<?> deserializerClass = forName(CLASS_NAME_FROM_STRING_PARAMETER_DESERIALIZER);
        final Constructor<?> deserializerConstructor = deserializerClass.getConstructor(Supplier.class);
        return (FromStringDeserializer<Parameter>) deserializerConstructor
                .newInstance(this.mockedParameterBuilderSupplier);
    }

    @SuppressWarnings("unchecked")
    private FromStringDeserializer<List<Parameter>> createFromStringParametersDeserializer()
            throws Exception {
        final Class<?> deserializerClass = forName(CLASS_NAME_FROM_STRING_PARAMETERS_DESERIALIZER);
        final Constructor<?> deserializerConstructor = deserializerClass.getConstructor(FromStringDeserializer.class);
        return (FromStringDeserializer<List<Parameter>>) deserializerConstructor
                .newInstance(this.mockedFromStringParameterDeserializer);
    }

    @SuppressWarnings("unchecked")
    private ResultRowMapper<ExtendedData> createExtendedDataRowMapper()
            throws Exception {
        when(this.mockedExtendedDataBuilderSupplier.get()).thenReturn(this.mockedExtendedDataBuilder);
        when(this.mockedExtendedDataBuilder.catalogData(any(Data.class))).thenReturn(this.mockedExtendedDataBuilder);
        when(this.mockedExtendedDataBuilder.catalogReductionPrecision(anyDouble()))
                .thenReturn(this.mockedExtendedDataBuilder);
        when(this.mockedExtendedDataBuilder.catalogInputs(anyInt())).thenReturn(this.mockedExtendedDataBuilder);
        when(this.mockedExtendedDataBuilder.catalogOutputs(anyInt())).thenReturn(this.mockedExtendedDataBuilder);
        when(this.mockedExtendedDataBuilder.catalogAnalogInputs(any(double[].class)))
                .thenReturn(this.mockedExtendedDataBuilder);
        when(this.mockedExtendedDataBuilder.catalogDriverKeyCode(anyString()))
                .thenReturn(this.mockedExtendedDataBuilder);
        when(this.mockedExtendedDataBuilder.catalogParameters(any(List.class)))
                .thenReturn(this.mockedExtendedDataBuilder);

        final Class<? extends ResultRowMapper<ExtendedData>> rowMapperClass = ExtendedDataResultRowMapper.class;
        final Constructor<? extends ResultRowMapper<ExtendedData>> rowMapperConstructor
                = rowMapperClass.getDeclaredConstructor(ResultRowMapper.class, Supplier.class,
                FromStringDeserializer.class);

        rowMapperConstructor.setAccessible(true);
        try {
            return rowMapperConstructor.newInstance(this.mockedDataResultRowMapper,
                    this.mockedExtendedDataBuilderSupplier, this.mockedFromStringParametersDeserializer);
        } finally {
            rowMapperConstructor.setAccessible(false);
        }
    }
}
