package by.zuevvlad.wialontransport.netty.handler;

import by.zuevvlad.wialontransport.netty.contextmanager.ContextManager;
import by.zuevvlad.wialontransport.netty.handler.handlingchain.PackageHandler;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.logging.Logger.getLogger;

public final class InboundPackageHandler extends ChannelInboundHandlerAdapter {
    private static final String TEMPLATE_MESSAGE_HANDLING_INBOUND_PACKAGE
            = "Start handling inbound package: '%s'.";
    private static final String MESSAGE_ACTIVE_CHANNEL = "New tracker is connected.";
    private static final String MESSAGE_INACTIVE_CHANNEL = "Tracker with imei '%s' is disconnected.";

    private final PackageHandler starterPackageHandler;
    private final ContextManager contextManager;
    private final Logger logger;

    public InboundPackageHandler(final PackageHandler starterPackageHandler,
                                 final ContextManager contextManager) {
        this.starterPackageHandler = starterPackageHandler;
        this.contextManager = contextManager;
        this.logger = getLogger(InboundPackageHandler.class.getName());
    }

    @Override
    public void channelRead(final ChannelHandlerContext context, final Object inboundObject) {
        final Package inboundPackage = (Package) inboundObject;
        this.logger.info(format(TEMPLATE_MESSAGE_HANDLING_INBOUND_PACKAGE, inboundPackage));
        this.starterPackageHandler.handle(inboundPackage, context);
    }

    @Override
    public void channelActive(ChannelHandlerContext context) {
        this.logger.info(MESSAGE_ACTIVE_CHANNEL);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        final String trackerImei = this.contextManager.findTrackerImei(context);
        this.logger.info(format(MESSAGE_INACTIVE_CHANNEL, trackerImei));
    }
}
