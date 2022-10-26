package by.zuevvlad.wialontransport.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.function.Supplier;

import static java.util.Objects.hash;

public final class Tracker extends Entity {
    public static final String NOT_DEFINED_IMEI = "not defined";
    public static final String NOT_DEFINED_PASSWORD = "not defined";
    public static final String NOT_DEFINED_PHONE_NUMBER = "not_defined";
    public static final Supplier<User> NOT_DEFINED_USER_SUPPLIER = User::new;

    private String imei;
    private String password;
    private String phoneNumber;
    private User user;

    public Tracker() {
        this.imei = NOT_DEFINED_IMEI;
        this.password = NOT_DEFINED_PASSWORD;
        this.phoneNumber = NOT_DEFINED_PHONE_NUMBER;
        this.user = NOT_DEFINED_USER_SUPPLIER.get();
    }

    public Tracker(final long id, final String imei, final String password, final String phoneNumber,
                   final User user) {
        super(id);
        this.imei = imei;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.user = user;
    }

    public void setImei(final String imei) {
        this.imei = imei;
    }

    public String getImei() {
        return this.imei;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (!super.equals(otherObject)) {
            return false;
        }
        final Tracker other = (Tracker) otherObject;
        return Objects.equals(this.imei, other.imei)
                && Objects.equals(this.password, other.password)
                && Objects.equals(this.phoneNumber, other.phoneNumber)
                && Objects.equals(this.user, other.user);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + hash(this.imei, this.password, this.phoneNumber, this.user);
    }

    @Override
    public String toString() {
        return super.toString() + "[imei = " + this.imei + ", password = " + this.password
                + ", phoneNumber = " + this.phoneNumber + ", user = " + this.user + "]";
    }

    @Override
    public void writeExternal(final ObjectOutput objectOutput)
            throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeChars(this.imei);
        objectOutput.writeChars(this.password);
        objectOutput.writeChars(this.phoneNumber);
        objectOutput.writeObject(this.user);
    }

    @Override
    public void readExternal(final ObjectInput objectInput)
            throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.imei = (String) objectInput.readObject();
        this.password = (String) objectInput.readObject();
        this.phoneNumber = (String) objectInput.readObject();
        this.user = (User) objectInput.readObject();
    }
}
