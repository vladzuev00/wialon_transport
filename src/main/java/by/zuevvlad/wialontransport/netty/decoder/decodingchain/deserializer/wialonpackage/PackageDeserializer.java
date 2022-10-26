package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;

@FunctionalInterface
public interface PackageDeserializer extends Deserializer<Package> {
    Package deserialize(final String deserialized);
}
