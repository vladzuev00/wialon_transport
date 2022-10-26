package by.zuevvlad.wialontransport.netty.decoder.decodingchain.component;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.PackageDecoder;

public final class StarterInboundPackageDecoder extends PackageDecoder {
    public static PackageDecoder create() {
        return SingletonHolder.PACKAGE_DECODER;
    }

    private StarterInboundPackageDecoder(final PackageDecoder nextPackageDecoder) {
        super(nextPackageDecoder, null, null);
    }

    private static final class SingletonHolder {
        private static final PackageDecoder PACKAGE_DECODER = new StarterInboundPackageDecoder(
                RequestLoginPackageDecoder.create());
    }
}
