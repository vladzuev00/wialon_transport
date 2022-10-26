package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.ping;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public final class ResponsePingPackageDeserializerTest {
    private final PackageDeserializer packageDeserializer;

    public ResponsePingPackageDeserializerTest() {
        this.packageDeserializer = ResponsePingPackageDeserializer.create();
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<PackageDeserializer> createdDeserializers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(ResponsePingPackageDeserializer.create());
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
    public void responsePingPackageShouldBeDeserialized() {
        final String deserialized = "#AP#\r\n";
        final Package resultPackage = this.packageDeserializer.deserialize(deserialized);
        assertNotNull(resultPackage);
    }
}
