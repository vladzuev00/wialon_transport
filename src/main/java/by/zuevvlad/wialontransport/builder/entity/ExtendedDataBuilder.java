package by.zuevvlad.wialontransport.builder.entity;

import by.zuevvlad.wialontransport.builder.Builder;
import by.zuevvlad.wialontransport.entity.DataEntity;
import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;

import java.util.List;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter;
import static java.util.Collections.emptyList;

public final class ExtendedDataBuilder implements Builder<ExtendedDataEntity> {
    private static final Supplier<DataEntity> NOT_DEFINED_DATA_SUPPLIER = DataEntity::new;
    private static final double NOT_DEFINED_VALUE_REDUCTION_PRECISION = Double.MIN_VALUE;
    private static final int NOT_DEFINED_VALUE_INPUTS = Integer.MIN_VALUE;
    private static final int NOT_DEFINED_VALUE_OUTPUTS = Integer.MIN_VALUE;
    private static final Supplier<double[]> NOT_DEFINED_VALUE_ANALOG_INPUTS_SUPPLIER = () -> new double[0];
    private static final String NOT_DEFINED_DRIVER_KEY_CODE = "not defined";

    private DataEntity data;
    private double reductionPrecision;
    private int inputs;
    private int outputs;
    private double[] analogInputs;
    private String driverKeyCode;
    private List<Parameter> parameters;

    public ExtendedDataBuilder() {
        this.data = NOT_DEFINED_DATA_SUPPLIER.get();
        this.reductionPrecision = NOT_DEFINED_VALUE_REDUCTION_PRECISION;
        this.inputs = NOT_DEFINED_VALUE_INPUTS;
        this.outputs = NOT_DEFINED_VALUE_OUTPUTS;
        this.analogInputs = NOT_DEFINED_VALUE_ANALOG_INPUTS_SUPPLIER.get();
        this.driverKeyCode = NOT_DEFINED_DRIVER_KEY_CODE;
        this.parameters = emptyList();
    }

    public ExtendedDataBuilder catalogReductionPrecision(final double reductionPrecision) {
        this.reductionPrecision = reductionPrecision;
        return this;
    }

    public ExtendedDataBuilder catalogInputs(final int inputs) {
        this.inputs = inputs;
        return this;
    }

    public ExtendedDataBuilder catalogOutputs(final int outputs) {
        this.outputs = outputs;
        return this;
    }

    public ExtendedDataBuilder catalogAnalogInputs(final double[] analogInputs) {
        this.analogInputs = analogInputs;
        return this;
    }

    public ExtendedDataBuilder catalogDriverKeyCode(final String driverKeyCode) {
        this.driverKeyCode = driverKeyCode;
        return this;
    }

    public ExtendedDataBuilder catalogParameters(final List<Parameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    public ExtendedDataBuilder catalogData(final DataEntity data) {
        this.data = data;
        return this;
    }

    @Override
    public ExtendedDataEntity build() {
        return new ExtendedDataEntity(this.data, this.reductionPrecision, this.inputs, this.outputs,
                this.analogInputs, this.driverKeyCode, this.parameters);
    }
}
