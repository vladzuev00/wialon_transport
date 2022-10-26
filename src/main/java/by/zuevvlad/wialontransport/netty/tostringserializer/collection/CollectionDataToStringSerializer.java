package by.zuevvlad.wialontransport.netty.tostringserializer.collection;

import by.zuevvlad.wialontransport.entity.Data;
import by.zuevvlad.wialontransport.netty.tostringserializer.DataToStringSerializer;
import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.propertyfilereader.PropertyFileReader;
import by.zuevvlad.wialontransport.propertyfilereader.PropertyFileReaderImplementation;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

public final class CollectionDataToStringSerializer extends CollectionToStringSerializer<Data> {

    private CollectionDataToStringSerializer(final ToStringSerializer<Data> dataToStringSerializer,
                                             final String serializedDataDelimiter) {
        super(dataToStringSerializer, serializedDataDelimiter);
    }

    public static ToStringSerializer<Collection<Data>> create() {
        return SingletonInitializer.COLLECTION_DATA_TO_STRING_SERIALIZER;
    }

    private static final class SingletonInitializer {
        private static final String FILE_PATH_NETTY_SERIALIZATION_CONFIGURATION
                = "./src/main/resources/netty/netty_serialization.properties";
        private static final File FILE_NETTY_SERIALIZATION_CONFIGURATION
                = new File(FILE_PATH_NETTY_SERIALIZATION_CONFIGURATION);
        private static final String PROPERTY_KEY_SERIALIZED_DATA_DELIMITER
                = "netty.serialization.black_box_package.serialized_data_delimiter";

        private static final ToStringSerializer<Collection<Data>> COLLECTION_DATA_TO_STRING_SERIALIZER
                = createCollectionDataToStringSerializer();

        private static ToStringSerializer<Collection<Data>> createCollectionDataToStringSerializer() {
            final PropertyFileReader propertyFileReader = PropertyFileReaderImplementation.create();
            final Properties properties = propertyFileReader.read(FILE_NETTY_SERIALIZATION_CONFIGURATION);
            final String serializedDataDelimiter = properties.getProperty(PROPERTY_KEY_SERIALIZED_DATA_DELIMITER);
            final ToStringSerializer<Data> dataToStringSerializer = DataToStringSerializer.create();
            return new CollectionDataToStringSerializer(dataToStringSerializer, serializedDataDelimiter);
        }
    }
}
