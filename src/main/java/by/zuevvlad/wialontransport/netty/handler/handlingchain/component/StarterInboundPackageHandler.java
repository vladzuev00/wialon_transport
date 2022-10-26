package by.zuevvlad.wialontransport.netty.handler.handlingchain.component;

import by.zuevvlad.wialontransport.netty.handler.handlingchain.PackageHandler;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import io.netty.channel.ChannelHandlerContext;

public final class StarterInboundPackageHandler extends PackageHandler {
    public static PackageHandler create() {
        return SingletonHolder.PACKAGE_HANDLER;
    }

    private StarterInboundPackageHandler(final PackageHandler nextHandler) {
        super(null, nextHandler);
    }

    @Override
    protected void handleIndependently(final Package handledPackage, final ChannelHandlerContext context) {
        throw new UnsupportedOperationException();
    }

    private static final class SingletonHolder {
        private static final PackageHandler PACKAGE_HANDLER = new StarterInboundPackageHandler(
                RequestLoginPackageHandler.create());
    }
}
