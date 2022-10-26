package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.message;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.message.ResponseMessagePackage;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static by.zuevvlad.wialontransport.wialonpackage.message.ResponseMessagePackage.Status.SUCCESS;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class ResponseMessagePackageDeserializerTest {
    private final PackageDeserializer packageDeserializer;

    public ResponseMessagePackageDeserializerTest() {
        this.packageDeserializer = ResponseMessagePackageDeserializer.create();
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<PackageDeserializer> createdDeserializers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(ResponseMessagePackageDeserializer.create());
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
    public void responseMessagePackageShouldBeDeserialized() {
        final String givenDeserialized = "#AM#1\r\n";

        final Package actual = this.packageDeserializer.deserialize(givenDeserialized);

        final ResponseMessagePackage expected = new ResponseMessagePackage(SUCCESS);

        assertEquals(expected, actual);
    }
}
