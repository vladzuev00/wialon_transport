package by.zuevvlad.wialontransport.netty.handler.handlingchain.component;

import by.zuevvlad.wialontransport.netty.handler.handlingchain.PackageHandler;
import by.zuevvlad.wialontransport.netty.service.authorizationdevice.AuthorizationDeviceService;
import by.zuevvlad.wialontransport.netty.service.authorizationdevice.AuthorizationDeviceServiceImplementation;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.login.RequestLoginPackage;
import by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage;
import by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage.Status;
import io.netty.channel.ChannelHandlerContext;

public final class RequestLoginPackageHandler extends PackageHandler {
    private final AuthorizationDeviceService authorizationDeviceService;

    private RequestLoginPackageHandler(final PackageHandler nextHandler,
                                       final AuthorizationDeviceService authorizationDeviceService) {
        super(RequestLoginPackage.class, nextHandler);
        this.authorizationDeviceService = authorizationDeviceService;
    }

    public static PackageHandler create() {
        return SingletonHolder.PACKAGE_ANSWERER;
    }

    @Override
    protected void handleIndependently(final Package handledPackage, final ChannelHandlerContext context) {
        final RequestLoginPackage requestLoginPackage = (RequestLoginPackage) handledPackage;
        final Status status = this.authorizationDeviceService.authorize(requestLoginPackage);
        final ResponseLoginPackage responseLoginPackage = new ResponseLoginPackage(status);
        context.writeAndFlush(responseLoginPackage);
    }

    private static final class SingletonHolder {
        private static final PackageHandler PACKAGE_ANSWERER = new RequestLoginPackageHandler(
                RequestReducedDataPackageHandler.create(), AuthorizationDeviceServiceImplementation.create());
    }
}
