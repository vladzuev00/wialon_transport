package by.zuevvlad.wialontransport.netty.tostringserializer;

import by.zuevvlad.wialontransport.entity.ExtendedData;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class ExtendedDataToStringSerializerTest {

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<ToStringSerializer<ExtendedData>> createdSerializers
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdSerializers.put(ExtendedDataToStringSerializer.create());
                } catch (InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdSerializers.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfSerializers = createdSerializers.stream().distinct().count();
        final long expectedAmountOfSerializers = 1;
        assertEquals(expectedAmountOfSerializers, actualAmountOfSerializers);
    }

    //нужно клиенту доделать потом
    @Test
    public void a() {
        throw new RuntimeException();
    }
}
