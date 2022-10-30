package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.builder.entity.ExtendedDataBuilder;
import by.zuevvlad.wialontransport.entity.DataEntity;
import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;

import java.util.List;
import java.util.function.Supplier;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.String.join;
import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.*;
import static java.util.Collections.emptyList;

public final class ExtendedDataDeserializer implements Deserializer<ExtendedDataEntity> {
    private static final String COMPONENTS_DELIMITER = ";";
    private static final String NOT_DEFINED_SERIALIZED_COMPONENT = "NA";
    private static final String EMPTY_STRING = "";

    private static final int INDEX_DESERIALIZED_REDUCTION_PRECISION = 10;
    private static final int INDEX_DESERIALIZED_INPUTS = 11;
    private static final int INDEX_DESERIALIZED_OUTPUTS = 12;
    private static final int INDEX_DESERIALIZED_ANALOG_INPUTS = 13;
    private static final int INDEX_DESERIALIZED_DRIVER_KEY_CODE = 14;
    private static final int INDEX_DESERIALIZED_PARAMETERS = 15;

    private final Deserializer<DataEntity> dataDeserializer;
    private final Deserializer<double[]> analogInputsDeserializer;
    private final Deserializer<List<Parameter>> parametersDeserializer;
    private final Supplier<ExtendedDataBuilder> extendedDataBuilderSupplier;

    public static Deserializer<ExtendedDataEntity> create() {
        return SingletonHolder.EXTENDED_DATA_DESERIALIZER;
    }

    private ExtendedDataDeserializer(final Deserializer<DataEntity> dataDeserializer,
                                     final Deserializer<double[]> analogInputsDeserializer,
                                     final Deserializer<List<Parameter>> parametersDeserializer,
                                     final Supplier<ExtendedDataBuilder> extendedDataBuilderSupplier) {
        this.dataDeserializer = dataDeserializer;
        this.analogInputsDeserializer = analogInputsDeserializer;
        this.parametersDeserializer = parametersDeserializer;
        this.extendedDataBuilderSupplier = extendedDataBuilderSupplier;
    }

    @Override
    public ExtendedDataEntity deserialize(final String deserialized) {
        final String[] deserializedComponents = deserialized.split(COMPONENTS_DELIMITER, -1);
        final DataEntity data = this.deserializeData(deserializedComponents);
        final double reductionPrecision = deserializeReductionPrecision(deserializedComponents);
        final int inputs = deserializeInputs(deserializedComponents);
        final int outputs = deserializeOutputs(deserializedComponents);
        final double[] analogInputs = this.deserializeAnalogInputs(deserializedComponents);
        final String driverKeyCode = deserializeDriverKeyCode(deserializedComponents);
        final List<Parameter> parameters = this.deserializeParameters(deserializedComponents);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        return extendedDataBuilder
                .catalogData(data)
                .catalogReductionPrecision(reductionPrecision)
                .catalogInputs(inputs)
                .catalogOutputs(outputs)
                .catalogAnalogInputs(analogInputs)
                .catalogDriverKeyCode(driverKeyCode)
                .catalogParameters(parameters)
                .build();
    }

    private DataEntity deserializeData(final String[] deserializedComponents) {
//        final String deserializedData = join(COMPONENTS_DELIMITER,
//                deserializedComponents[INDEX_DESERIALIZED_DATE],
//                deserializedComponents[INDEX_DESERIALIZED_TIME],
//                deserializedComponents[INDEX_DESERIALIZED_LATITUDE_COMPONENTS],
//                deserializedComponents[INDEX_DESERIALIZED_LATITUDE_TYPE],
//                deserializedComponents[INDEX_DESERIALIZED_LONGITUDE_COMPONENTS],
//                deserializedComponents[INDEX_DESERIALIZED_LONGITUDE_TYPE],
//                deserializedComponents[INDEX_DESERIALIZED_SPEED],
//                deserializedComponents[INDEX_DESERIALIZED_COURSE],
//                deserializedComponents[INDEX_DESERIALIZED_HEIGHT],
//                deserializedComponents[INDEX_DESERIALIZED_AMOUNT_SATELLITES]);
//        return this.dataDeserializer.deserialize(deserializedData);
        return null;
    }

    private static double deserializeReductionPrecision(final String[] deserializedComponents) {
        final String deserializedReductionPrecision = deserializedComponents[INDEX_DESERIALIZED_REDUCTION_PRECISION];
        return !deserializedReductionPrecision.equals(NOT_DEFINED_SERIALIZED_COMPONENT)
                ? parseDouble(deserializedReductionPrecision)
                : NOT_DEFINED_REDUCTION_PRECISION;
    }

    private static int deserializeInputs(final String[] deserializedComponents) {
        final String deserializedInputs = deserializedComponents[INDEX_DESERIALIZED_INPUTS];
        return !deserializedInputs.equals(NOT_DEFINED_SERIALIZED_COMPONENT)
                ? parseInt(deserializedInputs)
                : NOT_DEFINED_INPUTS;
    }

    private static int deserializeOutputs(final String[] deserializedComponents) {
        final String deserializedOutputs = deserializedComponents[INDEX_DESERIALIZED_OUTPUTS];
        return !deserializedOutputs.equals(NOT_DEFINED_SERIALIZED_COMPONENT)
                ? parseInt(deserializedOutputs)
                : NOT_DEFINED_OUTPUTS;
    }

    private double[] deserializeAnalogInputs(final String[] deserializedComponents) {
        final String deserializedAnalogInputs = deserializedComponents[INDEX_DESERIALIZED_ANALOG_INPUTS];
        return this.analogInputsDeserializer.deserialize(deserializedAnalogInputs);
    }

    private static String deserializeDriverKeyCode(final String[] deserializedComponents) {
        final String deserializedDriverKeyCode = deserializedComponents[INDEX_DESERIALIZED_DRIVER_KEY_CODE];
        return !deserializedDriverKeyCode.equals(NOT_DEFINED_SERIALIZED_COMPONENT)
                ? deserializedDriverKeyCode
                : NOT_DEFINED_DRIVER_KEY_CODE;
    }

    private List<Parameter> deserializeParameters(final String[] deserializedComponents) {
        final String deserializedParameters = deserializedComponents[INDEX_DESERIALIZED_PARAMETERS];
        return !deserializedParameters.equals(EMPTY_STRING)
                ? this.parametersDeserializer.deserialize(deserializedParameters)
                : emptyList();
    }

    private static final class SingletonHolder {
        private static final Deserializer<ExtendedDataEntity> EXTENDED_DATA_DESERIALIZER = new ExtendedDataDeserializer(
                DataDeserializer.create(), AnalogInputsDeserializer.create(), ParametersDeserializer.create(),
                ExtendedDataBuilder::new);
    }
}
