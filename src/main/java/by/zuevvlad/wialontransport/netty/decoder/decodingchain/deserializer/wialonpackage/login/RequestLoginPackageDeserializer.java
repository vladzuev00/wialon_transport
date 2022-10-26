package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.login;

import by.zuevvlad.wialontransport.pair.Pair;
import by.zuevvlad.wialontransport.wialonpackage.login.RequestLoginPackage;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;

import static by.zuevvlad.wialontransport.wialonpackage.login.RequestLoginPackage.*;

public final class RequestLoginPackageDeserializer implements PackageDeserializer {
    private static final String EMPTY_STRING = "";
    private static final int INDEX_IN_MESSAGE_COMPONENTS_IMEI = 0;
    private static final int INDEX_IN_MESSAGE_COMPONENTS_PASSWORD = 1;

    public static PackageDeserializer create() {
        return SingletonHolder.PACKAGE_DESERIALIZER;
    }

    private RequestLoginPackageDeserializer() {

    }

    @Override
    public RequestLoginPackage deserialize(final String deserialized) {
        final Pair<String, String> imeiAndPasswords = findImeiAndPassword(deserialized);
        final String imei = imeiAndPasswords.getFirst();
        final String password = imeiAndPasswords.getSecond();
        return new RequestLoginPackage(imei, password);
    }

    private static Pair<String, String> findImeiAndPassword(final String deserialized) {
        final String message = deserialized
                .replace(PACKAGE_DESCRIPTION_PREFIX, EMPTY_STRING)
                .replace(PACKAGE_DESCRIPTION_POSTFIX, EMPTY_STRING);
        final String[] messageComponents = message.split(DELIMITER_IMEI_AND_PASSWORD);
        final String imei = messageComponents[INDEX_IN_MESSAGE_COMPONENTS_IMEI];
        final String password = messageComponents[INDEX_IN_MESSAGE_COMPONENTS_PASSWORD];
        return new Pair<>(imei, password);
    }

    private static final class SingletonHolder {
        private static final PackageDeserializer PACKAGE_DESERIALIZER = new RequestLoginPackageDeserializer();
    }
}
