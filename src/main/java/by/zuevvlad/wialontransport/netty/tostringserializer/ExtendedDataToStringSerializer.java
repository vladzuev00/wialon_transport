package by.zuevvlad.wialontransport.netty.tostringserializer;

import by.zuevvlad.wialontransport.entity.Data;
import by.zuevvlad.wialontransport.entity.ExtendedData;
import by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType;
import by.zuevvlad.wialontransport.propertyfilereader.PropertyFileReader;
import by.zuevvlad.wialontransport.propertyfilereader.PropertyFileReaderImplementation;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static java.lang.Double.compare;
import static by.zuevvlad.wialontransport.entity.ExtendedData.*;
import static java.lang.String.join;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public final class ExtendedDataToStringSerializer implements ToStringSerializer<ExtendedData> {
    private final ToStringSerializer<Data> dataToStringSerializer;
    private final String serializedNotDefinedValue;
    private final String extendedDataComponentsDelimiter;
    private final String analogInputsDelimiter;
    private final String parameterComponentsDelimiter;
    private final String parametersDelimiter;

    public static ToStringSerializer<ExtendedData> create() {
        return SingletonInitializer.EXTENDED_DATA_TO_STRING_SERIALIZER;
    }

    private ExtendedDataToStringSerializer(final ToStringSerializer<Data> dataToStringSerializer,
                                           final String serializedNotDefinedValue,
                                           final String extendedDataComponentsDelimiter,
                                           final String analogInputsDelimiter,
                                           final String parameterComponentsDelimiter,
                                           final String parametersDelimiter) {
        this.dataToStringSerializer = dataToStringSerializer;
        this.serializedNotDefinedValue = serializedNotDefinedValue;
        this.extendedDataComponentsDelimiter = extendedDataComponentsDelimiter;
        this.analogInputsDelimiter = analogInputsDelimiter;
        this.parameterComponentsDelimiter = parameterComponentsDelimiter;
        this.parametersDelimiter = parametersDelimiter;
    }

    @Override
    public String serialize(final ExtendedData serializedExtendedData) {
        final String serializedAsData = this.dataToStringSerializer.serialize(serializedExtendedData);
        final String serializedReductionPrecision = this.findSerializedReductionPrecision(serializedExtendedData);
        final String serializedInputs = this.findSerializedInputs(serializedExtendedData);
        final String serializedOutputs = this.findSerializedOutputs(serializedExtendedData);
        final String serializedAnalogInputs = this.findSerializedAnalogInputs(serializedExtendedData);
        final String serializedDriverKeyCode = this.findSerializedDriverKeyCode(serializedExtendedData);
        final String serializedParameters = this.findSerializedParameters(serializedExtendedData);
        return join(this.extendedDataComponentsDelimiter, serializedAsData, serializedReductionPrecision,
                serializedInputs, serializedOutputs, serializedAnalogInputs, serializedDriverKeyCode,
                serializedParameters);
    }

    private String findSerializedReductionPrecision(final ExtendedData serializedExtendedData) {
        final double reductionPrecision = serializedExtendedData.getReductionPrecision();
        return compare(reductionPrecision, NOT_DEFINED_REDUCTION_PRECISION) != 0
                ? Double.toString(reductionPrecision)
                : this.serializedNotDefinedValue;
    }

    private String findSerializedInputs(final ExtendedData serializedExtendedData) {
        final int inputs = serializedExtendedData.getInputs();
        return inputs != NOT_DEFINED_INPUTS
                ? Integer.toString(inputs)
                : this.serializedNotDefinedValue;
    }

    private String findSerializedOutputs(final ExtendedData serializedExtendedData) {
        final int outputs = serializedExtendedData.getOutputs();
        return outputs != NOT_DEFINED_OUTPUTS
                ? Integer.toString(outputs)
                : this.serializedNotDefinedValue;
    }

    private String findSerializedAnalogInputs(final ExtendedData serializedExtendedData) {
        final double[] analogInputs = serializedExtendedData.getAnalogInputs();
        return stream(analogInputs)
                .mapToObj(Double::toString)
                .collect(joining(this.analogInputsDelimiter));
    }

    private String findSerializedDriverKeyCode(final ExtendedData serializedExtendedData) {
        final String driverKeyCode = serializedExtendedData.getDriverKeyCode();
        return !driverKeyCode.equals(NOT_DEFINED_DRIVER_KEY_CODE)
                ? driverKeyCode
                : this.serializedNotDefinedValue;
    }

    private String findSerializedParameters(final ExtendedData serializedExtendedData) {
        final List<Parameter> parameters = serializedExtendedData.getParameters();
        return parameters.stream()
                .map(this::findSerializedParameter)
                .collect(joining(this.parametersDelimiter));
    }

    private String findSerializedParameter(final Parameter parameter) {
        final StringBuilder serializedParameterBuilder = new StringBuilder();
        serializedParameterBuilder.append(parameter.getName());
        serializedParameterBuilder.append(this.parameterComponentsDelimiter);

        final ValueType valueType = parameter.getValueType();
        serializedParameterBuilder.append(valueType.getValue());

        serializedParameterBuilder.append(this.parameterComponentsDelimiter);
        serializedParameterBuilder.append(parameter.getValue());
        return serializedParameterBuilder.toString();
    }

    private static final class SingletonInitializer {
        private static final String FILE_PATH_NETTY_SERIALIZATION_CONFIGURATION
                = "./src/main/resources/netty/netty_serialization.properties";
        private static final File FILE_NETTY_SERIALIZATION_CONFIGURATION
                = new File(FILE_PATH_NETTY_SERIALIZATION_CONFIGURATION);

        private static final String PROPERTY_KEY_SERIALIZED_NOT_DEFINED_VALUE
                = "netty.serialization.extended_data.serialized_not_defined_value";
        private static final String PROPERTY_KEY_SERIALIZED_COMPONENTS_DELIMITER
                = "netty.serialization.extended_data.serialized_components_delimiter";
        private static final String PROPERTY_KEY_SERIALIZED_ANALOG_INPUTS_DELIMITER
                = "netty.serialization.extended_data.serialized_analog_inputs_delimiter";
        private static final String PROPERTY_KEY_SERIALIZED_PARAMETER_COMPONENTS_DELIMITER
                = "netty.serialization.extended_data.serialized_parameter_components_delimiter";
        private static final String PROPERTY_KEY_SERIALIZED_PARAMETERS_DELIMITER
                = "netty.serialization.extended_data.serialized_parameters_delimiter";

        private static final ToStringSerializer<ExtendedData> EXTENDED_DATA_TO_STRING_SERIALIZER
                = createExtendedDataToStringSerializer();

        private static ToStringSerializer<ExtendedData> createExtendedDataToStringSerializer() {
            final ToStringSerializer<Data> dataToStringSerializer = DataToStringSerializer.create();

            final PropertyFileReader propertyFileReader = PropertyFileReaderImplementation.create();
            final Properties properties = propertyFileReader.read(FILE_NETTY_SERIALIZATION_CONFIGURATION);
            final String serializedNotDefinedValue = properties.getProperty(PROPERTY_KEY_SERIALIZED_NOT_DEFINED_VALUE);
            final String extendedDataComponentsDelimiter = properties
                    .getProperty(PROPERTY_KEY_SERIALIZED_COMPONENTS_DELIMITER);
            final String analogInputsDelimiter = properties
                    .getProperty(PROPERTY_KEY_SERIALIZED_ANALOG_INPUTS_DELIMITER);
            final String parameterComponentsDelimiter = properties
                    .getProperty(PROPERTY_KEY_SERIALIZED_PARAMETER_COMPONENTS_DELIMITER);
            final String parametersDelimiter = properties.getProperty(PROPERTY_KEY_SERIALIZED_PARAMETERS_DELIMITER);

            return new ExtendedDataToStringSerializer(dataToStringSerializer, serializedNotDefinedValue,
                    extendedDataComponentsDelimiter, analogInputsDelimiter, parameterComponentsDelimiter,
                    parametersDelimiter);
        }
    }
}
