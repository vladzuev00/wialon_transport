package by.zuevvlad.wialontransport.netty.decoder.decodingchain.component;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.ping.RequestPingPackageDeserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.PackageDecoder;

import static by.zuevvlad.wialontransport.wialonpackage.ping.RequestPingPackage.PACKAGE_DESCRIPTION_PREFIX;

public final class RequestPingPackageDecoder extends PackageDecoder {
    public static PackageDecoder create() {
        return SingletonHolder.PACKAGE_DECODER;
    }

    private RequestPingPackageDecoder(final PackageDecoder nextPackageDecoder,
                                      final PackageDeserializer packageDeserializer) {
        super(nextPackageDecoder, PACKAGE_DESCRIPTION_PREFIX, packageDeserializer);
    }

    private static final class SingletonHolder {
        private static final PackageDecoder PACKAGE_DECODER = new RequestPingPackageDecoder(
                RequestBlackBoxPackageDecoder.create(), RequestPingPackageDeserializer.create());
    }
}
