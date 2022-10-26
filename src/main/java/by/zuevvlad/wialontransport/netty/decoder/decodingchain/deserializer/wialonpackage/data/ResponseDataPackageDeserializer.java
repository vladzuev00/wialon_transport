package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.data;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.data.ResponseDataPackage;
import by.zuevvlad.wialontransport.wialonpackage.data.ResponseDataPackage.Status;

import static by.zuevvlad.wialontransport.wialonpackage.data.ResponseDataPackage.PACKAGE_DESCRIPTION_POSTFIX;
import static by.zuevvlad.wialontransport.wialonpackage.data.ResponseDataPackage.PACKAGE_DESCRIPTION_PREFIX;
import static by.zuevvlad.wialontransport.wialonpackage.data.ResponseDataPackage.Status.findByValue;
import static java.lang.Byte.parseByte;

public final class ResponseDataPackageDeserializer implements PackageDeserializer {
    private static final String EMPTY_STRING = "";

    public static PackageDeserializer create() {
        return SingletonHolder.PACKAGE_DESERIALIZER;
    }

    private ResponseDataPackageDeserializer() {

    }

    @Override
    public ResponseDataPackage deserialize(final String deserialized) {
        final String serializedStatus = deserialized
                .replace(PACKAGE_DESCRIPTION_PREFIX, EMPTY_STRING)
                .replace(PACKAGE_DESCRIPTION_POSTFIX, EMPTY_STRING);
        final byte statusValue = parseByte(serializedStatus);
        final Status status = findByValue(statusValue);
        return new ResponseDataPackage(status);
    }

    private static final class SingletonHolder {
        private static final PackageDeserializer PACKAGE_DESERIALIZER = new ResponseDataPackageDeserializer();
    }
}
