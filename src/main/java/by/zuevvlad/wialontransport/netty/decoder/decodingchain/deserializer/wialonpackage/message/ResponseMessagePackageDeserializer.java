package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.message;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.message.ResponseMessagePackage;
import by.zuevvlad.wialontransport.wialonpackage.message.ResponseMessagePackage.Status;

import static by.zuevvlad.wialontransport.wialonpackage.message.ResponseMessagePackage.PACKAGE_DESCRIPTION_POSTFIX;
import static by.zuevvlad.wialontransport.wialonpackage.message.ResponseMessagePackage.PACKAGE_DESCRIPTION_PREFIX;
import static by.zuevvlad.wialontransport.wialonpackage.message.ResponseMessagePackage.Status.findByValue;
import static java.lang.Byte.parseByte;

public final class ResponseMessagePackageDeserializer implements PackageDeserializer {
    private static final String EMPTY_STRING = "";

    public static PackageDeserializer create() {
        return SingletonHolder.PACKAGE_DESERIALIZER;
    }

    private ResponseMessagePackageDeserializer() {

    }

    @Override
    public ResponseMessagePackage deserialize(final String deserialized) {
        final String serializedStatus = deserialized
                .replace(PACKAGE_DESCRIPTION_PREFIX, EMPTY_STRING)
                .replace(PACKAGE_DESCRIPTION_POSTFIX, EMPTY_STRING);
        final byte statusValue = parseByte(serializedStatus);
        final Status status = findByValue(statusValue);
        return new ResponseMessagePackage(status);
    }

    private static final class SingletonHolder {
        private static final PackageDeserializer PACKAGE_DESERIALIZER = new ResponseMessagePackageDeserializer();
    }
}
