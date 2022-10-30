package by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper;

import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter;
import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter.ValueType;
import static java.lang.Byte.parseByte;
import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter.ValueType.findByValue;
import static java.util.Arrays.stream;

//public final class ExtendedDataResultRowMapper implements ResultRowMapper<ExtendedData> {
//    private static final String COLUMN_NAME_REDUCTION_PRECISION = "reduction_precision";
//    private static final String COLUMN_NAME_INPUTS = "inputs";
//    private static final String COLUMN_NAME_OUTPUTS = "outputs";
//    private static final String COLUMN_NAME_ANALOG_INPUTS = "analog_inputs";
//    private static final String COLUMN_NAME_DRIVER_KEY_CODE = "driver_key_code";
//    private static final String COLUMN_NAME_PARAMETERS = "parameters";
//
//    private final ResultRowMapper<Data> dataResultRowMapper;
//    private final Supplier<ExtendedDataBuilder> extendedDataBuilderSupplier;
//    private final FromStringDeserializer<List<Parameter>> fromStringParametersDeserializer;
//
//    public static ResultRowMapper<ExtendedData> create() {
//        return SingletonHolder.EXTENDED_DATA_RESULT_ROW_MAPPER;
//    }
//
//    private ExtendedDataResultRowMapper(final ResultRowMapper<Data> dataResultRowMapper,
//                                        final Supplier<ExtendedDataBuilder> extendedDataBuilderSupplier,
//                                        final FromStringDeserializer<List<Parameter>> fromStringParametersDeserializer) {
//        this.dataResultRowMapper = dataResultRowMapper;
//        this.extendedDataBuilderSupplier = extendedDataBuilderSupplier;
//        this.fromStringParametersDeserializer = fromStringParametersDeserializer;
//    }
//
//    @Override
//    public ExtendedData map(final ResultSet resultSet) {
//        try {
//            final Data data = this.dataResultRowMapper.map(resultSet);
//            final double reductionPrecision = resultSet.getDouble(COLUMN_NAME_REDUCTION_PRECISION);
//            final int inputs = resultSet.getInt(COLUMN_NAME_INPUTS);
//            final int outputs = resultSet.getInt(COLUMN_NAME_OUTPUTS);
//
//            final Array analogInputsSqlArray = resultSet.getArray(COLUMN_NAME_ANALOG_INPUTS);
//            final double[] analogInputs = (double[]) analogInputsSqlArray.getArray();
//
//            final String driverKeyCode = resultSet.getString(COLUMN_NAME_DRIVER_KEY_CODE);
//
//            final String descriptionParameters = resultSet.getString(COLUMN_NAME_PARAMETERS);
//            final List<Parameter> parameters = this.fromStringParametersDeserializer.deserialize(descriptionParameters);
//
//            final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
//            return extendedDataBuilder
//                    .catalogData(data)
//                    .catalogReductionPrecision(reductionPrecision)
//                    .catalogInputs(inputs)
//                    .catalogOutputs(outputs)
//                    .catalogAnalogInputs(analogInputs)
//                    .catalogDriverKeyCode(driverKeyCode)
//                    .catalogParameters(parameters)
//                    .build();
//        } catch (final SQLException cause) {
//            throw new ResultSetMappingException(cause);
//        }
//    }
//
//    private static final class FromStringParametersDeserializer implements FromStringDeserializer<List<Parameter>> {
//        private static final String REGEX_PARAMETERS_DELIMITER = " *, *";
//        private static final String EMPTY_STRING = "";
//
//        private final FromStringDeserializer<Parameter> fromStringParameterDeserializer;
//
//        public FromStringParametersDeserializer(
//                final FromStringDeserializer<Parameter> fromStringParameterDeserializer) {
//            this.fromStringParameterDeserializer = fromStringParameterDeserializer;
//        }
//
//        @Override
//        public List<Parameter> deserialize(final String deserialized) {
//            if (EMPTY_STRING.equals(deserialized)) {
//                return emptyList();
//            }
//            final String[] deserializedParameters = deserialized.split(REGEX_PARAMETERS_DELIMITER);
//            return stream(deserializedParameters)
//                    .map(this.fromStringParameterDeserializer::deserialize)
//                    .collect(toList());
//        }
//
//        private static final class FromStringParameterDeserializer implements FromStringDeserializer<Parameter> {
//            private static final String REGEX_COMPONENTS_DELIMITER = " *: *";
//            private static final int LIMIT_AMOUNT_COMPONENTS = 3;
//
//            private static final int COMPONENT_INDEX_NAME = 0;
//            private static final int COMPONENT_INDEX_VALUE_TYPE = 1;
//            private static final int COMPONENT_INDEX_VALUE = 2;
//
//            private final Supplier<ParameterBuilder> parameterBuilderSupplier;
//
//            public FromStringParameterDeserializer(final Supplier<ParameterBuilder> parameterBuilderSupplier) {
//                this.parameterBuilderSupplier = parameterBuilderSupplier;
//            }
//
//            @Override
//            public Parameter deserialize(final String deserialized) {
//                final String[] deserializedComponents = deserialized
//                        .split(REGEX_COMPONENTS_DELIMITER, LIMIT_AMOUNT_COMPONENTS);
//
//                final String name = deserializedComponents[COMPONENT_INDEX_NAME];
//
//                final byte valueValueType = parseByte(deserializedComponents[COMPONENT_INDEX_VALUE_TYPE]);
//                final ValueType valueType = findByValue(valueValueType);
//
//                final Object value = valueType.parseValue(deserializedComponents[COMPONENT_INDEX_VALUE]);
//
//                final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
//                return parameterBuilder
//                        .catalogName(name)
//                        .catalogValueType(valueType)
//                        .catalogValue(value)
//                        .build();
//            }
//        }
//    }
//
//    private static final class SingletonHolder {
//        private static final ResultRowMapper<ExtendedData> EXTENDED_DATA_RESULT_ROW_MAPPER
//                = new ExtendedDataResultRowMapper(
//                DataResultRowMapper.create(),
//                ExtendedDataBuilder::new,
//                new FromStringParametersDeserializer(
//                        new FromStringParametersDeserializer.FromStringParameterDeserializer(
//                                ParameterBuilder::new)));
//    }
//}
