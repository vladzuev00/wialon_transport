package by.zuevvlad.wialontransport.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.function.Supplier;

import static java.util.Objects.hash;

public class CommandEntity extends Entity {
    private static final String NOT_DEFINED_MESSAGE = "not defined";
    private static final Supplier<TrackerEntity> SUPPLIER_NOT_DEFINED_TRACKER = TrackerEntity::new;

    private String message;
    private TrackerEntity tracker;

    public CommandEntity() {
        this.message = NOT_DEFINED_MESSAGE;
        this.tracker = SUPPLIER_NOT_DEFINED_TRACKER.get();
    }

    public CommandEntity(final long id, final String message, final TrackerEntity tracker) {
        super(id);
        this.message = message;
        this.tracker = tracker;
    }

    public final void setMessage(final String message) {
        this.message = message;
    }

    public final String getMessage() {
        return this.message;
    }

    public final void setTracker(final TrackerEntity tracker) {
        this.tracker = tracker;
    }

    public final TrackerEntity getTracker() {
        return this.tracker;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (!super.equals(otherObject)) {
            return false;
        }
        final CommandEntity other = (CommandEntity) otherObject;
        return Objects.equals(this.message, other.message) && Objects.equals(this.tracker, other.tracker);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + hash(this.message, this.tracker);
    }

    @Override
    public String toString() {
        return super.toString() + "[message = " + this.message + ", tracker = " + this.tracker + "]";
    }

    @Override
    public void writeExternal(final ObjectOutput objectOutput)
            throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeObject(this.message);
        objectOutput.writeObject(this.tracker);
    }

    @Override
    public void readExternal(final ObjectInput objectInput)
            throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.message = (String) objectInput.readObject();
        this.tracker = (TrackerEntity) objectInput.readObject();
    }
}
