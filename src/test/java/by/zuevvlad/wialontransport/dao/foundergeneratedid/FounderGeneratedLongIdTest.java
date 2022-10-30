package by.zuevvlad.wialontransport.dao.foundergeneratedid;

import by.zuevvlad.wialontransport.dao.foundergeneratedid.exception.FindingGeneratedIdByDataBaseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static java.util.List.of;

@RunWith(MockitoJUnitRunner.class)
public final class FounderGeneratedLongIdTest {
    private static final String COLUMN_NAME_ID = "id";

    @Mock
    private Statement mockedStatement;

    @Mock
    private ResultSet mockedResultSet;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<FounderGeneratedId<Long>> createdFoundersGeneratedId
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdFoundersGeneratedId.put(FounderGeneratedLongId.create());
                } catch (InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdFoundersGeneratedId.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfCreatedFounderGeneratedId = createdFoundersGeneratedId.stream().distinct().count();
        final long expectedAmountOfCreatedFounderGeneratedId = 1;
        assertEquals(expectedAmountOfCreatedFounderGeneratedId, actualAmountOfCreatedFounderGeneratedId);
    }

    @Test
    public void lastGeneratedIdShouldBeFound()
            throws Exception {
        when(this.mockedStatement.getGeneratedKeys()).thenReturn(this.mockedResultSet);
        when(this.mockedResultSet.next()).thenReturn(true);

        final long expected = 255;
        when(this.mockedResultSet.getLong(anyString())).thenReturn(expected);

        final FounderGeneratedId<Long> founderGeneratedId = this.createFounderGeneratedId();
        final Optional<Long> optionalActual = founderGeneratedId
                .findLastGeneratedId(this.mockedStatement, COLUMN_NAME_ID);
        assertTrue(optionalActual.isPresent());

        final long actual = optionalActual.get();
        assertEquals(expected, actual);

        verify(this.mockedStatement, times(1)).getGeneratedKeys();
        verify(this.mockedResultSet, times(1)).next();

        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());
        assertEquals(COLUMN_NAME_ID, this.stringArgumentCaptor.getValue());
    }

    @Test
    public void lastGeneratedIdShouldNotBeFound()
            throws Exception {
        when(this.mockedStatement.getGeneratedKeys()).thenReturn(this.mockedResultSet);
        when(this.mockedResultSet.next()).thenReturn(false);

        final FounderGeneratedId<Long> founderGeneratedId = this.createFounderGeneratedId();
        final Optional<Long> optionalActual = founderGeneratedId
                .findLastGeneratedId(this.mockedStatement, COLUMN_NAME_ID);
        assertTrue(optionalActual.isEmpty());

        verify(this.mockedStatement, times(1)).getGeneratedKeys();
        verify(this.mockedResultSet, times(1)).next();
    }

    @Test(expected = FindingGeneratedIdByDataBaseException.class)
    public void lastGeneratedIdShouldNotBeFoundBecauseOfExceptionDuringGettingGeneratedKeys()
            throws Exception {
        when(this.mockedStatement.getGeneratedKeys()).thenThrow(SQLException.class);

        final FounderGeneratedId<Long> founderGeneratedId = this.createFounderGeneratedId();
        founderGeneratedId.findLastGeneratedId(this.mockedStatement, COLUMN_NAME_ID);

        verify(this.mockedStatement, times(1)).getGeneratedKeys();
    }

    @Test(expected = FindingGeneratedIdByDataBaseException.class)
    public void lastGeneratedIdShouldNotBeFoundBecauseOfExceptionDuringCallingNextOnResultSet()
            throws Exception {
        when(this.mockedStatement.getGeneratedKeys()).thenReturn(this.mockedResultSet);
        when(this.mockedResultSet.next()).thenThrow(SQLException.class);

        final FounderGeneratedId<Long> founderGeneratedId = this.createFounderGeneratedId();
        founderGeneratedId.findLastGeneratedId(this.mockedStatement, COLUMN_NAME_ID);

        verify(this.mockedStatement, times(1)).getGeneratedKeys();
        verify(this.mockedResultSet, times(1)).next();
    }

    @Test(expected = FindingGeneratedIdByDataBaseException.class)
    public void lastGeneratedIdShouldNotBeFoundBecauseOfExceptionDuringGettingLong()
            throws Exception {
        when(this.mockedStatement.getGeneratedKeys()).thenReturn(this.mockedResultSet);
        when(this.mockedResultSet.next()).thenReturn(true);
        when(this.mockedResultSet.getLong(anyString())).thenThrow(SQLException.class);

        final FounderGeneratedId<Long> founderGeneratedId = this.createFounderGeneratedId();
        founderGeneratedId.findLastGeneratedId(this.mockedStatement, COLUMN_NAME_ID);

        verify(this.mockedStatement, times(1)).getGeneratedKeys();
        verify(this.mockedResultSet, times(1)).next();
        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());

        assertEquals(COLUMN_NAME_ID, this.stringArgumentCaptor.getValue());
    }

    @Test
    public void allGeneratedIdsShouldBeFound()
            throws Exception {
        when(this.mockedStatement.getGeneratedKeys()).thenReturn(this.mockedResultSet);
        when(this.mockedResultSet.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(this.mockedResultSet.getLong(anyString()))
                .thenReturn(1L)
                .thenReturn(2L)
                .thenReturn(3L);

        final FounderGeneratedId<Long> founderGeneratedId = this.createFounderGeneratedId();
        final List<Long> actual = founderGeneratedId.findAllGeneratedIds(this.mockedStatement, COLUMN_NAME_ID);
        final List<Long> expected = of(1L, 2L, 3L);
        assertEquals(expected, actual);

        verify(this.mockedStatement, times(1)).getGeneratedKeys();
        verify(this.mockedResultSet, times(4)).next();
        verify(this.mockedResultSet, times(3)).getLong(this.stringArgumentCaptor.capture());

        final List<String> expectedCapturedStringArguments = of(COLUMN_NAME_ID, COLUMN_NAME_ID, COLUMN_NAME_ID);
        assertEquals(expectedCapturedStringArguments, this.stringArgumentCaptor.getAllValues());
    }

    @Test
    public void allGeneratedIdsShouldNotBeFound()
            throws Exception {
        when(this.mockedStatement.getGeneratedKeys()).thenReturn(this.mockedResultSet);
        when(this.mockedResultSet.next()).thenReturn(false);

        final FounderGeneratedId<Long> founderGeneratedId = this.createFounderGeneratedId();
        final List<Long> generatedIds = founderGeneratedId.findAllGeneratedIds(this.mockedStatement, COLUMN_NAME_ID);
        assertTrue(generatedIds.isEmpty());

        verify(this.mockedStatement, times(1)).getGeneratedKeys();
        verify(this.mockedResultSet, times(1)).next();
    }

    @Test(expected = FindingGeneratedIdByDataBaseException.class)
    public void allGeneratedIdsShouldNotBeFoundBecauseOfExceptionDuringGettingGeneratedKeys()
            throws Exception {
        when(this.mockedStatement.getGeneratedKeys()).thenThrow(SQLException.class);

        final FounderGeneratedId<Long> founderGeneratedId = this.createFounderGeneratedId();
        founderGeneratedId.findAllGeneratedIds(this.mockedStatement, COLUMN_NAME_ID);

        verify(this.mockedStatement, times(1)).getGeneratedKeys();
    }

    @Test(expected = FindingGeneratedIdByDataBaseException.class)
    public void allGeneratedIdsShouldNotBeFoundBecauseOfExceptionDuringCallNextOnResultSet()
            throws Exception {
        when(this.mockedStatement.getGeneratedKeys()).thenReturn(this.mockedResultSet);
        when(this.mockedResultSet.next()).thenThrow(SQLException.class);

        final FounderGeneratedId<Long> founderGeneratedId = this.createFounderGeneratedId();
        founderGeneratedId.findAllGeneratedIds(this.mockedStatement, COLUMN_NAME_ID);

        verify(this.mockedStatement, times(1)).getGeneratedKeys();
        verify(this.mockedResultSet, times(1)).next();
    }

    @Test(expected = FindingGeneratedIdByDataBaseException.class)
    public void allGeneratedIdsShouldNotBeFoundBecauseOfExceptionDuringGettingLongFromResultSet()
            throws Exception {
        when(this.mockedStatement.getGeneratedKeys()).thenReturn(this.mockedResultSet);
        when(this.mockedResultSet.next()).thenReturn(true);
        when(this.mockedResultSet.getLong(anyString())).thenThrow(SQLException.class);

        final FounderGeneratedId<Long> founderGeneratedId = this.createFounderGeneratedId();
        founderGeneratedId.findAllGeneratedIds(this.mockedStatement, COLUMN_NAME_ID);

        verify(this.mockedStatement, times(1)).getGeneratedKeys();
        verify(this.mockedResultSet, times(1)).next();
        verify(this.mockedResultSet, times(1)).getLong(this.stringArgumentCaptor.capture());

        assertEquals(COLUMN_NAME_ID, this.stringArgumentCaptor.getValue());
    }

    private FounderGeneratedId<Long> createFounderGeneratedId()
            throws Exception {
        final Class<? extends FounderGeneratedId<Long>> classFounderGeneratedId = FounderGeneratedLongId.class;
        final Constructor<? extends FounderGeneratedId<Long>> constructorFounderGeneratedId
                = classFounderGeneratedId.getDeclaredConstructor();
        constructorFounderGeneratedId.setAccessible(true);
        try {
            return constructorFounderGeneratedId.newInstance();
        } finally {
            constructorFounderGeneratedId.setAccessible(false);
        }
    }
}
