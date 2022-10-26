package by.zuevvlad.wialontransport.dao.dbconnectionpool;

import by.zuevvlad.wialontransport.dao.dbconnectionpool.exception.DataBaseConnectionPoolFullException;
import by.zuevvlad.wialontransport.dao.exception.NoAvailableConnectionInPoolException;
import by.zuevvlad.wialontransport.propertyfilereader.PropertyFileReader;
import by.zuevvlad.wialontransport.propertyfilereader.PropertyFileReaderImplementation;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.*;

import static java.lang.Class.forName;
import static java.lang.Integer.parseInt;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.*;

public final class DataBaseConnectionPoolImplementationIntegrationTest {
    private static final String POOL_FULLER_CLASS_NAME
            = "by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPoolImplementation"
            + "$SingletonInitializer$PoolFuller";
    private static final Class<?>[] POOL_FULLER_CONSTRUCTOR_PARAMETER_TYPES
            = {String.class, String.class, String.class, int.class};
    private static final ExecutorService SINGLE_THREAD_EXECUTOR_SERVICE = newSingleThreadExecutor();

    private static final String PATH_FILE_WITH_DB_PROPERTIES_FOR_TEST
            = "./src/test/resources/dbproperty/db.properties";
    private static final String PROPERTY_KEY_DRIVER_CLASS_NAME = "db.driverClassName";
    private static final String PROPERTY_KEY_URL = "db.url";
    private static final String PROPERTY_KEY_USER = "db.user";
    private static final String PROPERTY_KEY_PASSWORD = "db.password";
    private static final String PROPERTY_KEY_INVOLVED_CONNECTIONS_AMOUNT = "db.involvedConnectionsAmount";

    private String url;
    private String user;
    private String password;
    private int involvedConnectionsAmount;

    @Before
    public void initializeDataBaseProperties()
            throws ClassNotFoundException {
        final PropertyFileReader propertyFileReader = PropertyFileReaderImplementation.create();
        final File dataBasePropertiesFile = new File(PATH_FILE_WITH_DB_PROPERTIES_FOR_TEST);
        final Properties dataBaseProperties = propertyFileReader.read(dataBasePropertiesFile);

        final String driverClassName = dataBaseProperties.getProperty(PROPERTY_KEY_DRIVER_CLASS_NAME);
        forName(driverClassName);

        this.url = dataBaseProperties.getProperty(PROPERTY_KEY_URL);
        this.user = dataBaseProperties.getProperty(PROPERTY_KEY_USER);
        this.password = dataBaseProperties.getProperty(PROPERTY_KEY_PASSWORD);
        this.involvedConnectionsAmount = parseInt(dataBaseProperties.getProperty(
                PROPERTY_KEY_INVOLVED_CONNECTIONS_AMOUNT));
    }

    @Test
    public void dataBaseConnectionPoolShouldBeThreadSafeLazySingleton() {
        final int startedThreadAmount = 50;
        final BlockingQueue<DataBaseConnectionPool> createdPools = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdPools.put(DataBaseConnectionPoolImplementation.create());
                } catch (InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdPools.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfCreatedPools = createdPools.stream().distinct().count();
        final long expectedAmountOfCreatedPools = 1;
        assertEquals(expectedAmountOfCreatedPools, actualAmountOfCreatedPools);
    }

    @Test
    public void connectionsShouldBeFulled()
            throws Exception {
        final Callable<BlockingQueue<Connection>> poolFuller = this.createPoolFuller();
        final Future<BlockingQueue<Connection>> futureFulledConnections = SINGLE_THREAD_EXECUTOR_SERVICE
                .submit(poolFuller);
        final BlockingQueue<Connection> fulledConnections = futureFulledConnections.get();
        final int actualAmountFulledConnections = fulledConnections.size();
        assertEquals(this.involvedConnectionsAmount, actualAmountFulledConnections);
    }

    @Test
    public void amountOfAvailableConnectionsShouldBeFoundAndEqualMaxPossible()
            throws Exception {
        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool()) {
            final int actual = dataBaseConnectionPool.findAmountOfAvailableConnections();
            assertEquals(this.involvedConnectionsAmount, actual);
        }
    }

    @Test
    public void amountOfAvailableConnectionsShouldBeFoundAndEqualMaxPossibleMinusThree()
            throws Exception {
        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool()) {
            final List<Connection> extractedConnections = new ArrayList<>();
            try {
                rangeClosed(1, 3).forEach(i -> {
                    final Optional<Connection> optionalExtractedConnection = dataBaseConnectionPool
                            .findAvailableConnection();
                    final Connection extractedConnection = optionalExtractedConnection
                            .orElseThrow(NoAvailableConnectionInPoolException::new);
                    extractedConnections.add(extractedConnection);
                });
                final int actual = dataBaseConnectionPool.findAmountOfAvailableConnections();
                final int expected = this.involvedConnectionsAmount - 3;
                assertEquals(expected, actual);
            } finally {
                extractedConnections.forEach(dataBaseConnectionPool::returnConnectionToPool);
            }
        }
    }

    @Test
    public void amountOfAvailableConnectionsShouldBeFoundAndEqualZero()
            throws Exception {
        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool()) {
            final List<Connection> extractedConnections = new ArrayList<>();
            try {
                rangeClosed(1, this.involvedConnectionsAmount).forEach(i -> {
                    final Optional<Connection> optionalExtractedConnection = dataBaseConnectionPool
                            .findAvailableConnection();
                    final Connection extractedConnection = optionalExtractedConnection
                            .orElseThrow(NoAvailableConnectionInPoolException::new);
                    extractedConnections.add(extractedConnection);
                });
                final int actual = dataBaseConnectionPool.findAmountOfAvailableConnections();
                final int expected = 0;
                assertEquals(expected, actual);
            } finally {
                extractedConnections.forEach(dataBaseConnectionPool::returnConnectionToPool);
            }
        }
    }

    @Test
    public void connectionShouldBeAccessedSuccessfully()
            throws Exception {
        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool()) {
            final Optional<Connection> optionalConnection = dataBaseConnectionPool.findAvailableConnection();
            assertTrue(optionalConnection.isPresent());
        }
    }

    @Test
    public void connectionShouldBeAccessedSuccessfullyAfterReturning()
            throws Exception {
        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool()) {
            final List<Connection> extractedConnections = new ArrayList<>();
            try {
                rangeClosed(1, 20).forEach(i -> {
                    final Optional<Connection> optionalExtractedConnection = dataBaseConnectionPool
                            .findAvailableConnection();
                    final Connection extractedConnection = optionalExtractedConnection
                            .orElseThrow(NoAvailableConnectionInPoolException::new);
                    extractedConnections.add(extractedConnection);
                });

                final Thread threadReturningFirstConnection = new Thread(() -> {
                    try {
                        MILLISECONDS.sleep(100);
                    } catch (final InterruptedException cause) {
                        throw new RuntimeException(cause);
                    }
                    final Connection returnedConnection = extractedConnections.remove(0);
                    dataBaseConnectionPool.returnConnectionToPool(returnedConnection);
                });
                threadReturningFirstConnection.start();

                threadReturningFirstConnection.join();

                final Optional<Connection> optionalConnection = dataBaseConnectionPool.findAvailableConnection();
                assertTrue(optionalConnection.isPresent());
            } finally {
                extractedConnections.forEach(dataBaseConnectionPool::returnConnectionToPool);
            }
        }
    }

    @Test
    public void connectionShouldNotBeAccessedSuccessfullyBecauseOfNoAvailableConnections()
            throws Exception {
        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool()) {
            final List<Connection> extractedConnections = new ArrayList<>();
            try {
                rangeClosed(1, 20).forEach(i -> {
                    final Optional<Connection> optionalExtractedConnection = dataBaseConnectionPool
                            .findAvailableConnection();
                    final Connection extractedConnection = optionalExtractedConnection
                            .orElseThrow(NoAvailableConnectionInPoolException::new);
                    extractedConnections.add(extractedConnection);
                });
                final Optional<Connection> optionalConnection = dataBaseConnectionPool.findAvailableConnection();
                assertTrue(optionalConnection.isEmpty());
            } finally {
                extractedConnections.forEach(dataBaseConnectionPool::returnConnectionToPool);
            }
        }
    }

    @Test
    public void connectionShouldBeReturnedToPool()
            throws Exception {
        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool()) {
            assertEquals(this.involvedConnectionsAmount, dataBaseConnectionPool.findAmountOfAvailableConnections());
            final Optional<Connection> optionalConnection = dataBaseConnectionPool.findAvailableConnection();
            final Connection connection = optionalConnection.orElseThrow(NoAvailableConnectionInPoolException::new);
            try {
                assertEquals(this.involvedConnectionsAmount - 1,
                        dataBaseConnectionPool.findAmountOfAvailableConnections());
            } finally {
                dataBaseConnectionPool.returnConnectionToPool(connection);
                assertEquals(this.involvedConnectionsAmount, dataBaseConnectionPool.findAmountOfAvailableConnections());
            }
        }
    }

    @Test(expected = DataBaseConnectionPoolFullException.class)
    public void connectionShouldNotBeReturnedInFullPool()
            throws Exception {
        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool()) {
            final Optional<Connection> optionalConnection = dataBaseConnectionPool.findAvailableConnection();
            final Connection connection = optionalConnection.orElseThrow(NoAvailableConnectionInPoolException::new);
            dataBaseConnectionPool.returnConnectionToPool(connection);
            dataBaseConnectionPool.returnConnectionToPool(connection);
        }
    }

    @Test
    public void connectionPoolShouldBeClosed()
            throws Exception {
        final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool();
        assertNotEquals(0, dataBaseConnectionPool.findAmountOfAvailableConnections());
        dataBaseConnectionPool.close();
        assertEquals(0, dataBaseConnectionPool.findAmountOfAvailableConnections());
    }

    @SuppressWarnings("unchecked")
    private Callable<BlockingQueue<Connection>> createPoolFuller()
            throws Exception {
        final Class<?> poolFullerClass = forName(POOL_FULLER_CLASS_NAME);
        final Constructor<?> poolFullerConstructor = poolFullerClass.getConstructor(
                POOL_FULLER_CONSTRUCTOR_PARAMETER_TYPES);
        return (Callable<BlockingQueue<Connection>>) poolFullerConstructor
                .newInstance(this.url, this.user, this.password, this.involvedConnectionsAmount);
    }

    private DataBaseConnectionPool createDataBaseConnectionPool()
            throws Exception {
        final Callable<BlockingQueue<Connection>> poolFuller = this.createPoolFuller();
        final Future<BlockingQueue<Connection>> futureFulledConnections = SINGLE_THREAD_EXECUTOR_SERVICE
                .submit(poolFuller);

        final Class<? extends DataBaseConnectionPool> dataBaseConnectionPoolClass
                = DataBaseConnectionPoolImplementation.class;
        final Constructor<? extends DataBaseConnectionPool> dataBaseConnectionPoolConstructor
                = dataBaseConnectionPoolClass.getDeclaredConstructor(Future.class);
        dataBaseConnectionPoolConstructor.setAccessible(true);
        try {
            return dataBaseConnectionPoolConstructor.newInstance(futureFulledConnections);
        } finally {
            dataBaseConnectionPoolConstructor.setAccessible(false);
        }
    }
}
