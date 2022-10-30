package by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper;

import by.zuevvlad.wialontransport.entity.DataEntity;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class DataResultRowMapperIntegrationTest {

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<ResultRowMapper<DataEntity>> createdRowMappers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdRowMappers.put(DataResultRowMapper.create());
                } catch (InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdRowMappers.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfRowMappers = createdRowMappers.stream().distinct().count();
        final long expectedAmountOfRowMappers = 1;
        assertEquals(expectedAmountOfRowMappers, actualAmountOfRowMappers);
    }
}
