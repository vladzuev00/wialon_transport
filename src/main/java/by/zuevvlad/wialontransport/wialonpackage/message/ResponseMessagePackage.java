package by.zuevvlad.wialontransport.wialonpackage.message;

import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;

import java.util.Objects;

import static java.lang.String.format;
import static java.util.Arrays.stream;

public final class ResponseMessagePackage implements Package {
    public static final String PACKAGE_DESCRIPTION_PREFIX = "#AM#";
    public static final String PACKAGE_DESCRIPTION_POSTFIX = "\r\n";

    private static final ToStringSerializer<ResponseMessagePackage> RESPONSE_MESSAGE_PACKAGE_TO_STRING_SERIALIZER
            = new ResponseMessagePackageToStringSerializer();

    private final Status status;

    public ResponseMessagePackage(final Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    @Override
    public String serialize() {
        return RESPONSE_MESSAGE_PACKAGE_TO_STRING_SERIALIZER.serialize(this);
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
        final ResponseMessagePackage other = (ResponseMessagePackage) otherObject;
        return this.status == other.status;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.status);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[status = " + this.status + "]";
    }

    public enum Status {
        NOT_DEFINED((byte) -1), SUCCESS((byte) 1), ERROR((byte) 0);

        private final byte value;

        Status(final byte value) {
            this.value = value;
        }

        public final byte getValue() {
            return this.value;
        }

        public static Status findByValue(final byte value) {
            return stream(Status.values())
                    .filter(status -> status.value == value)
                    .findAny()
                    .orElse(NOT_DEFINED);
        }
    }

    private static final class ResponseMessagePackageToStringSerializer
            implements ToStringSerializer<ResponseMessagePackage> {
        //%s - status's value
        private static final String SERIALIZED_RESPONSE_MESSAGE_PACKAGE_TEMPLATE = "#AM#%d\r\n";

        @Override
        public String serialize(final ResponseMessagePackage serializedPackage) {
            return format(SERIALIZED_RESPONSE_MESSAGE_PACKAGE_TEMPLATE, serializedPackage.status.value);
        }
    }
}
