package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer;

@FunctionalInterface
public interface Deserializer<ResultType> {
    ResultType deserialize(final String deserialized);
}
