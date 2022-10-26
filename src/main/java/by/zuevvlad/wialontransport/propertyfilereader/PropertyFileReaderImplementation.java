package by.zuevvlad.wialontransport.propertyfilereader;

import by.zuevvlad.wialontransport.propertyfilereader.exception.PropertyFileReadingException;

import java.io.*;
import java.util.Properties;

public final class PropertyFileReaderImplementation implements PropertyFileReader {

    public static PropertyFileReader create() {
        return SingletonHolder.PROPERTY_FILE_READER;
    }

    private PropertyFileReaderImplementation() {

    }

    @Override
    public Properties read(final File file) {
        try (final InputStream inputStream = new FileInputStream(file);
             final InputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            final Properties readProperties = new Properties();
            readProperties.load(bufferedInputStream);
            return readProperties;
        } catch (final IOException cause) {
            throw new PropertyFileReadingException(cause);
        }
    }

    private static final class SingletonHolder {
        private static final PropertyFileReader PROPERTY_FILE_READER = new PropertyFileReaderImplementation();
    }
}
