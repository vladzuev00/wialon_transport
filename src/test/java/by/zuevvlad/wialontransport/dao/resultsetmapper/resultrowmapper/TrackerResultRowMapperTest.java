package by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper;

import by.zuevvlad.wialontransport.dao.resultsetmapper.exception.ResultSetMappingException;
import by.zuevvlad.wialontransport.entity.Tracker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class TrackerResultRowMapperTest {
    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_IMEI = "imei";
    private static final String COLUMN_NAME_PASSWORD = "password";

    private final ResultRowMapper<Tracker> deviceResultRowMapper;

    @Mock
    private ResultSet mockedResultSet;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    public TrackerResultRowMapperTest() {
        this.deviceResultRowMapper = TrackerResultRowMapper.create();
    }

    @Test
    public void deviceShouldBeMapped()
            throws SQLException {
        final long givenId = 255;
        when(this.mockedResultSet.getLong(anyString())).thenReturn(givenId);

        final String givenImei = "imei";
        final String givenPassword = "password";
        when(this.mockedResultSet.getString(anyString()))
                .thenReturn(givenImei)
                .thenReturn(givenPassword);

        final Tracker actual = this.deviceResultRowMapper.map(this.mockedResultSet);
//        final Tracker expected = new Tracker(givenId, givenImei, givenPassword);
        assertEquals(null, actual);

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(2)).getString(this.stringArgumentCaptor.capture());

        final List<String> expectedCapturedStringArguments = List.of(COLUMN_NAME_ID, COLUMN_NAME_IMEI,
                COLUMN_NAME_PASSWORD);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void deviceShouldNotBeMappedBecauseOfId()
            throws SQLException {
        when(this.mockedResultSet.getLong(anyString())).thenThrow(SQLException.class);

        this.deviceResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());
        assertEquals(COLUMN_NAME_ID, this.stringArgumentCaptor.getValue());
    }

    @Test(expected = ResultSetMappingException.class)
    public void deviceShouldNotBeMappedBecauseOfImei()
            throws SQLException {
        final long givenId = 255;
        when(this.mockedResultSet.getLong(anyString())).thenReturn(givenId);
        when(this.mockedResultSet.getString(anyString())).thenThrow(SQLException.class);

        this.deviceResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(1)).getString(this.stringArgumentCaptor.capture());
        final List<String> expectedCapturedStringArguments = List.of(COLUMN_NAME_ID, COLUMN_NAME_IMEI);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test(expected = ResultSetMappingException.class)
    public void deviceShouldNotBeMappedBecauseOfPassword()
            throws SQLException {
        final long givenId = 255;
        when(this.mockedResultSet.getLong(anyString())).thenReturn(givenId);

        final String givenImei = "imei";
        when(this.mockedResultSet.getString(anyString()))
                .thenReturn(givenImei)
                .thenThrow(SQLException.class);

        this.deviceResultRowMapper.map(this.mockedResultSet);

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());
        verify(this.mockedResultSet, times(2)).getString(this.stringArgumentCaptor.capture());

        final List<String> expectedCapturedStringArguments = List.of(COLUMN_NAME_ID, COLUMN_NAME_IMEI,
                COLUMN_NAME_PASSWORD);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<ResultRowMapper<Tracker>> createdRowMappers
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdRowMappers.put(TrackerResultRowMapper.create());
                } catch (final InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdRowMappers.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfCreatedRowMappers = createdRowMappers.stream().distinct().count();
        final long expectedAmountOfCreatedRowMappers = 1;
        assertEquals(expectedAmountOfCreatedRowMappers, actualAmountOfCreatedRowMappers);
    }
}
