package by.zuevvlad.wialontransport.netty.decoder;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.PackageDecoder;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.logging.Logger.getLogger;

public final class InboundPackageDecoder extends ReplayingDecoder<Package> {
    private static final Logger LOGGER = getLogger(InboundPackageDecoder.class.getName());
    private static final String TEMPLATE_MESSAGE_START_DECODING_INBOUND_PACKAGE
            = "Start decoding inbound package: %s";

    private static final char CHARACTER_OF_END_INBOUND_PACKAGE = '\n';

    private final PackageDecoder starterPackageDecoder;

    public InboundPackageDecoder(final PackageDecoder starterPackageDecoder) {
        this.starterPackageDecoder = starterPackageDecoder;
    }

    @Override
    protected void decode(final ChannelHandlerContext context, final ByteBuf byteBuf, final List<Object> outObjects) {
        final String serializedInboundPackage = findSerializedPackage(byteBuf);
        LOGGER.info(format(TEMPLATE_MESSAGE_START_DECODING_INBOUND_PACKAGE, serializedInboundPackage));
        final Package inboundPackage = this.starterPackageDecoder.decode(serializedInboundPackage);
        outObjects.add(inboundPackage);
    }

    private static String findSerializedPackage(final ByteBuf byteBuf) {
        final StringBuilder serializedPackageBuilder = new StringBuilder();
        char currentAppendedCharacter;
        do {
            currentAppendedCharacter = (char) byteBuf.readByte();
            serializedPackageBuilder.append(currentAppendedCharacter);
        } while (byteBuf.isReadable() && currentAppendedCharacter != CHARACTER_OF_END_INBOUND_PACKAGE);
        return serializedPackageBuilder.toString();
    }
}
