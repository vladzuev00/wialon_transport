package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.reduceddata;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.reduceddata.ResponseReducedDataPackage;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static by.zuevvlad.wialontransport.wialonpackage.reduceddata.ResponseReducedDataPackage.Status.NOT_DEFINED;
import static by.zuevvlad.wialontransport.wialonpackage.reduceddata.ResponseReducedDataPackage.Status.PACKAGE_FIX_SUCCESS;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class ResponseReducedDataPackageDeserializerTest {
    private final PackageDeserializer packageDeserializer;

    public ResponseReducedDataPackageDeserializerTest() {
        this.packageDeserializer = ResponseReducedDataPackageDeserializer.create();
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<PackageDeserializer> createdDeserializers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(ResponseReducedDataPackageDeserializer.create());
                } catch (final InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdDeserializers.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfDeserializers = createdDeserializers.stream().distinct().count();
        final long expectedAmountOfDeserializers = 1;
        assertEquals(expectedAmountOfDeserializers, actualAmountOfDeserializers);
    }

    @Test
    public void packageShouldBeDeserialized() {
        final String givenDeserialized = "#ASD#1\r\n";
        final Package actual = this.packageDeserializer.deserialize(givenDeserialized);
        final Package expected = new ResponseReducedDataPackage(PACKAGE_FIX_SUCCESS);
        assertEquals(expected, actual);
    }

    @Test
    public void packageShouldBeDeserializedWithNotDefinedValue() {
        final String givenDeserialized = "#ASD#\r\n";
        final Package actual = this.packageDeserializer.deserialize(givenDeserialized);
        final Package expected = new ResponseReducedDataPackage(NOT_DEFINED);
        assertEquals(expected, actual);
    }
}
