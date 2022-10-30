package by.zuevvlad.wialontransport.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.Optional;

import static by.zuevvlad.wialontransport.entity.OutboundCommandEntity.Status.NOT_DEFINED;
import static java.util.Objects.hash;
import static java.util.Optional.ofNullable;

public final class OutboundCommandEntity extends CommandEntity {
    private Status status;
    private CommandEntity responseCommand;

    public OutboundCommandEntity() {
        this.status = NOT_DEFINED;
        this.responseCommand = null;
    }

    public OutboundCommandEntity(final long id, final String message, final TrackerEntity tracker,
                                 final Status status, final CommandEntity responseCommand) {
        super(id, message, tracker);
        this.status = status;
        this.responseCommand = responseCommand;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setResponseCommand(final CommandEntity responseCommand) {
        this.responseCommand = responseCommand;
    }

    public Optional<CommandEntity> getOptionalResponseCommand() {
        return ofNullable(this.responseCommand);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (!super.equals(otherObject)) {
            return false;
        }
        final OutboundCommandEntity other = (OutboundCommandEntity) otherObject;
        return this.status == other.status
                && Objects.equals(this.responseCommand, other.responseCommand);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + hash(this.status, this.responseCommand);
    }

    @Override
    public String toString() {
        return super.toString() + "[status = " + this.status
                + ", responseCommand = " + this.responseCommand + "]";
    }

    @Override
    public void writeExternal(final ObjectOutput objectOutput)
            throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeObject(this.status);
        objectOutput.writeObject(this.responseCommand);
    }

    @Override
    public void readExternal(final ObjectInput objectInput)
            throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.status = (Status) objectInput.readObject();
        this.responseCommand = (CommandEntity) objectInput.readObject();
    }

    public enum Status {
        NOT_DEFINED, NEW, SENT, SUCCESS_ANSWERED, ERROR_ANSWERED, TIMEOUT_NOT_ANSWERED
    }
}
