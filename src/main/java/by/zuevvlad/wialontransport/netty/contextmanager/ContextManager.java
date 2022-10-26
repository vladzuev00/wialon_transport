package by.zuevvlad.wialontransport.netty.contextmanager;

import by.zuevvlad.wialontransport.entity.Tracker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import static io.netty.util.AttributeKey.valueOf;

public final class ContextManager {
    private static final String NAME_CHANNEL_ATTRIBUTE_KEY_TRACKER = "tracker";
    private static final AttributeKey<Tracker> CHANNEL_ATTRIBUTE_KEY_TRACKER
            = valueOf(NAME_CHANNEL_ATTRIBUTE_KEY_TRACKER);

    private ContextManager() {

    }

    public static ContextManager create() {
        return SingletonHolder.CONTEXT_MANAGER;
    }

    public void putTracker(final ChannelHandlerContext context, final Tracker tracker) {
        putAttributeValue(context, CHANNEL_ATTRIBUTE_KEY_TRACKER, tracker);
    }

    public Tracker findTracker(final ChannelHandlerContext context) {
        return findAttributeValue(context, CHANNEL_ATTRIBUTE_KEY_TRACKER);
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
