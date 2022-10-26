package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.login;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage;
import by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage.Status;

import static by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage.*;
import static by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage.Status.findByValue;

public final class ResponseLoginPackageDeserializer implements PackageDeserializer {
    private static final String EMPTY_STRING = "";

    public static PackageDeserializer create() {
        return SingletonHolder.PACKAGE_DESERIALIZER;
    }

    private ResponseLoginPackageDeserializer() {

    }

    @Override
    public final ResponseLoginPackage deserialize(final String deserialized) {
        final String statusValue = findStatusValue(deserialized);
        final Status responseLoginStatus = findByValue(statusValue);
        return new ResponseLoginPackage(responseLoginStatus);
    }

    private static String findStatusValue(final String deserialized) {
        return deserialized
                .replace(PACKAGE_DESCRIPTION_PREFIX, EMPTY_STRING)
                .replace(PACKAGE_DESCRIPTION_POSTFIX, EMPTY_STRING);
    }

    private static final class SingletonHolder {
        private static final PackageDeserializer PACKAGE_DESERIALIZER = new ResponseLoginPackageDeserializer();
    }
}
