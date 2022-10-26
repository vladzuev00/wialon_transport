package by.zuevvlad.wialontransport.wialonpackage.reduceddata;

import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;

import java.util.Objects;

import static java.lang.Byte.MIN_VALUE;
import static java.lang.String.format;
import static java.util.Arrays.stream;

public final class ResponseReducedDataPackage implements Package {
    public static final String PACKAGE_DESCRIPTION_PREFIX = "#ASD#";
    public static final String PACKAGE_DESCRIPTION_POSTFIX = "\r\n";

    private static final ToStringSerializer<ResponseReducedDataPackage> RESPONSE_REDUCED_DATA_PACKAGE_TO_STRING_SERIALIZER
            = new ResponseReducedDataPackageToStringSerializer();

    private final Status status;

    public ResponseReducedDataPackage(final Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    @Override
    public String serialize() {
        return RESPONSE_REDUCED_DATA_PACKAGE_TO_STRING_SERIALIZER.serialize(this);
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
        final ResponseReducedDataPackage other = (ResponseReducedDataPackage) otherObject;
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
        ERROR_GETTING_AMOUNT_SATELLITES((byte) 12);

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

    private static final class ResponseReducedDataPackageToStringSerializer
            implements ToStringSerializer<ResponseReducedDataPackage> {
        private static final String SERIALIZED_RESPONSE_REDUCED_DATA_PACKAGE_TEMPLATE = "#ASD#%d\r\n";

        @Override
        public String serialize(final ResponseReducedDataPackage serializedPackage) {
            return format(SERIALIZED_RESPONSE_REDUCED_DATA_PACKAGE_TEMPLATE, serializedPackage.status.value);
        }
    }
}
