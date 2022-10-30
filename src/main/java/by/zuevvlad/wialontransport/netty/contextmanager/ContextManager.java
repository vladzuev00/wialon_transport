package by.zuevvlad.wialontransport.netty.contextmanager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import static io.netty.util.AttributeKey.valueOf;

public final class ContextManager {
    private static final String NAME_CHANNEL_ATTRIBUTE_KEY_TRACKER_IMEI = "tracker_imei";
    private static final AttributeKey<String> CHANNEL_ATTRIBUTE_KEY_TRACKER_IMEI
            = valueOf(NAME_CHANNEL_ATTRIBUTE_KEY_TRACKER_IMEI);

    private ContextManager() {

    }

    public static ContextManager create() {
        return SingletonHolder.CONTEXT_MANAGER;
    }

    public void putTrackerImei(final ChannelHandlerContext context, final String imei) {
        putAttributeValue(context, CHANNEL_ATTRIBUTE_KEY_TRACKER_IMEI, imei);
    }

    public String findTrackerImei(final ChannelHandlerContext context) {
        return findAttributeValue(context, CHANNEL_ATTRIBUTE_KEY_TRACKER_IMEI);
    }

    private static <ValueType> void putAttributeValue(final ChannelHandlerContext context,
                                                      final AttributeKey<ValueType> attributeKey,
                                                      final ValueType value) {
        final Channel channel = context.channel();
        final Attribute<ValueType> attribute = channel.attr(attributeKey);
        attribute.set(value);
    }

    private static <ValueType> ValueType findAttributeValue(final ChannelHandlerContext context,
                                                            final AttributeKey<ValueType> attributeKey) {
        final Channel channel = context.channel();
        final Attribute<ValueType> attribute = channel.attr(attributeKey);
        return attribute.get();
    }

    private static final class SingletonHolder {
        private static final ContextManager CONTEXT_MANAGER = new ContextManager();
    }
}
