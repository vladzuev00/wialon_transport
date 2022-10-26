package by.zuevvlad.wialontransport.netty.decoder.decodingchain.component;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.PackageDecoder;

public final class FinisherInboundPackageDecoder extends PackageDecoder {
    public static PackageDecoder create() {
        return SingletonHolder.PACKAGE_DECODER;
    }

    private FinisherInboundPackageDecoder() {
        super(null, null, null);
    }

    private static final class SingletonHolder {
        private static final PackageDecoder PACKAGE_DECODER = new FinisherInboundPackageDecoder();
    }
}
