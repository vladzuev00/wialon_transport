package by.zuevvlad.wialontransport.netty.handler;

import by.zuevvlad.wialontransport.netty.handler.handlingchain.exception.NoSuitablePackageHandlerException;
import by.zuevvlad.wialontransport.netty.handler.handlingchain.PackageHandler;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.login.RequestLoginPackage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@RunWith(MockitoJUnitRunner.class)
//public final class PackageHandlerTest {
//    private static final String FIELD_NAME_NEXT_ANSWERER = "nextAnswerer";
//
//    @Mock
//    private PackageHandler mockedNextAnswerer;
//
//    @Mock
//    private CallingMarker mockedCallingMarker;
//
//    private TempPackageHandler tempPackageAnswerer;
//
//    @Captor
//    private ArgumentCaptor<Package> packageArgumentCaptor;
//
//    @Before
//    public void initializeTempPackageAnswerer() {
//        this.tempPackageAnswerer = new TempPackageHandler(this.mockedNextAnswerer, this.mockedCallingMarker);
//    }
//
//    @Test
//    public void answererShouldAnswerIndependently() {
//        final TempPackage givenTempPackage = new TempPackage();
//
//        this.tempPackageAnswerer.handle(givenTempPackage);
//
//        verify(this.mockedCallingMarker, times(1)).markCalling();
//        verify(this.mockedNextAnswerer, times(0)).handleIndependently(any(Package.class));
//    }
//
//    @Test
//    public void answererShouldDelegateAnsweringToNextAnswerer() {
//        final RequestLoginPackage givenRequestLoginPackage = new RequestLoginPackage("imei", "password");
//        when(this.mockedNextAnswerer.handle(givenRequestLoginPackage)).thenReturn(givenRequestLoginPackage);
//
//        this.tempPackageAnswerer.handle(givenRequestLoginPackage);
//
//        verify(this.mockedCallingMarker, times(0)).markCalling();
//        verify(this.mockedNextAnswerer, times(1)).handle(this.packageArgumentCaptor.capture());
//
//        assertSame(givenRequestLoginPackage, this.packageArgumentCaptor.getValue());
//    }
//
//    @Test(expected = NoSuitablePackageHandlerException.class)
//    public void packageShouldNotBeAnsweredBecauseOfNextAnswererIsNull()
//            throws Exception {
//        setNullAsNextAnswerer(this.tempPackageAnswerer);
//
//        final RequestLoginPackage givenRequestLoginPackage = new RequestLoginPackage("imei", "password");
//
//        this.tempPackageAnswerer.handle(givenRequestLoginPackage);
//
//        verify(this.mockedCallingMarker, times(0)).markCalling();
//        verify(this.mockedNextAnswerer, times(0)).handleIndependently(any(Package.class));
//    }
//
//    private static final class TempPackage implements Package {
//
//        @Override
//        public String serialize() {
//            throw new UnsupportedOperationException();
//        }
//    }
//
//    private static final class CallingMarker {
//        public void markCalling() {
//
//        }
//    }
//
//    private static final class TempPackageHandler extends PackageHandler {
//        private final CallingMarker callingMarker;
//
//        public TempPackageHandler(final PackageHandler nextAnswerer, final CallingMarker callingMarker) {
//            super(TempPackage.class, nextAnswerer);
//            this.callingMarker = callingMarker;
//        }
//
//        @Override
//        protected Package answerIndependently(final Package requestPackage) {
//            this.callingMarker.markCalling();
//            return requestPackage;
//        }
//    }
//
//    private static void setNullAsNextAnswerer(final TempPackageHandler tempPackageAnswerer)
//            throws Exception {
//        final Field nextAnswererField = PackageHandler.class.getDeclaredField(FIELD_NAME_NEXT_ANSWERER);
//        nextAnswererField.setAccessible(true);
//        try {
//            nextAnswererField.set(tempPackageAnswerer, null);
//        } finally {
//            nextAnswererField.setAccessible(false);
//        }
//    }
//}
