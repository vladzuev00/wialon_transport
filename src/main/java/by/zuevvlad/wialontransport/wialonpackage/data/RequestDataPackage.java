package by.zuevvlad.wialontransport.wialonpackage.data;

import by.zuevvlad.wialontransport.entity.ExtendedData;
import by.zuevvlad.wialontransport.netty.tostringserializer.ExtendedDataToStringSerializer;
import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;

import java.util.Objects;

import static java.lang.String.*;

public final class RequestDataPackage implements Package {
    public static final String PACKAGE_DESCRIPTION_PREFIX = "#D#";
    public static final String PACKAGE_DESCRIPTION_POSTFIX = "\r\n";

    private static final ToStringSerializer<RequestDataPackage> REQUEST_DATA_PACKAGE_TO_STRING_SERIALIZER
            = new RequestDataPackageToStringSerializer(ExtendedDataToStringSerializer.create());

    private final ExtendedData extendedData;

    public RequestDataPackage(final ExtendedData extendedData) {
        this.extendedData = extendedData;
    }

    public ExtendedData getExtendedData() {
        return this.extendedData;
    }

    @Override
    public String serialize() {
        return REQUEST_DATA_PACKAGE_TO_STRING_SERIALIZER.serialize(this);
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
        final RequestDataPackage other = (RequestDataPackage) otherObject;
        return Objects.equals(this.extendedData, other.extendedData);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.extendedData);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[extendedData = " + this.extendedData + "]";
    }

    private static final class RequestDataPackageToStringSerializer implements ToStringSerializer<RequestDataPackage> {
        //%s - serialized extended data
        private static final String SERIALIZED_REQUEST_DATA_PACKAGE_TEMPLATE = "#D#%s\r\n";

        private final ToStringSerializer<ExtendedData> extendedDataToStringSerializer;

        public RequestDataPackageToStringSerializer(
                final ToStringSerializer<ExtendedData> extendedDataToStringSerializer) {
            this.extendedDataToStringSerializer = extendedDataToStringSerializer;
        }

        @Override
        public String serialize(final RequestDataPackage serializedPackage) {
            final String serializedExtendedData = this.extendedDataToStringSerializer
                    .serialize(serializedPackage.extendedData);
            return format(SERIALIZED_REQUEST_DATA_PACKAGE_TEMPLATE, serializedExtendedData);
        }
    }
}
