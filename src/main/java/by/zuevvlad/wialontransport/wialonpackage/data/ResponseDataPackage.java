package by.zuevvlad.wialontransport.wialonpackage.data;

import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;

import java.util.Objects;

import static java.lang.Byte.MIN_VALUE;
import static java.lang.String.format;
import static java.util.Arrays.stream;

public final class ResponseDataPackage implements Package {
    public static final String PACKAGE_DESCRIPTION_PREFIX = "#AD#";
    public static final String PACKAGE_DESCRIPTION_POSTFIX = "\r\n";

    private static final ToStringSerializer<ResponseDataPackage> RESPONSE_DATA_PACKAGE_TO_STRING_SERIALIZER
            = new ResponseDataPackageToStringSerializer();

    private final Status status;

    public ResponseDataPackage(final Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    @Override
    public String serialize() {
        return RESPONSE_DATA_PACKAGE_TO_STRING_SERIALIZER.serialize(this);
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
        final ResponseDataPackage other = (ResponseDataPackage) otherObject;
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
        NOT_DEFINED(MIN_VALUE), ERROR_PACKAGE_STRUCTURE((byte) -1), INCORRECT_TIME((byte) 0),
        PACKAGE_FIX_SUCCESS((byte) 1), ERROR_GETTING_COORDINATE((byte) 10),
        ERROR_GETTING_SPEED_OR_COURSE_OR_HEIGHT((byte) 11),
        ERROR_GETTING_AMOUNT_SATELLITES_OR_REDUCTION_PRECISION((byte) 12),
        ERROR_GETTING_INPUTS_OR_OUTPUTS((byte) 13), ERROR_GETTING_ANALOG_INPUTS((byte) 14),
        ERROR_GETTING_PARAMETERS((byte) 15);

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

    private static final class ResponseDataPackageToStringSerializer
            implements ToStringSerializer<ResponseDataPackage> {
        private static final String SERIALIZED_RESPONSE_DATA_PACKAGE_TEMPLATE = "#AD#%d\r\n";

        @Override
        public String serialize(final ResponseDataPackage serializedPackage) {
            return format(SERIALIZED_RESPONSE_DATA_PACKAGE_TEMPLATE, serializedPackage.status.value);
        }
    }
}
