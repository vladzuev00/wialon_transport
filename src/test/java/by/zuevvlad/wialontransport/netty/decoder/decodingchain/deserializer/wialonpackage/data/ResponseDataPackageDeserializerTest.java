package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.data;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.data.ResponseDataPackage;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static by.zuevvlad.wialontransport.wialonpackage.data.ResponseDataPackage.Status.PACKAGE_FIX_SUCCESS;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class ResponseDataPackageDeserializerTest {
    private final PackageDeserializer packageDeserializer;

    public ResponseDataPackageDeserializerTest() {
        this.packageDeserializer = ResponseDataPackageDeserializer.create();
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<PackageDeserializer> createdDeserializers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(ResponseDataPackageDeserializer.create());
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
    public void responseDataPackageShouldBeDeserialized() {
        final String givenDeserialized = "#AD#1\r\n";

        final Package actual = this.packageDeserializer.deserialize(givenDeserialized);

        final ResponseDataPackage expected = new ResponseDataPackage(PACKAGE_FIX_SUCCESS);

        assertEquals(expected, actual);
    }
}
