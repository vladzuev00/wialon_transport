package by.zuevvlad.wialontransport.netty.decoder.decodingchain.component;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.PackageDecoder;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class RequestBlackBoxPackageDecoderTest {

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<PackageDecoder> createdPackageDecoders = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdPackageDecoders.put(RequestBlackBoxPackageDecoder.create());
                } catch (InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdPackageDecoders.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfPackageDecoders = createdPackageDecoders.stream().distinct().count();
        final long expectedAmountOfPackageDecoders = 1;
        assertEquals(expectedAmountOfPackageDecoders, actualAmountOfPackageDecoders);
    }
}
