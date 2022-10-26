package by.zuevvlad.wialontransport.propertyfilereader;

import by.zuevvlad.wialontransport.propertyfilereader.exception.PropertyFileReadingException;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class PropertyFileReaderImplementationTest {
    private static final String PATH_PROPERTY_FILE_FOR_TEST = "./src/test/resources/propertyfilefortest/file.properties";
    private static final String PATH_PROPERTY_EMPTY_FILE_FOR_TEST
            = ".src/test/resources/propertyfilefortest/emptyfile.properties";
    private static final String PATH_NOT_EXISTING_PROPERTY_FILE = "not exist";

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<PropertyFileReader> createdReaders = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdReaders.put(PropertyFileReaderImplementation.create());
                } catch (final InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdReaders.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfReaders = createdReaders.stream().distinct().count();
        final long expectedAmountOfReaders = 1;
        assertEquals(expectedAmountOfReaders, actualAmountOfReaders);
    }

    @Test
    public void propertyFileShouldBeRead()
            throws Exception {
        final PropertyFileReader propertyFileReader = createPropertyFileReader();
        final File file = new File(PATH_PROPERTY_FILE_FOR_TEST);
        final Properties actual = propertyFileReader.read(file);

        final Properties expected = new Properties() {
            {
                super.put("first", "firstvalue");
                super.put("second", "secondvalue");
                super.put("third", "thirdvalue");
            }
        };
        assertEquals(expected, actual);
    }

    @Test
    public void emptyPropertyShouldBeRead()
            throws Exception {
        final PropertyFileReader propertyFileReader = createPropertyFileReader();
        final File file = new File(PATH_PROPERTY_EMPTY_FILE_FOR_TEST);
        final Properties properties = propertyFileReader.read(file);
        assertTrue(properties.isEmpty());
    }

    @Test(expected = PropertyFileReadingException.class)
    public void propertyFileWithNotExistingPathShouldNotBeRead()
            throws Exception {
        final PropertyFileReader propertyFileReader = createPropertyFileReader();
        final File file = new File(PATH_NOT_EXISTING_PROPERTY_FILE);
        propertyFileReader.read(file);
    }

    private static PropertyFileReader createPropertyFileReader()
            throws Exception {
        final Class<? extends PropertyFileReader> propertyFileReaderClass = PropertyFileReaderImplementation.class;
        final Constructor<? extends PropertyFileReader> propertyFileReaderConstructor
                = propertyFileReaderClass.getDeclaredConstructor();
        return propertyFileReaderConstructor.newInstance();
    }
}
