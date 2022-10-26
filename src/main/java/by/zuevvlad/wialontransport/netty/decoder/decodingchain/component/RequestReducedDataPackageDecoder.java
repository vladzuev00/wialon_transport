package by.zuevvlad.wialontransport.netty.decoder.decodingchain.component;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.reduceddata.RequestReducedDataPackageDeserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.PackageDecoder;

import static by.zuevvlad.wialontransport.wialonpackage.reduceddata.RequestReducedDataPackage.PACKAGE_DESCRIPTION_PREFIX;

public final class RequestReducedDataPackageDecoder extends PackageDecoder {
    public static PackageDecoder create() {
        return SingletonHolder.PACKAGE_DECODER;
    }

    private RequestReducedDataPackageDecoder(final PackageDecoder nextPackageDecoder,
                                             final PackageDeserializer packageDeserializer) {
        super(nextPackageDecoder, PACKAGE_DESCRIPTION_PREFIX, packageDeserializer);
    }

    private static final class SingletonHolder {
        private static final PackageDecoder PACKAGE_DECODER = new RequestReducedDataPackageDecoder(
                RequestDataPackageDecoder.create(), RequestReducedDataPackageDeserializer.create());
    }
}
