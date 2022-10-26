package by.zuevvlad.wialontransport.dao.foundergeneratedid;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class FounderGeneratedLongIdIntegrationTest {

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
}
