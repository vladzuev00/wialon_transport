package by.zuevvlad.wialontransport.netty.handler;

import by.zuevvlad.wialontransport.netty.handler.handlingchain.PackageHandler;
import by.zuevvlad.wialontransport.netty.handler.handlingchain.component.RequestLoginPackageHandler;
import by.zuevvlad.wialontransport.netty.service.authorizationdevice.AuthorizationDeviceService;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.login.RequestLoginPackage;
import by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage;
import by.zuevvlad.wialontransport.wialonpackage.ping.RequestPingPackage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Constructor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage.Status.SUCCESS_AUTHORIZATION;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@RunWith(MockitoJUnitRunner.class)
//public final class RequestLoginPackageHandlerTest {
//
//    @Mock
//    private AuthorizationDeviceService mockedAuthorizationDeviceService;
//
//    @Captor
//    private ArgumentCaptor<RequestLoginPackage> requestLoginPackageArgumentCaptor;
//
//    private PackageHandler packageHandler;
//
//    @Before
//    public void initializePackageAnswerer()
//            throws Exception {
//        this.packageHandler = createPackageAnswerer(this.mockedAuthorizationDeviceService);
//    }
//
//    @Test
//    public void singletonShouldBeLazyThreadSafe() {
//        final int startedThreadAmount = 50;
//        final BlockingQueue<PackageHandler> createdAnswerers = new ArrayBlockingQueue<>(startedThreadAmount);
//        rangeClosed(1, startedThreadAmount).forEach(i -> {
//            final Thread startedThread = new Thread(() -> {
//                try {
//                    createdAnswerers.put(RequestLoginPackageHandler.create());
//                } catch (final InterruptedException cause) {
//                    throw new RuntimeException(cause);
//                }
//            });
//            startedThread.start();
//        });
//        while (createdAnswerers.size() < startedThreadAmount) {
//            Thread.yield();
//        }
//        final long actualAmountOfAnswerers = createdAnswerers.stream().distinct().count();
//        final long expectedAmountOfAnswerers = 1;
//        assertEquals(expectedAmountOfAnswerers, actualAmountOfAnswerers);
//    }
//
//    @Test
//    public void answererShouldAnswerIndependently() {
//        final Package givenPackage = new RequestLoginPackage("imei", "password");
//
//        when(this.mockedAuthorizationDeviceService.authorize(any(RequestLoginPackage.class)))
//                .thenReturn(SUCCESS_AUTHORIZATION);
//
//        final Package actual = this.packageHandler.handleIndependently(givenPackage);
//        final ResponseLoginPackage expected = new ResponseLoginPackage(SUCCESS_AUTHORIZATION);
//        assertEquals(expected, actual);
//
//        verify(this.mockedAuthorizationDeviceService, times(1))
//                .authorize(this.requestLoginPackageArgumentCaptor.capture());
//        assertSame(givenPackage, this.requestLoginPackageArgumentCaptor.getValue());
//    }
//
//    @Test(expected = ClassCastException.class)
//    public void answererShouldNotAnswerIndependentlyBecauseOfNotSuitableTypeOfPackage() {
//        final Package givenPackage = new RequestPingPackage();
//        this.packageHandler.handleIndependently(givenPackage);
//    }
//
//    private static PackageHandler createPackageAnswerer(final AuthorizationDeviceService authorizationDeviceService)
//            throws Exception {
//        final Constructor<? extends PackageHandler> answererConstructor = RequestLoginPackageHandler.class
//                .getDeclaredConstructor(PackageHandler.class, AuthorizationDeviceService.class);
//        answererConstructor.setAccessible(true);
//        try {
//            return answererConstructor.newInstance(null, authorizationDeviceService);
//        } finally {
//            answererConstructor.setAccessible(false);
//        }
//    }
//}
