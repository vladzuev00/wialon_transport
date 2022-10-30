package by.zuevvlad.wialontransport.builder;

import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter;
import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter.ValueType;
import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter.ValueType.NOT_DEFINED;

public final class ParameterBuilder implements Builder<Parameter> {
    private static final String NOT_DEFINED_VALUE_NAME = "not defined";
    private static final ValueType NOT_DEFINED_VALUE_VALUE_TYPE = NOT_DEFINED;

    private String name;
    private ValueType valueType;
    private Object value;

    public ParameterBuilder() {
        this.name = NOT_DEFINED_VALUE_NAME;
        this.valueType = NOT_DEFINED_VALUE_VALUE_TYPE;
        this.value = NOT_DEFINED;
    }

    public ParameterBuilder catalogName(final String name) {
        this.name = name;
        return this;
    }

    public ParameterBuilder catalogValueType(final ValueType valueType) {
        this.valueType = valueType;
        return this;
    }

    public ParameterBuilder catalogValue(final Object value) {
        this.value = value;
        return this;
    }

    @Override
    public Parameter build() {
        return new Parameter(this.name, this.valueType, this.value);
    }
}
