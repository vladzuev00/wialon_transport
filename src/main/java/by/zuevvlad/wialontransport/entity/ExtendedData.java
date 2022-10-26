package by.zuevvlad.wialontransport.entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;

public final class ExtendedData extends Data {
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
    private List<Parameter> parameters;

    public ExtendedData() {
        this.reductionPrecision = NOT_DEFINED_REDUCTION_PRECISION;
        this.inputs = NOT_DEFINED_INPUTS;
        this.outputs = NOT_DEFINED_OUTPUTS;
        this.analogInputs = NOT_DEFINED_VALUE_ANALOG_INPUTS_SUPPLIER.get();
        this.driverKeyCode = NOT_DEFINED_DRIVER_KEY_CODE;
        this.parameters = emptyList();
    }

    public ExtendedData(final long id, final LocalDateTime dateTime, final Latitude latitude, final Longitude longitude,
                        final int speed, final int course, final int height, final int amountSatellites,
                        final Tracker tracker,
                        final double reductionPrecision, final int inputs, final int outputs,
                        final double[] analogInputs, final String driverKeyCode,
                        final List<Parameter> parameters) {
        super(id, dateTime, latitude, longitude, speed, course, height, amountSatellites, tracker);
        this.reductionPrecision = reductionPrecision;
        this.inputs = inputs;
        this.outputs = outputs;
        this.analogInputs = analogInputs;
        this.driverKeyCode = driverKeyCode;
        this.parameters = parameters;
    }

    public ExtendedData(final Data data, final double reductionPrecision, final int inputs, final int outputs,
                        final double[] analogInputs, final String driverKeyCode,
                        final List<Parameter> parameters) {
        super(data);
        this.reductionPrecision = reductionPrecision;
        this.inputs = inputs;
        this.outputs = outputs;
        this.analogInputs = analogInputs;
        this.driverKeyCode = driverKeyCode;
        this.parameters = parameters;
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

    public void setParameters(final List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<Parameter> getParameters() {
        return this.parameters;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (!super.equals(otherObject)) {
            return false;
        }
        final ExtendedData other = (ExtendedData) otherObject;
        return this.reductionPrecision == other.reductionPrecision
                && this.inputs == other.inputs
                && this.outputs == other.outputs
                && Arrays.equals(this.analogInputs, other.analogInputs)
                && Objects.equals(this.driverKeyCode, other.driverKeyCode)
                && Objects.equals(this.parameters, other.parameters);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(this.reductionPrecision, this.inputs, this.outputs, this.driverKeyCode,
                this.parameters) + Arrays.hashCode(this.analogInputs);
    }

    @Override
    public String toString() {
        return super.toString() + "[reductionPrecision = " + this.reductionPrecision + ", inputs = " + this.inputs
                + ", outputs = " + this.outputs + ", analogInputs = " + Arrays.toString(this.analogInputs)
                + ", driverKeyCode = " + this.driverKeyCode + ", parameters = " + this.parameters + "]";
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
        objectOutput.writeObject(this.parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(final ObjectInput objectInput)
            throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.reductionPrecision = objectInput.readDouble();
        this.inputs = objectInput.readInt();
        this.outputs = objectInput.readInt();
        this.analogInputs = (double[]) objectInput.readObject();
        this.driverKeyCode = (String) objectInput.readObject();
        this.parameters = (List<Parameter>) objectInput.readObject();
    }

    public static final class Parameter implements Externalizable {
        private static final String NOT_DEFINED_NAME = "not defined";
        private static final ValueType NOT_DEFINED_VALUE_TYPE = ValueType.NOT_DEFINED;
        private static final Supplier<Object> NOT_DEFINED_VALUE_SUPPLIER = Object::new;

        private String name;
        private ValueType valueType;
        private Object value;

        public Parameter() {
            this.name = NOT_DEFINED_NAME;
            this.valueType = NOT_DEFINED_VALUE_TYPE;
            this.value = NOT_DEFINED_VALUE_SUPPLIER.get();
        }

        public Parameter(final String name, final ValueType valueType, final Object value) {
            this.name = name;
            this.valueType = valueType;
            this.value = value;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setValueType(final ValueType valueType) {
            this.valueType = valueType;
        }

        public ValueType getValueType() {
            return this.valueType;
        }

        public void setValue(final Object value) {
            this.value = value;
        }

        public Object getValue() {
            return this.value;
        }

        @Override
        public boolean equals(final Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null) {
                return false;
            }
            if (this.getClass() != otherObject.getClass()) {
                return false;
            }
            final Parameter other = (Parameter) otherObject;
            return Objects.equals(this.name, other.name)
                    && this.valueType == other.valueType
                    && Objects.equals(this.value, other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.valueType, this.value);
        }

        @Override
        public String toString() {
            return this.getClass().getName() + "[name = " + this.name + ", valueType = " + this.valueType
                    + ", value = " + this.value + "]";
        }

        @Override
        public void writeExternal(final ObjectOutput objectOutput)
                throws IOException {
            objectOutput.writeObject(this.name);
            objectOutput.writeObject(this.valueType);
            objectOutput.writeObject(this.value);
        }

        @Override
        public void readExternal(final ObjectInput objectInput)
                throws IOException, ClassNotFoundException {
            this.name = (String) objectInput.readObject();
            this.valueType = (ValueType) objectInput.readObject();
            this.value = objectInput.readObject();
        }

        public enum ValueType {
            NOT_DEFINED((byte) 0, valueDescription -> null),
            INTEGER((byte) 1, Integer::parseInt),
            DOUBLE((byte) 2, Double::parseDouble),
            STRING((byte) 3, valueDescription -> valueDescription);

            private final byte value;
            private final Function<String, Object> parserFunction;

            ValueType(final byte value, final Function<String, Object> parserFunction) {
                this.value = value;
                this.parserFunction = parserFunction;
            }

            public final byte getValue() {
                return this.value;
            }

            public final Object parseValue(final String valueDescription) {
                return this.parserFunction.apply(valueDescription);
            }

            public static ValueType findByValue(final byte value) {
                return Arrays.stream(ValueType.values())
                        .filter(researchType -> researchType.value == value)
                        .findAny()
                        .orElse(NOT_DEFINED);
            }
        }
    }
}
