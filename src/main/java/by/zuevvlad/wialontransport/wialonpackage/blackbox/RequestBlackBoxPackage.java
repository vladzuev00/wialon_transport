package by.zuevvlad.wialontransport.wialonpackage.blackbox;

import by.zuevvlad.wialontransport.entity.DataEntity;
import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;
import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.netty.tostringserializer.collection.CollectionDataToStringSerializer;
import by.zuevvlad.wialontransport.netty.tostringserializer.collection.CollectionExtendedDataToStringSerializer;
import by.zuevvlad.wialontransport.propertyfilereader.PropertyFileReader;
import by.zuevvlad.wialontransport.propertyfilereader.PropertyFileReaderImplementation;
import by.zuevvlad.wialontransport.wialonpackage.Package;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Objects.hash;

public final class RequestBlackBoxPackage implements Package {
    public static final String PACKAGE_DESCRIPTION_PREFIX = "#B#";
    public static final String PACKAGE_DESCRIPTION_POSTFIX = "\r\n";

    private static final ToStringSerializer<RequestBlackBoxPackage> REQUEST_BLACK_BOX_PACKAGE_TO_STRING_SERIALIZER
            = new RequestBlackBoxPackageToStringSerializer(
            CollectionDataToStringSerializer.create(),
            CollectionExtendedDataToStringSerializer.create());

    private final List<DataEntity> data;
    private final List<ExtendedDataEntity> extendedData;

    public RequestBlackBoxPackage(final List<DataEntity> data, final List<ExtendedDataEntity> extendedData) {
        this.data = data;
        this.extendedData = extendedData;
    }

    public List<DataEntity> getData() {
        return this.data;
    }

    public List<ExtendedDataEntity> getExtendedData() {
        return this.extendedData;
    }

    @Override
    public String serialize() {
        return REQUEST_BLACK_BOX_PACKAGE_TO_STRING_SERIALIZER.serialize(this);
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
        final RequestBlackBoxPackage other = (RequestBlackBoxPackage) otherObject;
        return Objects.equals(this.data, other.data) && Objects.equals(this.extendedData, other.extendedData);
    }

    @Override
    public int hashCode() {
        return hash(this.data, this.extendedData);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[data = " + this.data + ", extendedData = " + this.extendedData + "]";
    }

    private static final class RequestBlackBoxPackageToStringSerializer
            implements ToStringSerializer<RequestBlackBoxPackage> {
        private static final String FILE_PATH_NETTY_SERIALIZATION_CONFIGURATION
                = "./src/main/resources/netty/netty_serialization.properties";
        private static final File FILE_NETTY_SERIALIZATION_CONFIGURATION
                = new File(FILE_PATH_NETTY_SERIALIZATION_CONFIGURATION);
        private static final String PROPERTY_KEY_SERIALIZED_DATA_DELIMITER
                = "netty.serialization.black_box_package.serialized_data_delimiter";
        private static final String SERIALIZED_DATA_DELIMITER = findSerializedDataDelimiter();

        /*
            %s - serialized data | serialized extended data
         */
        private static final String SERIALIZED_REQUEST_BLACK_BOX_PACKAGE_TEMPLATE = "#B#%s\r\n";

        private final ToStringSerializer<Collection<DataEntity>> collectionDataToStringSerializer;
        private final ToStringSerializer<Collection<ExtendedDataEntity>> collectionExtendedDataToStringSerializer;

        public RequestBlackBoxPackageToStringSerializer(
                final ToStringSerializer<Collection<DataEntity>> collectionDataToStringSerializer,
                final ToStringSerializer<Collection<ExtendedDataEntity>> collectionExtendedDataToStringSerializer) {
            this.collectionDataToStringSerializer = collectionDataToStringSerializer;
            this.collectionExtendedDataToStringSerializer = collectionExtendedDataToStringSerializer;
        }

        @Override
        public String serialize(final RequestBlackBoxPackage serialized) {
            final String serializedData = this.collectionDataToStringSerializer.serialize(serialized.data);
            final String serializedExtendedData = this.collectionExtendedDataToStringSerializer
                    .serialize(serialized.extendedData);
            final String serializedAllData = join(SERIALIZED_DATA_DELIMITER, serializedData, serializedExtendedData);
            return format(SERIALIZED_REQUEST_BLACK_BOX_PACKAGE_TEMPLATE, serializedAllData);
        }

        private static String findSerializedDataDelimiter() {
            final PropertyFileReader propertyFileReader = PropertyFileReaderImplementation.create();
            final Properties properties = propertyFileReader.read(FILE_NETTY_SERIALIZATION_CONFIGURATION);
            return properties.getProperty(PROPERTY_KEY_SERIALIZED_DATA_DELIMITER);
        }
    }
}
