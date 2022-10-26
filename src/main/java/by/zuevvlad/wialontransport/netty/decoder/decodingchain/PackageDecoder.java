package by.zuevvlad.wialontransport.netty.decoder.decodingchain;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.exception.NoSuitablePackageDecoderException;
import by.zuevvlad.wialontransport.wialonpackage.Package;

public abstract class PackageDecoder {
    private final PackageDecoder nextPackageDecoder;
    private final String packagePrefix;
    private final PackageDeserializer packageDeserializer;

    public PackageDecoder(final PackageDecoder nextPackageDecoder, final String packagePrefix,
                          final PackageDeserializer packageDeserializer) {
        this.nextPackageDecoder = nextPackageDecoder;
        this.packagePrefix = packagePrefix;
        this.packageDeserializer = packageDeserializer;
    }

    public final Package decode(final String decoded) {
        if (this.isAbleToDecode(decoded)) {
            return this.decodeIndependently(decoded);
        }
        return this.delegateToNextDecoder(decoded);
    }

    private boolean isAbleToDecode(final String decoded) {
        //packagePrefix == null in starter and finisher decoding chain
        return this.packagePrefix != null && decoded.startsWith(this.packagePrefix);
    }

    private Package decodeIndependently(final String decoded) {
        return this.packageDeserializer.deserialize(decoded);
    }

    private Package delegateToNextDecoder(final String decoded) {
        if (this.nextPackageDecoder != null) {
            return this.nextPackageDecoder.decode(decoded);
        }
        throw new NoSuitablePackageDecoderException();
    }
}
