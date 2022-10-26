package by.zuevvlad.wialontransport.netty.handler;

import by.zuevvlad.wialontransport.entity.Data;
import by.zuevvlad.wialontransport.netty.handler.handlingchain.PackageHandler;
import by.zuevvlad.wialontransport.netty.handler.handlingchain.component.RequestReducedDataPackageHandler;
import by.zuevvlad.wialontransport.service.extendeddata.ExtendedDataService;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.reduceddata.RequestReducedDataPackage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.Constructor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class RequestReducedDataPackageHandlerTest {

    @Mock
    private ExtendedDataService mockedExtendedDataService;

    private PackageHandler packageHandler;

    @Before
    public void initializePackageAnswerer()
            throws Exception {
        this.packageHandler = createPackageAnswerer(this.mockedExtendedDataService);
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<PackageHandler> createdAnswerers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdAnswerers.put(RequestReducedDataPackageHandler.create());
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

    @Test
    public void answererShouldAnswerIndependently() {
        final Data givenData = new Data();
        final Package givenPackage = new RequestReducedDataPackage(givenData);


    }

    private static PackageHandler createPackageAnswerer(final ExtendedDataService extendedDataService)
            throws Exception {
        final Constructor<? extends PackageHandler> packageAnswererConstructor = RequestReducedDataPackageHandler.class
                .getDeclaredConstructor(PackageHandler.class, ExtendedDataService.class);
        packageAnswererConstructor.setAccessible(true);
        try {
            return packageAnswererConstructor.newInstance(null, extendedDataService);
        } finally {
            packageAnswererConstructor.setAccessible(false);
        }
    }
}
