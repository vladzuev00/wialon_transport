package by.zuevvlad.wialontransport.wialonpackage.reduceddata;

import by.zuevvlad.wialontransport.entity.Data;
import by.zuevvlad.wialontransport.netty.tostringserializer.DataToStringSerializer;
import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;

import java.util.Objects;

import static java.lang.String.format;

public final class RequestReducedDataPackage implements Package {
    public static final String PACKAGE_DESCRIPTION_PREFIX = "#SD#";
    public static final String PACKAGE_DESCRIPTION_POSTFIX = "\r\n";
    public static final String DELIMITER_DATA_COMPONENTS = ";";

    private static final ToStringSerializer<RequestReducedDataPackage> REQUEST_REDUCED_DATA_PACKAGE_TO_STRING_SERIALIZER
            = new RequestReducedDataPackageToStringSerializer(DataToStringSerializer.create());

    private final Data data;

    public RequestReducedDataPackage(final Data data) {
        this.data = data;
    }

    public Data getData() {
        return this.data;
    }

    @Override
    public String serialize() {
        return REQUEST_REDUCED_DATA_PACKAGE_TO_STRING_SERIALIZER.serialize(this);
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
        final RequestReducedDataPackage other = (RequestReducedDataPackage) otherObject;
        return Objects.equals(this.data, other.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.data);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[data = " + this.data + "]";
    }

    private static final class RequestReducedDataPackageToStringSerializer
            implements ToStringSerializer<RequestReducedDataPackage> {
        //%s - serialized data
        private static final String SERIALIZED_REQUEST_DATA_PACKAGE_TEMPLATE = "#SD#%s\r\n";

        private final ToStringSerializer<Data> dataToStringSerializer;

        public RequestReducedDataPackageToStringSerializer(final ToStringSerializer<Data> dataToStringSerializer) {
            this.dataToStringSerializer = dataToStringSerializer;
        }

        @Override
        public String serialize(final RequestReducedDataPackage serializedPackage) {
            final String serializedData = this.dataToStringSerializer.serialize(serializedPackage.data);
            return format(SERIALIZED_REQUEST_DATA_PACKAGE_TEMPLATE, serializedData);
        }
    }
}
