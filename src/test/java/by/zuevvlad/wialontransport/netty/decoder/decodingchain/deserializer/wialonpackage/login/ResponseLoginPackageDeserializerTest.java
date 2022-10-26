package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.login;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage.Status.NOT_DEFINED;
import static by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage.Status.SUCCESS_AUTHORIZATION;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class ResponseLoginPackageDeserializerTest {
    private final PackageDeserializer packageDeserializer;

    public ResponseLoginPackageDeserializerTest() {
        this.packageDeserializer = ResponseLoginPackageDeserializer.create();
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<PackageDeserializer> createdDeserializers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(ResponseLoginPackageDeserializer.create());
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
    public void responseLoginPackageShouldBeDeserialized() {
        final String deserialized = "#AL#1\r\n";
        final Package actual = this.packageDeserializer.deserialize(deserialized);
        final ResponseLoginPackage expected = new ResponseLoginPackage(SUCCESS_AUTHORIZATION);
        assertEquals(expected, actual);
    }

    @Test
    public void responseLoginPackageShouldBeDeserializedWithNotDefinedStatus() {
        final String deserialized = "#AL#\r\n";
        final Package actual = this.packageDeserializer.deserialize(deserialized);
        final ResponseLoginPackage expected = new ResponseLoginPackage(NOT_DEFINED);
        assertEquals(expected, actual);
    }
}
