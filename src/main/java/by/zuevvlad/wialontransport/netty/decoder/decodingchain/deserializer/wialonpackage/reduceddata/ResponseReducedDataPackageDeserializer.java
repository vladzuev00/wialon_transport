package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.reduceddata;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.reduceddata.ResponseReducedDataPackage;

import static by.zuevvlad.wialontransport.wialonpackage.reduceddata.ResponseReducedDataPackage.*;
import static by.zuevvlad.wialontransport.wialonpackage.reduceddata.ResponseReducedDataPackage.Status.findByValue;
import static java.lang.Byte.parseByte;
import static by.zuevvlad.wialontransport.wialonpackage.reduceddata.ResponseReducedDataPackage.Status.NOT_DEFINED;

public final class ResponseReducedDataPackageDeserializer implements PackageDeserializer {
    private static final String EMPTY_STRING = "";

    public static PackageDeserializer create() {
        return SingletonHolder.PACKAGE_DESERIALIZER;
    }

    private ResponseReducedDataPackageDeserializer() {

    }

    @Override
    public ResponseReducedDataPackage deserialize(final String deserialized) {
        final byte statusValue = findStatusValue(deserialized);
        final Status status = findByValue(statusValue);
        return new ResponseReducedDataPackage(status);
    }

    private static byte findStatusValue(final String deserialized) {
        try {
            final String deserializedStatusValue = deserialized
                    .replace(PACKAGE_DESCRIPTION_PREFIX, EMPTY_STRING)
                    .replace(PACKAGE_DESCRIPTION_POSTFIX, EMPTY_STRING);
            return parseByte(deserializedStatusValue);
        } catch (final NumberFormatException numberFormatException) {
            return NOT_DEFINED.getValue();
        }
    }

    private static final class SingletonHolder {
        private static final PackageDeserializer PACKAGE_DESERIALIZER = new ResponseReducedDataPackageDeserializer();
    }
}
