package by.zuevvlad.wialontransport.netty.tostringserializer;

@FunctionalInterface
public interface ToStringSerializer<SerializedObjectType> {
    String serialize(final SerializedObjectType serializedObject);
}

