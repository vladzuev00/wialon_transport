package by.zuevvlad.wialontransport.netty.decoder.decodingchain.component;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.data.RequestDataPackageDeserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.PackageDecoder;

import static by.zuevvlad.wialontransport.wialonpackage.data.RequestDataPackage.PACKAGE_DESCRIPTION_PREFIX;

public final class RequestDataPackageDecoder extends PackageDecoder {
    public static PackageDecoder create() {
        return SingletonHolder.PACKAGE_DECODER;
    }

    private RequestDataPackageDecoder(final PackageDecoder nextPackageDecoder,
                                      final PackageDeserializer packageDeserializer) {
        super(nextPackageDecoder, PACKAGE_DESCRIPTION_PREFIX, packageDeserializer);
    }

    private static final class SingletonHolder {
        private static final PackageDecoder PACKAGE_DECODER = new RequestDataPackageDecoder(
                RequestPingPackageDecoder.create(), RequestDataPackageDeserializer.create());
    }
}
