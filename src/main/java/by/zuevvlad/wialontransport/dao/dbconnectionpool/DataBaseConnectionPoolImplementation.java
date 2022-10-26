package by.zuevvlad.wialontransport.dao.dbconnectionpool;

import by.zuevvlad.wialontransport.dao.dbconnectionpool.exception.*;
import by.zuevvlad.wialontransport.propertyfilereader.PropertyFileReader;
import by.zuevvlad.wialontransport.propertyfilereader.PropertyFileReaderImplementation;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.lang.Class.forName;
import static java.util.stream.IntStream.range;
import static java.sql.DriverManager.getConnection;
import static java.util.logging.Logger.getLogger;
import static java.lang.String.format;
import static java.util.stream.IntStream.rangeClosed;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.Optional.ofNullable;

public final class DataBaseConnectionPoolImplementation implements DataBaseConnectionPool {
    private static final Logger LOGGER = getLogger(DataBaseConnectionPoolImplementation.class.getName());

    private static final String MESSAGE_SUCCESS_ACCESS_CONNECTION = "Connection was successfully accessed.";
    private static final String MESSAGE_ERRONEOUSLY_ACCESS_CONNECTION = "Connection was erroneously accessed.";

    private static final String MESSAGE_SUCCESS_RETURN_CONNECTION = "Connection was successfully returned.";

    private static final String MESSAGE_TEMPLATE_SUCCESS_CLOSE_CONNECTION = "Connection #%d was successfully "
            + "closed.";
    private static final String MESSAGE_TEMPLATE_ERRONEOUSLY_CLOSE_CONNECTION = "Closing connection #%d was "
            + "erroneously.";

    private static final String MESSAGE_SUCCESS_CLOSE_POOL = "Pool was successfully closed.";

    private static final String EXCEPTION_DESCRIPTION_CAUSED_INTERRUPTION
            = "Thread getting available connections was interrupted.";

    private static final long WAITING_CONNECTION_UNITS_AMOUNT = 1;
    private static final TimeUnit TIME_UNIT_OF_WAITING_OF_CONNECTION = SECONDS;

    private final Future<BlockingQueue<Connection>> holderOfAvailableConnections;

    private DataBaseConnectionPoolImplementation(final Future<BlockingQueue<Connection>> holderOfAvailableConnections) {
        this.holderOfAvailableConnections = holderOfAvailableConnections;
    }

    public static DataBaseConnectionPool create() {
        return SingletonInitializer.DATA_BASE_CONNECTION_POOL;
    }

    @Override
    public int findAmountOfAvailableConnections() {
        try {
            final BlockingQueue<Connection> availableConnections = this.holderOfAvailableConnections.get();
            return availableConnections.size();
        } catch (final ExecutionException executionException) {
            throw (DataBaseConnectionPoolFullingException) executionException.getCause();
        } catch (final InterruptedException cause) {
            throw new DataBaseConnectionPoolFindingAmountAvailableConnectionsException(
                    EXCEPTION_DESCRIPTION_CAUSED_INTERRUPTION, cause);
        }
    }

    @Override
    public Optional<Connection> findAvailableConnection() {
        try {
            final BlockingQueue<Connection> availableConnections = this.holderOfAvailableConnections.get();
            final Connection foundConnection = availableConnections.poll(WAITING_CONNECTION_UNITS_AMOUNT,
                    TIME_UNIT_OF_WAITING_OF_CONNECTION);
            LOGGER.info(foundConnection != null ? MESSAGE_SUCCESS_ACCESS_CONNECTION
                    : MESSAGE_ERRONEOUSLY_ACCESS_CONNECTION);
            return ofNullable(foundConnection);
        } catch (final ExecutionException executionException) {
            throw (DataBaseConnectionPoolFullingException) executionException.getCause();
        } catch (final InterruptedException cause) {
            throw new DataBaseConnectionPoolAccessConnectionException(EXCEPTION_DESCRIPTION_CAUSED_INTERRUPTION, cause);
        }
    }

    @Override
    public void returnConnectionToPool(final Connection returnedConnection) {
        try {
            final BlockingQueue<Connection> availableConnections = this.holderOfAvailableConnections.get();
            final boolean connectionWasReturned = availableConnections.offer(returnedConnection);
            if (!connectionWasReturned) {
                throw new DataBaseConnectionPoolFullException("Data base connection's pool is already full.");
            }
            LOGGER.info(MESSAGE_SUCCESS_RETURN_CONNECTION);
        } catch (final ExecutionException executionException) {
            throw (DataBaseConnectionPoolFullingException) executionException.getCause();
        } catch (final InterruptedException cause) {
            throw new DataBaseConnectionPoolReturningConnectionException(
                    EXCEPTION_DESCRIPTION_CAUSED_INTERRUPTION, cause);
        }
    }

    @Override
    public void close() {
        try {
            final BlockingQueue<Connection> availableConnections = this.holderOfAvailableConnections.get();
            closeConnections(availableConnections, availableConnections.size());
            LOGGER.info(MESSAGE_SUCCESS_CLOSE_POOL);
        } catch (final ExecutionException executionException) {
            throw (DataBaseConnectionPoolFullingException) executionException.getCause();
        } catch (final InterruptedException cause) {
            throw new DataBaseConnectionPoolClosingException(EXCEPTION_DESCRIPTION_CAUSED_INTERRUPTION, cause);
        }
    }

    private static void closeConnections(final BlockingQueue<Connection> fulledConnections,
                                         final int closedConnectionsAmount) {
        rangeClosed(1, closedConnectionsAmount).forEach(i -> {
            try {
                final Connection closedConnection = fulledConnections.take();
                closedConnection.close();
                LOGGER.info(format(MESSAGE_TEMPLATE_SUCCESS_CLOSE_CONNECTION, i));
            } catch (final InterruptedException | SQLException cause) {
                LOGGER.warning(format(MESSAGE_TEMPLATE_ERRONEOUSLY_CLOSE_CONNECTION, i));
                throw new DataBaseConnectionPoolClosingException(cause);
            }
        });
    }

    private static final class SingletonInitializer {
        private static final PropertyFileReader PROPERTY_FILE_READER = PropertyFileReaderImplementation.create();
        private static final String PATH_FILE_WITH_DATA_BASE_PROPERTIES = "./src/main/resources/db.properties";
        private static final File FILE_WITH_DATA_BASE_PROPERTIES = new File(PATH_FILE_WITH_DATA_BASE_PROPERTIES);
        private static final String PROPERTY_KEY_DRIVER_CLASS_NAME = "db.driverClassName";
        private static final String PROPERTY_KEY_URL = "db.url";
        private static final String PROPERTY_KEY_USER = "db.user";
        private static final String PROPERTY_KEY_PASSWORD = "db.password";
        private static final String PROPERTY_KEY_INVOLVED_CONNECTIONS_AMOUNT = "db.involvedConnectionsAmount";
        public static final DataBaseConnectionPool DATA_BASE_CONNECTION_POOL = createDataBaseConnectionPool();


        public static DataBaseConnectionPool createDataBaseConnectionPool() {
            final Properties dataBaseProperties = PROPERTY_FILE_READER.read(FILE_WITH_DATA_BASE_PROPERTIES);
            final String driverClassName = dataBaseProperties.getProperty(PROPERTY_KEY_DRIVER_CLASS_NAME);
            final String url = dataBaseProperties.getProperty(PROPERTY_KEY_URL);
            final String user = dataBaseProperties.getProperty(PROPERTY_KEY_USER);
            final String password = dataBaseProperties.getProperty(PROPERTY_KEY_PASSWORD);
            final int involvedConnectionsAmount = readInvolvedConnectionsAmount(dataBaseProperties);

            loadDriver(driverClassName);
            final ExecutorService executorService = newSingleThreadExecutor();
            final PoolFuller poolFuller = new PoolFuller(url, user, password, involvedConnectionsAmount);
            final Future<BlockingQueue<Connection>> holderOfAvailableConnections = executorService.submit(poolFuller);
            executorService.shutdown();

            return new DataBaseConnectionPoolImplementation(holderOfAvailableConnections);
        }

        private static void loadDriver(final String driverClassName) {
            try {
                forName(driverClassName);
            } catch (final ClassNotFoundException cause) {
                throw new DataBaseConnectionPoolCreatingException("Class '" + driverClassName
                        + "' of driver wasn't found.", cause);
            }
        }

        private static int readInvolvedConnectionsAmount(final Properties dataBaseProperties) {
            final String involvedConnectionsAmount = dataBaseProperties
                    .getProperty(PROPERTY_KEY_INVOLVED_CONNECTIONS_AMOUNT);
            return parseInt(involvedConnectionsAmount);
        }

        private static final class PoolFuller implements Callable<BlockingQueue<Connection>> {
            private static final String MESSAGE_TEMPLATE_SUCCESSFULLY_FULLING_CONNECTION = "Connection #%d was "
                    + "successfully fulled.";
            private static final String MESSAGE_TEMPLATE_ERRONEOUSLY_FULLING_CONNECTION = "Fulling connection #%d was "
                    + "erroneously.";

            private static final String MESSAGE_TEMPLATE_STARTING_CLOSING_CONNECTIONS = "Error arose while fulling pool. "
                    + "Closing %d connections is starting.";
            private static final String MESSAGE_TEMPLATE_SUCCESS_FULLING = "Pool of connections to data base was "
                    + "successfully fulled by %d connections.";

            private final String dataBaseUrl;
            private final String dataBaseUserName;
            private final String dataBasePassword;
            private final int involvedConnectionsAmount;

            public PoolFuller(final String dataBaseUrl, final String dataBaseUserName, final String dataBasePassword,
                              final int involvedConnectionsAmount) {
                this.dataBaseUrl = dataBaseUrl;
                this.dataBaseUserName = dataBaseUserName;
                this.dataBasePassword = dataBasePassword;
                this.involvedConnectionsAmount = involvedConnectionsAmount;
            }

            @Override
            public BlockingQueue<Connection> call() {
                final BlockingQueue<Connection> fulledConnections = new ArrayBlockingQueue<>(
                        this.involvedConnectionsAmount);
                try {
                    range(0, this.involvedConnectionsAmount).forEach(i -> {
                        try {
                            final Connection currentFulledConnection = getConnection(this.dataBaseUrl,
                                    this.dataBaseUserName, this.dataBasePassword);
                            fulledConnections.add(currentFulledConnection);
                            LOGGER.info(format(MESSAGE_TEMPLATE_SUCCESSFULLY_FULLING_CONNECTION, i + 1));
                        } catch (final SQLException cause) {
                            LOGGER.warning(format(MESSAGE_TEMPLATE_ERRONEOUSLY_FULLING_CONNECTION, i + 1));
                            throw new DataBaseConnectionPoolFullingException(i, cause);
                        }
                    });
                } catch (final DataBaseConnectionPoolFullingException fullingException) {
                    final int closedConnectionsAmount = fullingException.getLastFulledConnectionIndex();
                    LOGGER.info(format(MESSAGE_TEMPLATE_STARTING_CLOSING_CONNECTIONS, closedConnectionsAmount));
                    closeConnections(fulledConnections, closedConnectionsAmount);
                    throw fullingException;
                }
                LOGGER.info(format(MESSAGE_TEMPLATE_SUCCESS_FULLING, this.involvedConnectionsAmount));
                return fulledConnections;
            }
        }
    }
}
