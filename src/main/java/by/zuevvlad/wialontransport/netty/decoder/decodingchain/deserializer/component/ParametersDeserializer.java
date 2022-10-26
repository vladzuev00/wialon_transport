package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.builder.ParameterBuilder;
import by.zuevvlad.wialontransport.entity.ExtendedData.Parameter;
import by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;

import java.util.List;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType.NOT_DEFINED;
import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType.findByValue;
import static java.lang.Byte.parseByte;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public final class ParametersDeserializer implements Deserializer<List<Parameter>> {
    private static final String DELIMITER_PARAMETERS = ",";

    private final Deserializer<Parameter> parameterDeserializer;

    public static Deserializer<List<Parameter>> create() {
        return SingletonHolder.PARAMETERS_DESERIALIZER;
    }

    private ParametersDeserializer(final Deserializer<Parameter> parameterDeserializer) {
        this.parameterDeserializer = parameterDeserializer;
    }

    @Override
    public List<Parameter> deserialize(final String deserialized) {
        return stream(deserialized.split(DELIMITER_PARAMETERS))
                .map(this.parameterDeserializer::deserialize)
                .collect(toList());
    }

    private static final class ParameterDeserializer implements Deserializer<Parameter> {
        private static final String DELIMITER_COMPONENTS = ":";
        private static final int INDEX_DESERIALIZED_NAME = 0;
        private static final int INDEX_DESERIALIZED_TYPE_VALUE = 1;
        private static final int INDEX_DESERIALIZED_VALUE = 2;

        private final Supplier<ParameterBuilder> parameterBuilderSupplier;

        public ParameterDeserializer(final Supplier<ParameterBuilder> parameterBuilderSupplier) {
            this.parameterBuilderSupplier = parameterBuilderSupplier;
        }

        @Override
        public Parameter deserialize(final String deserialized) {
            final String[] deserializedComponents = deserialized.split(DELIMITER_COMPONENTS);
            final String name = deserializedComponents[INDEX_DESERIALIZED_NAME];
            final ValueType valueType = deserializeValueType(deserializedComponents);
            final Object value = valueType.parseValue(deserializedComponents[INDEX_DESERIALIZED_VALUE]);

            final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
            return parameterBuilder
                    .catalogName(name)
                    .catalogValueType(valueType)
                    .catalogValue(value)
                    .build();
        }

        private static ValueType deserializeValueType(final String[] deserializedComponents) {
            try {
                final String deserializedTypeValue = deserializedComponents[INDEX_DESERIALIZED_TYPE_VALUE];
                final byte typeValue = parseByte(deserializedTypeValue);
                return findByValue(typeValue);
            } catch (final NumberFormatException numberFormatException) {
                return NOT_DEFINED;
            }
        }
    }

    private static final class SingletonHolder {
        private static final Deserializer<List<Parameter>> PARAMETERS_DESERIALIZER = new ParametersDeserializer(
                new ParameterDeserializer(ParameterBuilder::new));
    }
}
