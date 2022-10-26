package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.ping;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.ping.RequestPingPackage;

public final class RequestPingPackageDeserializer implements PackageDeserializer {

    public static PackageDeserializer create() {
        return SingletonHolder.PACKAGE_DESERIALIZER;
    }

    private RequestPingPackageDeserializer() {

    }

    @Override
    public RequestPingPackage deserialize(final String deserialized) {
        return new RequestPingPackage();
    }

    private static final class SingletonHolder {
        private static final PackageDeserializer PACKAGE_DESERIALIZER = new RequestPingPackageDeserializer();
    }
}
