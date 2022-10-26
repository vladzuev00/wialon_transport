package by.zuevvlad.wialontransport.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

import static by.zuevvlad.wialontransport.entity.User.Role.NOT_DEFINED;

public final class User extends Entity {
    public static final String NOT_DEFINED_EMAIL = "not defined";
    public static final String NOT_DEFINED_PASSWORD = "not defined";

    private String email;
    private String password;
    private Role role;

    public User() {
        this.email = NOT_DEFINED_EMAIL;
        this.password = NOT_DEFINED_PASSWORD;
        this.role = NOT_DEFINED;
    }

    public User(final long id, final String email, final String password, final Role role) {
        super(id);
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public Role getRole() {
        return this.role;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (!super.equals(otherObject)) {
            return true;
        }
        final User other = (User) otherObject;
        return Objects.equals(this.email, other.email)
                && Objects.equals(this.password, other.password)
                && this.role == other.role;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(this.email, this.password, this.role);
    }

    @Override
    public String toString() {
        return super.toString() + "[email = " + this.email + ", password = " + this.password
                + ", role = " + this.role + "]";
    }

    @Override
    public void writeExternal(final ObjectOutput objectOutput)
            throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeChars(this.email);
        objectOutput.writeChars(this.password);
        objectOutput.writeObject(this.role);
    }

    @Override
    public void readExternal(final ObjectInput objectInput)
            throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.email = (String) objectInput.readObject();
        this.password = (String) objectInput.readObject();
        this.role = (Role) objectInput.readObject();
    }

    public enum Role {
        NOT_DEFINED, USER, ADMIN
    }
}
