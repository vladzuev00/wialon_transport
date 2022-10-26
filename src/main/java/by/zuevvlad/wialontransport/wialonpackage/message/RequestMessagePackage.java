package by.zuevvlad.wialontransport.wialonpackage.message;

import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;

import java.util.Objects;

import static java.lang.String.format;

public final class RequestMessagePackage implements Package {
    public static final String PACKAGE_DESCRIPTION_PREFIX = "#M#";
    public static final String PACKAGE_DESCRIPTION_POSTFIX = "\r\n";

    private static final ToStringSerializer<RequestMessagePackage> REQUEST_MESSAGE_PACKAGE_TO_STRING_SERIALIZER
            = new RequestMessagePackageToStringSerializer();

    private final String message;

    public RequestMessagePackage(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String serialize() {
        return REQUEST_MESSAGE_PACKAGE_TO_STRING_SERIALIZER.serialize(this);
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
        final RequestMessagePackage other = (RequestMessagePackage) otherObject;
        return Objects.equals(this.message, other.message);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.message);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[message = " + this.message + "]";
    }

    private static final class RequestMessagePackageToStringSerializer
            implements ToStringSerializer<RequestMessagePackage> {
        //%s - message
        private static final String SERIALIZED_REQUEST_MESSAGE_PACKAGE_TEMPLATE = "#M#%s\r\n";

        @Override
        public String serialize(final RequestMessagePackage serializedPackage) {
            return format(SERIALIZED_REQUEST_MESSAGE_PACKAGE_TEMPLATE, serializedPackage.message);
        }
    }
}
