package by.zuevvlad.wialontransport.netty.handler;

import by.zuevvlad.wialontransport.netty.handler.handlingchain.PackageHandler;
import by.zuevvlad.wialontransport.netty.handler.handlingchain.component.StarterInboundPackageHandler;
import by.zuevvlad.wialontransport.wialonpackage.ping.RequestPingPackage;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class StarterInboundPackageHandlerTest {
    private final PackageHandler packageHandler;

    public StarterInboundPackageHandlerTest() {
        this.packageHandler = StarterInboundPackageHandler.create();
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<PackageHandler> createdAnswerers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdAnswerers.put(StarterInboundPackageHandler.create());
                } catch (final InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdAnswerers.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfAnswerers = createdAnswerers.stream().distinct().count();
        final long expectedAmountOfAnswerers = 1;
        assertEquals(expectedAmountOfAnswerers, actualAmountOfAnswerers);
    }

//    @Test(expected = UnsupportedOperationException.class)
//    public void answererShouldNotAnswerOnRequestPackage() {
//        final RequestPingPackage requestPingPackage = new RequestPingPackage();
//        this.packageHandler.handleIndependently(requestPingPackage);
//    }
}
