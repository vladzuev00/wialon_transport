package by.zuevvlad.wialontransport.dao.serializer;

import by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter;
import by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter.ValueType;

import java.util.List;

import static java.lang.String.join;
import static java.util.stream.Collectors.joining;

public final class ParametersSerializer implements Serializer<List<Parameter>> {
    private static final String PARAMETERS_DELIMITER = ",";

    private final Serializer<Parameter> parameterSerializer;

    public static Serializer<List<Parameter>> create() {
        return SingletonHolder.PARAMETERS_SERIALIZER;
    }

    private ParametersSerializer(final Serializer<Parameter> parameterSerializer) {
        this.parameterSerializer = parameterSerializer;
    }

    @Override
    public String serialize(final List<Parameter> parameters) {
        return parameters.stream()
                .map(this.parameterSerializer::serialize)
                .collect(joining(PARAMETERS_DELIMITER));
    }

    private static final class ParameterSerializer implements Serializer<Parameter> {
        private static final String PARAMETER_COMPONENTS_DELIMITER = ":";

        @Override
        public String serialize(final Parameter parameter) {
            final ValueType valueType = parameter.getValueType();
            return join(PARAMETER_COMPONENTS_DELIMITER, parameter.getName(), Byte.toString(valueType.getValue()),
                    parameter.getValue().toString());
        }
    }

    private static final class SingletonHolder {
        private static final Serializer<List<Parameter>> PARAMETERS_SERIALIZER
                = new ParametersSerializer(new ParameterSerializer());
    }
}
