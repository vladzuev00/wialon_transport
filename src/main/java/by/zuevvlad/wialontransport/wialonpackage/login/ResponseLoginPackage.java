package by.zuevvlad.wialontransport.wialonpackage.login;

import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;

import java.util.Objects;

import static java.lang.String.format;
import static java.util.Arrays.stream;

public final class ResponseLoginPackage implements Package {
    public static final String PACKAGE_DESCRIPTION_PREFIX = "#AL#";
    public static final String PACKAGE_DESCRIPTION_POSTFIX = "\r\n";

    private static final ToStringSerializer<ResponseLoginPackage> RESPONSE_LOGIN_PACKAGE_TO_STRING_SERIALIZER
            = new ResponseLoginPackageSerializer();

    private final Status status;

    public ResponseLoginPackage(final Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
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
        final ResponseLoginPackage other = (ResponseLoginPackage) otherObject;
        return this.status == other.status;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.status);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[responseLoginStatus = " + this.status + "]";
    }

    @Override
    public String serialize() {
        return RESPONSE_LOGIN_PACKAGE_TO_STRING_SERIALIZER.serialize(this);
    }

    public enum Status {
        NOT_DEFINED("not defined"),
        SUCCESS_AUTHORIZATION("1"),
        CONNECTION_FAILURE("0"),
        ERROR_CHECK_PASSWORD("01");

        private final String value;

        Status(final String value) {
            this.value = value;
        }

        public final String getValue() {
            return this.value;
        }

        public static Status findByValue(final String value) {
            return stream(Status.values())
                    .filter(status -> Objects.equals(status.value, value))
                    .findAny()
                    .orElse(NOT_DEFINED);
        }
    }

    private static final class ResponseLoginPackageSerializer implements ToStringSerializer<ResponseLoginPackage> {
        private static final String SERIALIZED_RESPONSE_LONG_PACKAGE_TEMPLATE = "#AL#%s\r\n";

        @Override
        public String serialize(final ResponseLoginPackage serializedPackage) {
            return format(SERIALIZED_RESPONSE_LONG_PACKAGE_TEMPLATE, serializedPackage.status.value);
        }
    }
}
