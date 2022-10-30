package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.data;

import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.ExtendedDataDeserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.data.RequestDataPackage;

import static by.zuevvlad.wialontransport.wialonpackage.data.RequestDataPackage.PACKAGE_DESCRIPTION_POSTFIX;
import static by.zuevvlad.wialontransport.wialonpackage.data.RequestDataPackage.PACKAGE_DESCRIPTION_PREFIX;

public final class RequestDataPackageDeserializer implements PackageDeserializer {
    private static final String EMPTY_STRING = "";

    private final Deserializer<ExtendedDataEntity> extendedDataDeserializer;

    public static PackageDeserializer create() {
        return SingletonHolder.PACKAGE_DESERIALIZER;
    }

    private RequestDataPackageDeserializer(final Deserializer<ExtendedDataEntity> extendedDataDeserializer) {
        this.extendedDataDeserializer = extendedDataDeserializer;
    }

    @Override
    public RequestDataPackage deserialize(final String deserialized) {
        final String serializedExtendedData = deserialized
                .replace(PACKAGE_DESCRIPTION_PREFIX, EMPTY_STRING)
                .replace(PACKAGE_DESCRIPTION_POSTFIX, EMPTY_STRING);
        final ExtendedDataEntity extendedData = this.extendedDataDeserializer.deserialize(serializedExtendedData);
        return new RequestDataPackage(extendedData);
    }

    private static final class SingletonHolder {
        private static final PackageDeserializer PACKAGE_DESERIALIZER = new RequestDataPackageDeserializer(
                ExtendedDataDeserializer.create());
    }
}
