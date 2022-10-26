package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.blackbox;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.blackbox.ResponseBlackBoxPackage;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class ResponseBlackBoxPackageDeserializerTest {
    private final PackageDeserializer packageDeserializer;

    public ResponseBlackBoxPackageDeserializerTest() {
        this.packageDeserializer = ResponseBlackBoxPackageDeserializer.create();
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<PackageDeserializer> createdDeserializers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(ResponseBlackBoxPackageDeserializer.create());
                } catch (InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdDeserializers.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfDeserializers = createdDeserializers.stream()
                .distinct()
                .count();
        final long expectedAmountOfDeserializers = 1;
        assertEquals(expectedAmountOfDeserializers, actualAmountOfDeserializers);
    }

    @Test
    public void responseBlackBoxPackageShouldBeDeserialized() {
        final String givenDeserialized = "#AB#10\r\n";

        final Package actual = this.packageDeserializer.deserialize(givenDeserialized);

        final ResponseBlackBoxPackage expected = new ResponseBlackBoxPackage(10);

        assertEquals(expected, actual);
    }
}
