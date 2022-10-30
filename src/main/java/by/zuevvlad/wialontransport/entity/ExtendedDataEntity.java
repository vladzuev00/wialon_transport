package by.zuevvlad.wialontransport.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

public final class ExtendedDataEntity extends DataEntity {
    public static final double NOT_DEFINED_REDUCTION_PRECISION = Double.MIN_VALUE;
    public static final int NOT_DEFINED_INPUTS = Integer.MIN_VALUE;
    public static final int NOT_DEFINED_OUTPUTS = Integer.MIN_VALUE;
    public static final Supplier<double[]> NOT_DEFINED_VALUE_ANALOG_INPUTS_SUPPLIER = () -> new double[0];
    public static final String NOT_DEFINED_DRIVER_KEY_CODE = "not defined";

    private double reductionPrecision;
    private int inputs;
    private int outputs;
    private double[] analogInputs;
    private String driverKeyCode;

    public ExtendedDataEntity() {
        this.reductionPrecision = NOT_DEFINED_REDUCTION_PRECISION;
        this.inputs = NOT_DEFINED_INPUTS;
        this.outputs = NOT_DEFINED_OUTPUTS;
        this.analogInputs = NOT_DEFINED_VALUE_ANALOG_INPUTS_SUPPLIER.get();
        this.driverKeyCode = NOT_DEFINED_DRIVER_KEY_CODE;
    }

    public ExtendedDataEntity(final long id, final LocalDateTime dateTime, final Latitude latitude,
                              final Longitude longitude, final int speed, final int course, final int height,
                              final int amountSatellites, final TrackerEntity tracker, final double reductionPrecision,
                              final int inputs, final int outputs, final double[] analogInputs,
                              final String driverKeyCode) {
        super(id, dateTime, latitude, longitude, speed, course, height, amountSatellites, tracker);
        this.reductionPrecision = reductionPrecision;
        this.inputs = inputs;
        this.outputs = outputs;
        this.analogInputs = analogInputs;
        this.driverKeyCode = driverKeyCode;
    }

    public void setReductionPrecision(final double reductionPrecision) {
        this.reductionPrecision = reductionPrecision;
    }

    public double getReductionPrecision() {
        return this.reductionPrecision;
    }

    public void setInputs(final int inputs) {
        this.inputs = inputs;
    }

    public int getInputs() {
        return this.inputs;
    }

    public void setOutputs(final int outputs) {
        this.outputs = outputs;
    }

    public int getOutputs() {
        return this.outputs;
    }

    public void setAnalogInputs(final double[] analogInputs) {
        this.analogInputs = analogInputs;
    }

    public double[] getAnalogInputs() {
        return this.analogInputs;
    }

    public void setDriverKeyCode(final String driverKeyCode) {
        this.driverKeyCode = driverKeyCode;
    }

    public String getDriverKeyCode() {
        return this.driverKeyCode;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (!super.equals(otherObject)) {
            return false;
        }
        final ExtendedDataEntity other = (ExtendedDataEntity) otherObject;
        return this.reductionPrecision == other.reductionPrecision
                && this.inputs == other.inputs
                && this.outputs == other.outputs
                && Arrays.equals(this.analogInputs, other.analogInputs)
                && Objects.equals(this.driverKeyCode, other.driverKeyCode);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(this.reductionPrecision, this.inputs, this.outputs, this.driverKeyCode)
                + Arrays.hashCode(this.analogInputs);
    }

    @Override
    public String toString() {
        return super.toString() + "[reductionPrecision = " + this.reductionPrecision + ", inputs = " + this.inputs
                + ", outputs = " + this.outputs + ", analogInputs = " + Arrays.toString(this.analogInputs)
                + ", driverKeyCode = " + this.driverKeyCode + "]";
    }

    @Override
    public void writeExternal(final ObjectOutput objectOutput)
            throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeDouble(this.reductionPrecision);
        objectOutput.writeInt(this.inputs);
        objectOutput.writeInt(this.outputs);
        objectOutput.writeObject(this.analogInputs);
        objectOutput.writeObject(this.driverKeyCode);
    }

    @Override
    public void readExternal(final ObjectInput objectInput)
            throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.reductionPrecision = objectInput.readDouble();
        this.inputs = objectInput.readInt();
        this.outputs = objectInput.readInt();
        this.analogInputs = (double[]) objectInput.readObject();
        this.driverKeyCode = (String) objectInput.readObject();
    }
}
