package by.zuevvlad.wialontransport.netty.tostringserializer.collection;

import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;

import java.util.Collection;

import static java.util.stream.Collectors.joining;

public abstract class CollectionToStringSerializer<ComponentType>
        implements ToStringSerializer<Collection<ComponentType>> {
    private final ToStringSerializer<ComponentType> componentToStringSerializer;
    private final String serializedComponentsDelimiter;

    protected CollectionToStringSerializer(final ToStringSerializer<ComponentType> componentToStringSerializer,
                                           final String serializedComponentsDelimiter) {
        this.componentToStringSerializer = componentToStringSerializer;
        this.serializedComponentsDelimiter = serializedComponentsDelimiter;
    }

    @Override
    public final String serialize(final Collection<ComponentType> serialized) {
        return serialized.stream()
                .map(this.componentToStringSerializer::serialize)
                .collect(joining(this.serializedComponentsDelimiter));
    }
}
