package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.message;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.message.RequestMessagePackage;

import static by.zuevvlad.wialontransport.wialonpackage.message.RequestMessagePackage.PACKAGE_DESCRIPTION_POSTFIX;
import static by.zuevvlad.wialontransport.wialonpackage.message.RequestMessagePackage.PACKAGE_DESCRIPTION_PREFIX;

public final class RequestMessagePackageDeserializer implements PackageDeserializer {
    private static final String EMPTY_STRING = "";

    public static PackageDeserializer create() {
        return SingletonHolder.PACKAGE_DESERIALIZER;
    }

    private RequestMessagePackageDeserializer() {

    }

    @Override
    public RequestMessagePackage deserialize(final String deserialized) {
        final String message = deserialized
                .replace(PACKAGE_DESCRIPTION_PREFIX, EMPTY_STRING)
                .replace(PACKAGE_DESCRIPTION_POSTFIX, EMPTY_STRING);
        return new RequestMessagePackage(message);
    }

    private static final class SingletonHolder {
        private static final PackageDeserializer PACKAGE_DESERIALIZER = new RequestMessagePackageDeserializer();
    }
}
