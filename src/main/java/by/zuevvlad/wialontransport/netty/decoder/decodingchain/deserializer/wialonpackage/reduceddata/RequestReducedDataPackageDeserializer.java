package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.reduceddata;

import by.zuevvlad.wialontransport.entity.DataEntity;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.reduceddata.RequestReducedDataPackage;

import static by.zuevvlad.wialontransport.wialonpackage.reduceddata.RequestReducedDataPackage.*;
public final class RequestReducedDataPackageDeserializer implements PackageDeserializer {
    private static final String EMPTY_STRING = "";

    private final Deserializer<DataEntity> dataDeserializer;

    public static PackageDeserializer create() {
        return SingletonHolder.PACKAGE_DESERIALIZER;
    }

    private RequestReducedDataPackageDeserializer(final Deserializer<DataEntity> dataDeserializer) {
        this.dataDeserializer = dataDeserializer;
    }

    @Override
    public RequestReducedDataPackage deserialize(final String deserialized) {
        final String serializedData = deserialized
                .replace(PACKAGE_DESCRIPTION_PREFIX, EMPTY_STRING)
                .replace(PACKAGE_DESCRIPTION_POSTFIX, EMPTY_STRING);
        final DataEntity data = this.dataDeserializer.deserialize(serializedData);
        return new RequestReducedDataPackage(data);
    }

    private static final class SingletonHolder {
        private static final PackageDeserializer PACKAGE_DESERIALIZER = new RequestReducedDataPackageDeserializer(
                null);
    }
}
