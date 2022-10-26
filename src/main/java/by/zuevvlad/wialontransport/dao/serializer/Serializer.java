package by.zuevvlad.wialontransport.dao.serializer;

@FunctionalInterface
public interface Serializer<SourceType> {
    String serialize(final SourceType source);
}
