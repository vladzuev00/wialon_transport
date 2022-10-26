package by.zuevvlad.wialontransport.netty.decoder.decodingchain.component;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.blackbox.RequestBlackBoxPackageDeserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.PackageDecoder;

import static by.zuevvlad.wialontransport.wialonpackage.blackbox.RequestBlackBoxPackage.PACKAGE_DESCRIPTION_PREFIX;

public final class RequestBlackBoxPackageDecoder extends PackageDecoder {

    public static PackageDecoder create() {
        return SingletonHolder.PACKAGE_DECODER;
    }

    private RequestBlackBoxPackageDecoder(final PackageDecoder nextPackageDecoder,
                                          final PackageDeserializer packageDeserializer) {
        super(nextPackageDecoder, PACKAGE_DESCRIPTION_PREFIX, packageDeserializer);
    }

    private static final class SingletonHolder {
        private static final PackageDecoder PACKAGE_DECODER = new RequestBlackBoxPackageDecoder(
                RequestMessagePackageDecoder.create(), RequestBlackBoxPackageDeserializer.create());
    }
}
