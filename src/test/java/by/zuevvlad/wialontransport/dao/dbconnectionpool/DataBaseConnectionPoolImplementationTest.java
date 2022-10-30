package by.zuevvlad.wialontransport.dao.dbconnectionpool;

import by.zuevvlad.wialontransport.dao.dbconnectionpool.exception.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class DataBaseConnectionPoolImplementationTest {
    private static final int INVOLVED_CONNECTIONS_AMOUNT = 20;

    private static final long WAITING_CONNECTION_UNITS_AMOUNT = 1;
    private static final TimeUnit TIME_UNIT_OF_WAITING_OF_CONNECTION = SECONDS;

    @Mock
    private Connection mockedConnection;

    @Mock
    private Future<BlockingQueue<Connection>> mockedFutureAvailableConnections;

    @Mock
    private ExecutionException mockedExecutionException;

    @Mock
    private BlockingQueue<Connection> mockedAvailableConnections;

    @Captor
    private ArgumentCaptor<Connection> connectionArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Captor
    private ArgumentCaptor<TimeUnit> timeUnitArgumentCaptor;

    @Test
    public void amountOfAvailableConnectionsShouldBeFound()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenReturn(this.mockedAvailableConnections);
        when(this.mockedAvailableConnections.size()).thenReturn(INVOLVED_CONNECTIONS_AMOUNT);

        final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool(
                this.mockedFutureAvailableConnections);
        final int actual = dataBaseConnectionPool.findAmountOfAvailableConnections();
        assertEquals(INVOLVED_CONNECTIONS_AMOUNT, actual);

        verify(this.mockedFutureAvailableConnections, times(1)).get();
        verify(this.mockedAvailableConnections, times(1)).size();
    }

    @Test
    public void amountOfAvailableConnectionShouldNotBeFoundBecauseOfExecutionException()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenThrow(this.mockedExecutionException);

        final DataBaseConnectionPoolFullingException poolFullingException
                = new DataBaseConnectionPoolFullingException(0);
        when(this.mockedExecutionException.getCause()).thenReturn(poolFullingException);

        final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool(
                this.mockedFutureAvailableConnections);
        try {
            dataBaseConnectionPool.findAvailableConnection();
        } catch (final Exception exception) {
            assertSame(poolFullingException, exception);
        }

        verify(this.mockedFutureAvailableConnections, times(1)).get();
        verify(this.mockedExecutionException, times(1)).getCause();
    }

    @Test(expected = DataBaseConnectionPoolFindingAmountAvailableConnectionsException.class)
    public void amountOfAvailableConnectionShouldNotBeFoundBecauseOfInterruptedException()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenThrow(InterruptedException.class);
        final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool(
                this.mockedFutureAvailableConnections);
        dataBaseConnectionPool.findAvailableConnection();

        verify(this.mockedFutureAvailableConnections, times(1)).get();
    }

    @Test
    public void connectionShouldBeAccessedSuccessfully()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenReturn(this.mockedAvailableConnections);
        when(this.mockedAvailableConnections.poll(anyLong(), any())).thenReturn(this.mockedConnection);
        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool(
                this.mockedFutureAvailableConnections)) {
            final Optional<Connection> optionalConnection = dataBaseConnectionPool.findAvailableConnection();
            assertTrue(optionalConnection.isPresent());
            assertEquals(this.mockedConnection, optionalConnection.get());
        }

        verify(this.mockedFutureAvailableConnections, times(1)).get();

        verify(this.mockedAvailableConnections, times(1))
                .poll(this.longArgumentCaptor.capture(), this.timeUnitArgumentCaptor.capture());
        assertEquals(WAITING_CONNECTION_UNITS_AMOUNT, this.longArgumentCaptor.getValue().longValue());
        assertSame(TIME_UNIT_OF_WAITING_OF_CONNECTION, this.timeUnitArgumentCaptor.getValue());
    }

    @Test
    public void connectionShouldNotBeAccessed()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenReturn(this.mockedAvailableConnections);
        when(this.mockedAvailableConnections.poll(anyLong(), any(TimeUnit.class))).thenReturn(null);
        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool(
                this.mockedFutureAvailableConnections)) {
            final Optional<Connection> optionalConnection = dataBaseConnectionPool.findAvailableConnection();
            assertTrue(optionalConnection.isEmpty());
        }

        verify(this.mockedFutureAvailableConnections, times(1)).get();

        verify(this.mockedAvailableConnections, times(1))
                .poll(this.longArgumentCaptor.capture(), this.timeUnitArgumentCaptor.capture());
        assertEquals(WAITING_CONNECTION_UNITS_AMOUNT, this.longArgumentCaptor.getValue().longValue());
        assertSame(TIME_UNIT_OF_WAITING_OF_CONNECTION, this.timeUnitArgumentCaptor.getValue());
    }

    @Test
    public void connectionShouldNotBeAccessedBecauseOfExecutionException()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenThrow(this.mockedExecutionException);

        final DataBaseConnectionPoolFullingException poolFullingException
                = new DataBaseConnectionPoolFullingException(0);
        when(this.mockedExecutionException.getCause()).thenReturn(poolFullingException);

        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool(
                this.mockedFutureAvailableConnections)) {
            dataBaseConnectionPool.findAvailableConnection();
        } catch (final Exception exception) {
            assertSame(poolFullingException, exception);
        }

        verify(this.mockedFutureAvailableConnections, times(1)).get();
        verify(this.mockedExecutionException, times(1)).getCause();
    }

    @Test(expected = DataBaseConnectionPoolAccessConnectionException.class)
    public void connectionShouldNotBeAccessedBecauseOfInterruptedException()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenThrow(InterruptedException.class);
        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool(
                this.mockedFutureAvailableConnections)) {
            dataBaseConnectionPool.findAvailableConnection();
        }

        verify(this.mockedFutureAvailableConnections, times(1)).get();
    }

    @Test
    public void connectionShouldBeSuccessfullyReturned()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenReturn(this.mockedAvailableConnections);
        when(this.mockedAvailableConnections.size()).thenReturn(INVOLVED_CONNECTIONS_AMOUNT - 1);
        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool(
                this.mockedFutureAvailableConnections)) {
            dataBaseConnectionPool.returnConnectionToPool(this.mockedConnection);
        }

        verify(this.mockedFutureAvailableConnections, times(1)).get();
        verify(this.mockedAvailableConnections, times(1)).size();

        verify(this.mockedAvailableConnections, times(1))
                .add(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test(expected = DataBaseConnectionPoolFullException.class)
    public void connectionShouldNotBeSuccessfullyReturnedBecausePoolIsFull()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenReturn(this.mockedAvailableConnections);
        when(this.mockedAvailableConnections.size()).thenReturn(INVOLVED_CONNECTIONS_AMOUNT);

        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool(
                this.mockedFutureAvailableConnections)) {
            dataBaseConnectionPool.returnConnectionToPool(this.mockedConnection);
        }

        verify(this.mockedFutureAvailableConnections, times(1)).get();
        verify(this.mockedAvailableConnections, times(1)).size();

        verify(this.mockedAvailableConnections, times(0))
                .add(this.connectionArgumentCaptor.capture());
        assertSame(this.mockedConnection, this.connectionArgumentCaptor.getValue());
    }

    @Test
    public void connectionShouldNotBeSuccessfullyReturnedBecauseOfExecutionException()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenThrow(this.mockedExecutionException);

        final DataBaseConnectionPoolFullingException poolFullingException
                = new DataBaseConnectionPoolFullingException(0);
        when(this.mockedExecutionException.getCause()).thenReturn(poolFullingException);

        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool(
                this.mockedFutureAvailableConnections)) {
            dataBaseConnectionPool.returnConnectionToPool(this.mockedConnection);
        } catch (final Exception exception) {
            assertSame(poolFullingException, exception);
        }

        verify(this.mockedFutureAvailableConnections, times(1)).get();
        verify(this.mockedExecutionException, times(1)).getCause();
    }

    @Test(expected = DataBaseConnectionPoolReturningConnectionException.class)
    public void connectionShouldNotBeSuccessfullyReturnedBecauseOfInterruptedException()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenThrow(InterruptedException.class);

        try (final DataBaseConnectionPool dataBaseConnectionPool = this.createDataBaseConnectionPool(
                this.mockedFutureAvailableConnections)) {
            dataBaseConnectionPool.returnConnectionToPool(this.mockedConnection);
        }

        verify(this.mockedFutureAvailableConnections, times(1)).get();
    }

    @Test
    public void poolShouldBeClosed()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenReturn(this.mockedAvailableConnections);
        when(this.mockedAvailableConnections.size()).thenReturn(5);
        when(this.mockedAvailableConnections.take())
                .thenReturn(this.mockedConnection)
                .thenReturn(this.mockedConnection)
                .thenReturn(this.mockedConnection)
                .thenReturn(this.mockedConnection)
                .thenReturn(this.mockedConnection);

        final DataBaseConnectionPool dataBaseConnectionPool = this
                .createDataBaseConnectionPool(this.mockedFutureAvailableConnections);
        dataBaseConnectionPool.close();

        verify(this.mockedFutureAvailableConnections, times(1)).get();
        verify(this.mockedAvailableConnections, times(1)).size();
        verify(this.mockedAvailableConnections, times(5)).take();
        verify(this.mockedConnection, times(5)).close();
    }

    @Test
    public void poolShouldNotBeClosedBecauseOfExecutionException()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenThrow(this.mockedExecutionException);

        final DataBaseConnectionPoolFullingException poolFullingException
                = new DataBaseConnectionPoolFullingException(0);
        when(this.mockedExecutionException.getCause()).thenReturn(poolFullingException);

        final DataBaseConnectionPool dataBaseConnectionPool = this
                .createDataBaseConnectionPool(this.mockedFutureAvailableConnections);
        try {
            dataBaseConnectionPool.close();
        } catch (final Exception exception) {
            assertSame(poolFullingException, exception);
        }

        verify(this.mockedFutureAvailableConnections, times(1)).get();
        verify(this.mockedExecutionException, times(1)).getCause();
    }

    @Test(expected = DataBaseConnectionPoolClosingException.class)
    public void poolShouldNotBeClosedBecauseOfInterruptedExceptionDuringGettingAvailableConnections()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenThrow(InterruptedException.class);

        final DataBaseConnectionPool dataBaseConnectionPool = this
                .createDataBaseConnectionPool(this.mockedFutureAvailableConnections);
        dataBaseConnectionPool.close();

        verify(this.mockedFutureAvailableConnections, times(1)).get();
    }

    @Test(expected = DataBaseConnectionPoolClosingException.class)
    public void poolShouldNotBeClosedBecauseOfInterruptedExceptionDuringTakingClosedConnection()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenReturn(this.mockedAvailableConnections);
        when(this.mockedAvailableConnections.size()).thenReturn(1);
        when(this.mockedAvailableConnections.take()).thenThrow(InterruptedException.class);

        final DataBaseConnectionPool dataBaseConnectionPool = this
                .createDataBaseConnectionPool(this.mockedFutureAvailableConnections);
        dataBaseConnectionPool.close();

        verify(this.mockedFutureAvailableConnections, times(1)).get();
        verify(this.mockedAvailableConnections, times(1)).size();
        verify(this.mockedAvailableConnections, times(1)).take();
    }

    @Test
    public void poolShouldNotBeClosedBecauseOfInterruptedExceptionDuringClosingConnection()
            throws Exception {
        when(this.mockedFutureAvailableConnections.get()).thenReturn(this.mockedAvailableConnections);
        when(this.mockedAvailableConnections.size()).thenReturn(1);
        when(this.mockedAvailableConnections.take()).thenReturn(this.mockedConnection);
        doThrow(SQLException.class).when(this.mockedConnection).close();

        final DataBaseConnectionPool dataBaseConnectionPool = this
                .createDataBaseConnectionPool(this.mockedFutureAvailableConnections);
        dataBaseConnectionPool.close();

        verify(this.mockedFutureAvailableConnections, times(1)).get();
        verify(this.mockedAvailableConnections, times(1)).size();
        verify(this.mockedAvailableConnections, times(1)).take();
        verify(this.mockedConnection, times(1)).close();
    }

    private DataBaseConnectionPool createDataBaseConnectionPool(
            final Future<BlockingQueue<Connection>> futureFulledConnections)
            throws Exception {
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
