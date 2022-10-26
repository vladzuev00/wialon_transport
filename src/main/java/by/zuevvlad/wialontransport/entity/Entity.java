package by.zuevvlad.wialontransport.entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static java.lang.Long.MIN_VALUE;

public abstract class Entity implements Externalizable {
    private static final long NOT_DEFINED_ID = MIN_VALUE;

    private long id;

    public Entity() {
        this.id = NOT_DEFINED_ID;
    }

    public Entity(final long id) {
        this.id = id;
    }

    public final void setId(final long id) {
        this.id = id;
    }

    public final long getId() {
        return this.id;
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
        final Entity other = (Entity) otherObject;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[id = " + this.id + "]";
    }

    @Override
    public void writeExternal(final ObjectOutput objectOutput)
            throws IOException {
        objectOutput.writeLong(this.id);
    }

    @Override
    public void readExternal(final ObjectInput objectInput)
            throws IOException, ClassNotFoundException {
        this.id = objectInput.readLong();
    }
}
