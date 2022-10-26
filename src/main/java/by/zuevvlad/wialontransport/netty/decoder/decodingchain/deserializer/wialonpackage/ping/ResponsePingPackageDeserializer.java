package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.ping;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.ping.ResponsePingPackage;

public final class ResponsePingPackageDeserializer implements PackageDeserializer {

    public static PackageDeserializer create() {
        return SingletonHolder.PACKAGE_DESERIALIZER;
    }

    private ResponsePingPackageDeserializer() {

    }

    @Override
    public ResponsePingPackage deserialize(final String deserialized) {
        return new ResponsePingPackage();
    }

    private static final class SingletonHolder {
        private static final PackageDeserializer PACKAGE_DESERIALIZER = new ResponsePingPackageDeserializer();
    }
}
