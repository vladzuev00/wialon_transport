package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.blackbox;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.blackbox.ResponseBlackBoxPackage;

import static by.zuevvlad.wialontransport.wialonpackage.blackbox.ResponseBlackBoxPackage.PACKAGE_DESCRIPTION_POSTFIX;
import static by.zuevvlad.wialontransport.wialonpackage.blackbox.ResponseBlackBoxPackage.PACKAGE_DESCRIPTION_PREFIX;
import static java.lang.Integer.parseInt;

public final class ResponseBlackBoxPackageDeserializer implements PackageDeserializer {
    private static final String EMPTY_STRING = "";

    public static PackageDeserializer create() {
        return SingletonHolder.PACKAGE_DESERIALIZER;
    }

    private ResponseBlackBoxPackageDeserializer() {

    }

    @Override
    public ResponseBlackBoxPackage deserialize(final String deserialized) {
        final String serializedAmountFixedMessages = deserialized
                .replace(PACKAGE_DESCRIPTION_PREFIX, EMPTY_STRING)
                .replace(PACKAGE_DESCRIPTION_POSTFIX, EMPTY_STRING);
        final int amountFixedMessages = parseInt(serializedAmountFixedMessages);
        return new ResponseBlackBoxPackage(amountFixedMessages);
    }

    private static final class SingletonHolder {
        private static final PackageDeserializer PACKAGE_DESERIALIZER = new ResponseBlackBoxPackageDeserializer();
    }
}
