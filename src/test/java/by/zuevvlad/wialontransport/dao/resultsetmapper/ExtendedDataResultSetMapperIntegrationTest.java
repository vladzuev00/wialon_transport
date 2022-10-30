package by.zuevvlad.wialontransport.dao.resultsetmapper;

import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class ExtendedDataResultSetMapperIntegrationTest {

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<ResultSetMapper<ExtendedDataEntity>> createdSetMappers
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdSetMappers.put(ExtendedDataResultSetMapper.create());
                } catch (InterruptedException cause) {
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
}
