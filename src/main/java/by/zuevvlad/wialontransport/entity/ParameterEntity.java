package by.zuevvlad.wialontransport.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.ParameterEntity.Type.NOT_DEFINED;
import static java.util.Arrays.stream;
import static java.util.Objects.hash;

public final class ParameterEntity extends Entity {
    private static final String VALUE_NOT_DEFINED_NAME = "not defined";
    private static final Supplier<Object> SUPPLIER_NOT_DEFINED_VALUE = Object::new;
    private static final Supplier<ExtendedDataEntity> SUPPLIER_NOT_DEFINED_EXTENDED_DATA = ExtendedDataEntity::new;

    private String name;
    private Type type;
    private Object value;
    private ExtendedDataEntity extendedData;

    public ParameterEntity() {
        this.name = VALUE_NOT_DEFINED_NAME;
        this.type = NOT_DEFINED;
        this.value = SUPPLIER_NOT_DEFINED_VALUE.get();
        this.extendedData = SUPPLIER_NOT_DEFINED_EXTENDED_DATA.get();
    }

    public ParameterEntity(final long id, final String name, final Type type, final Object value,
                           final ExtendedDataEntity extendedData) {
        super(id);
        this.name = name;
        this.type = type;
        this.value = value;
        this.extendedData = extendedData;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public void setExtendedData(final ExtendedDataEntity extendedData) {
        this.extendedData = extendedData;
    }

    public ExtendedDataEntity getExtendedData() {
        return this.extendedData;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (!super.equals(otherObject)) {
            return false;
        }
        final ParameterEntity other = (ParameterEntity) otherObject;
        return Objects.equals(this.name, other.name)
                && this.type == other.type
                && Objects.equals(this.value, other.value)
                && Objects.equals(this.extendedData, other.extendedData);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + hash(this.name, this.type, this.value, this.extendedData);
    }

    @Override
    public String toString() {
        return super.toString() + "[name = " + this.name + ", type = " + this.type + ", value = " + this.value
                + ", extendedData = " + this.extendedData + "]";
    }

    @Override
    public void writeExternal(final ObjectOutput objectOutput)
            throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeObject(this.name);
        objectOutput.writeObject(this.type);
        objectOutput.writeObject(this.value);
        objectOutput.writeObject(this.extendedData);
    }

    @Override
    public void readExternal(final ObjectInput objectInput)
            throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.name = (String) objectInput.readObject();
        this.type = (Type) objectInput.readObject();
        this.value = objectInput.readObject();
        this.extendedData = (ExtendedDataEntity) objectInput.readObject();
    }

    public enum Type {
        NOT_DEFINED((byte) 0),
        INTEGER((byte) 1),
        DOUBLE((byte) 2),
        STRING((byte) 3);

        private final byte value;

        Type(final byte value) {
            this.value = value;
        }

        public final byte getValue() {
            return this.value;
        }

        public static Type findByValue(final byte value) {
            return stream(values())
                    .filter(type -> type.value == value)
                    .findAny()
                    .orElse(NOT_DEFINED);
        }
    }
}
