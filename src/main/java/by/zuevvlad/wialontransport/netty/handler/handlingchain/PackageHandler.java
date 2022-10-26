package by.zuevvlad.wialontransport.netty.handler.handlingchain;

import by.zuevvlad.wialontransport.netty.handler.handlingchain.exception.NoSuitablePackageHandlerException;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import io.netty.channel.ChannelHandlerContext;

public abstract class PackageHandler {
    private final Class<? extends Package> handledPackageType;
    private final PackageHandler nextHandler;

    public PackageHandler(final Class<? extends Package> handledPackageType, final PackageHandler nextHandler) {
        this.handledPackageType = handledPackageType;
        this.nextHandler = nextHandler;
    }

    public final void handle(final Package handledPackage, final ChannelHandlerContext context) {
        if (this.isAbleToHandle(handledPackage)) {
            this.handleIndependently(handledPackage, context);
            return;
        }
        this.delegateToNextHandler(handledPackage, context);
    }

    protected abstract void handleIndependently(final Package handledPackage, final ChannelHandlerContext context);

    private boolean isAbleToHandle(final Package handledPackage) {
        return this.handledPackageType != null && this.handledPackageType.isInstance(handledPackage);
    }

    private void delegateToNextHandler(final Package handledPackage, final ChannelHandlerContext context) {
        if (this.nextHandler == null) {
            throw new NoSuitablePackageHandlerException();
        }
        this.nextHandler.handle(handledPackage, context);
    }
}
