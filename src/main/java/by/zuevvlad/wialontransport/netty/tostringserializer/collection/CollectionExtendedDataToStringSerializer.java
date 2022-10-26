package by.zuevvlad.wialontransport.netty.tostringserializer.collection;

import by.zuevvlad.wialontransport.entity.ExtendedData;
import by.zuevvlad.wialontransport.netty.tostringserializer.ExtendedDataToStringSerializer;
import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.propertyfilereader.PropertyFileReader;
import by.zuevvlad.wialontransport.propertyfilereader.PropertyFileReaderImplementation;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

public final class CollectionExtendedDataToStringSerializer extends CollectionToStringSerializer<ExtendedData> {

    private CollectionExtendedDataToStringSerializer(
            final ToStringSerializer<ExtendedData> extendedDataToStringSerializer,
            final String serializedExtendedDataDelimiter) {
        super(extendedDataToStringSerializer, serializedExtendedDataDelimiter);
    }

    public static ToStringSerializer<Collection<ExtendedData>> create() {
        return SingletonInitializer.COLLECTION_EXTENDED_DATA_TO_STRING_SERIALIZER;
    }

    private static final class SingletonInitializer {
        private static final String FILE_PATH_NETTY_SERIALIZATION_CONFIGURATION
                = "./src/main/resources/netty/netty_serialization.properties";
        private static final File FILE_NETTY_SERIALIZATION_CONFIGURATION
                = new File(FILE_PATH_NETTY_SERIALIZATION_CONFIGURATION);
        private static final String PROPERTY_KEY_SERIALIZED_DATA_DELIMITER
                = "netty.serialization.black_box_package.serialized_data_delimiter";

        private static final ToStringSerializer<Collection<ExtendedData>> COLLECTION_EXTENDED_DATA_TO_STRING_SERIALIZER
                = createCollectionExtendedDataToStringSerializer();

        private static ToStringSerializer<Collection<ExtendedData>> createCollectionExtendedDataToStringSerializer() {
            final PropertyFileReader propertyFileReader = PropertyFileReaderImplementation.create();
            final Properties properties = propertyFileReader.read(FILE_NETTY_SERIALIZATION_CONFIGURATION);
            final String serializedExtendedDataDelimiter = properties
                    .getProperty(PROPERTY_KEY_SERIALIZED_DATA_DELIMITER);
            final ToStringSerializer<ExtendedData> extendedDataToStringSerializer
                    = ExtendedDataToStringSerializer.create();
            return new CollectionExtendedDataToStringSerializer(extendedDataToStringSerializer,
                    serializedExtendedDataDelimiter);
        }
    }
}
