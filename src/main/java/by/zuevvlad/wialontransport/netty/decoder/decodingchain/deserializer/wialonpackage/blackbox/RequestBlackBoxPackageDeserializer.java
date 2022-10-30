package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.blackbox;

import by.zuevvlad.wialontransport.entity.DataEntity;
import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.ExtendedDataDeserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.blackbox.RequestBlackBoxPackage;

import java.util.List;

import static by.zuevvlad.wialontransport.wialonpackage.blackbox.RequestBlackBoxPackage.PACKAGE_DESCRIPTION_POSTFIX;
import static by.zuevvlad.wialontransport.wialonpackage.blackbox.RequestBlackBoxPackage.PACKAGE_DESCRIPTION_PREFIX;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public final class RequestBlackBoxPackageDeserializer implements PackageDeserializer {
    private static final String EMPTY_STRING = "";
    private static final String SERIALIZED_DATA_DELIMITER = "\\|";
    private static final String REGEX_SERIALIZED_DATA
            = "((\\d{6}|(NA));){2}"              //date, time
            + "((\\d{4}\\.\\d+;[NS])|(NA;NA));"  //latitude
            + "((\\d{5}\\.\\d+;[EW])|(NA;NA));"  //longitude
            + "((\\d+|(NA));){3}"                //speed, course, height
            + "(\\d+|(NA))";                     //sats
    private static final String REGEX_SERIALIZED_EXTENDED_DATA = REGEX_SERIALIZED_DATA + ";"
            + "((\\d+\\.\\d+)|(NA));"         //reductionPrecision
            + "((\\d+|(NA));){2}"             //inputs, outputs
            + "((\\d+(\\.\\d+)?),?)*;"        //analogInputs
            + ".*;"                           //driverKeyCode
            + "(.+:[123]:.+,?)*";             //parameters

    private final Deserializer<DataEntity> dataDeserializer;
    private final Deserializer<ExtendedDataEntity> extendedDataDeserializer;

    public static PackageDeserializer create() {
        return SingletonHolder.PACKAGE_DESERIALIZER;
    }

    private RequestBlackBoxPackageDeserializer(final Deserializer<DataEntity> dataDeserializer,
                                               final Deserializer<ExtendedDataEntity> extendedDataDeserializer) {
        this.dataDeserializer = dataDeserializer;
        this.extendedDataDeserializer = extendedDataDeserializer;
    }

    @Override
    public RequestBlackBoxPackage deserialize(final String deserialized) {
        final String message = deserialized
                .replace(PACKAGE_DESCRIPTION_PREFIX, EMPTY_STRING)
                .replace(PACKAGE_DESCRIPTION_POSTFIX, EMPTY_STRING);
        final String[] serializedAllData = message.split(SERIALIZED_DATA_DELIMITER);
        final List<DataEntity> data = this.deserializeData(serializedAllData);
        final List<ExtendedDataEntity> extendedData = this.deserializeExtendedData(serializedAllData);
        return new RequestBlackBoxPackage(data, extendedData);
    }

    private List<DataEntity> deserializeData(final String[] serializedAllData) {
        return stream(serializedAllData)
                .filter(serializedData -> serializedData.matches(REGEX_SERIALIZED_DATA))
                .map(this.dataDeserializer::deserialize)
                .collect(toList());
    }

    private List<ExtendedDataEntity> deserializeExtendedData(final String[] serializedAllData) {
        return stream(serializedAllData)
                .filter(serializedData -> serializedData.matches(REGEX_SERIALIZED_EXTENDED_DATA))
                .map(this.extendedDataDeserializer::deserialize)
                .collect(toList());
    }

    private static final class SingletonHolder {
        private static final PackageDeserializer PACKAGE_DESERIALIZER = new RequestBlackBoxPackageDeserializer(
                null, ExtendedDataDeserializer.create());
    }
}
