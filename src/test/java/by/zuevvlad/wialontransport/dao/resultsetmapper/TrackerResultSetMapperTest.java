package by.zuevvlad.wialontransport.dao.resultsetmapper;

import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.entity.TrackerEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class TrackerResultSetMapperTest {
    private ResultSetMapper<TrackerEntity> deviceResultSetMapper;

    @Mock
    private ResultRowMapper<TrackerEntity> mockedDeviceResultRowMapper;

    @Mock
    private ResultSet mockedResultSet;

    @Captor
    private ArgumentCaptor<ResultSet> resultSetArgumentCaptor;

    @Before
    public void initializeResultSetMapper()
            throws Exception {
        this.deviceResultSetMapper = createDeviceResultSetMapper(this.mockedDeviceResultRowMapper);
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<ResultSetMapper<TrackerEntity>> createdSetMappers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdSetMappers.put(TrackerResultSetMapper.create());
                } catch (final InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdSetMappers.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfSetMappers = createdSetMappers.stream().distinct().count();
        final long expectedAmountOfSetMappers = 1;
        assertEquals(expectedAmountOfSetMappers, actualAmountOfSetMappers);
    }

//    @Test
//    public void devicesShouldBeMapped()
//            throws SQLException {
//        when(this.mockedResultSet.next())
//                .thenReturn(true)
//                .thenReturn(true)
//                .thenReturn(false);
//
//        final long firstGivenId = 255;
//        final String firstGivenImei = "first-imei";
//        final String firstGivenPassword = "first-password";
//        final Tracker firstTracker = new Tracker(firstGivenId, firstGivenImei, firstGivenPassword);
//
//        final long secondGivenId = 256;
//        final String secondGivenImei = "second-imei";
//        final String secondGivenPassword = "second-password";
//        final Tracker secondTracker = new Tracker(secondGivenId, secondGivenImei, secondGivenPassword);
//
//        when(this.mockedDeviceResultRowMapper.map(any(ResultSet.class)))
//                .thenReturn(firstTracker)
//                .thenReturn(secondTracker);
//
//        final List<Tracker> actualTrackers = this.deviceResultSetMapper.map(this.mockedResultSet);
//        final List<Tracker> expectedTrackers = List.of(firstTracker, secondTracker);
//        assertEquals(expectedTrackers, actualTrackers);
//
//        verify(this.mockedResultSet, times(3)).next();
//        verify(this.mockedDeviceResultRowMapper, times(2)).map(this.resultSetArgumentCaptor.capture());
//        final List<ResultSet> expectedCapturedResultSetArguments = List.of(this.mockedResultSet, this.mockedResultSet);
//        assertEquals(expectedCapturedResultSetArguments, this.resultSetArgumentCaptor.getAllValues());
//    }

    @Test
    public void emptyListOfDevicesShouldBeMapped()
            throws SQLException {
        when(this.mockedResultSet.next()).thenReturn(false);

        this.deviceResultSetMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).next();
    }

    private static ResultSetMapper<TrackerEntity> createDeviceResultSetMapper(
            final ResultRowMapper<TrackerEntity> deviceResultRowMapper)
            throws Exception {
        final Class<? extends ResultSetMapper<TrackerEntity>> resultSetMapperClass = TrackerResultSetMapper.class;
        final Constructor<? extends ResultSetMapper<TrackerEntity>> resultSetMapperConstructor
                = resultSetMapperClass.getDeclaredConstructor(ResultRowMapper.class);
        resultSetMapperConstructor.setAccessible(true);
        try {
            return resultSetMapperConstructor.newInstance(deviceResultRowMapper);
        } finally {
            resultSetMapperConstructor.setAccessible(false);
        }
    }
}
