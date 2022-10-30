package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.entity.DataEntity.Latitude;
import by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception.NotValidInboundSerializedLatitudeException;

import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type.NOT_DEFINED;
import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type.findByValue;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;

public final class LatitudeDeserializer implements Deserializer<Latitude> {
    private static final int START_INDEX_DESERIALIZED_DEGREES = 0;
    private static final int END_INDEX_DESERIALIZED_DEGREES = 1;

    private static final int START_INDEX_DESERIALIZED_MINUTES = 2;
    private static final int END_INDEX_DESERIALIZED_MINUTES = 3;

    private static final int START_INDEX_DESERIALIZED_MINUTE_SHARE = 5;
    private static final String DELIMITER_MINUTE_SHARE_AND_TYPE = ";";

    private static final String NOT_DEFINED_SERIALIZED_LATITUDE = "NA;NA";

    private static final String TEMPLATE_EXCEPTION_DESCRIPTION_NOT_VALID_TYPE
            = "Impossible to deserialize latitude's type in '%s'.";

    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;

    public static Deserializer<Latitude> create() {
        return SingletonHolder.LATITUDE_DESERIALIZER;
    }

    private LatitudeDeserializer(final Supplier<LatitudeBuilder> latitudeBuilderSupplier) {
        this.latitudeBuilderSupplier = latitudeBuilderSupplier;
    }

    @Override
    public Latitude deserialize(final String deserialized) {
        try {
            final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
            return !deserialized.equals(NOT_DEFINED_SERIALIZED_LATITUDE)
                    ? latitudeBuilder
                    .catalogDegrees(deserializeDegrees(deserialized))
                    .catalogMinutes(deserializeMinutes(deserialized))
                    .catalogMinuteShare(deserializeMinuteShare(deserialized))
                    .catalogType(deserializeType(deserialized))
                    .build()
                    : latitudeBuilder.build();
        } catch (final IndexOutOfBoundsException | NumberFormatException cause) {
            throw new NotValidInboundSerializedLatitudeException(cause);
        }
    }

    private static int deserializeDegrees(final String deserialized) {
        final String deserializedDegrees = deserialized.substring(START_INDEX_DESERIALIZED_DEGREES,
                END_INDEX_DESERIALIZED_DEGREES + 1);
        return parseInt(deserializedDegrees);
    }

    private static int deserializeMinutes(final String deserialized) {
        final String deserializedMinutes = deserialized.substring(START_INDEX_DESERIALIZED_MINUTES,
                END_INDEX_DESERIALIZED_MINUTES + 1);
        return parseInt(deserializedMinutes);
    }

    private static int deserializeMinuteShare(final String deserialized) {
        final int indexDelimiterMinuteShareAndType = deserialized.lastIndexOf(DELIMITER_MINUTE_SHARE_AND_TYPE);
        final String deserializedMinuteShare = deserialized.substring(START_INDEX_DESERIALIZED_MINUTE_SHARE,
                indexDelimiterMinuteShareAndType);
        return parseInt(deserializedMinuteShare);
    }

    private static Type deserializeType(final String deserialized) {
        final int indexDelimiterMinuteShareAndType = deserialized.lastIndexOf(DELIMITER_MINUTE_SHARE_AND_TYPE);
        final String deserializedTypeValue = deserialized.substring(indexDelimiterMinuteShareAndType + 1);
        final Type type = findByValue(deserializedTypeValue.charAt(0));
        if (type == NOT_DEFINED) {
            throw new NotValidInboundSerializedLatitudeException(
                    format(TEMPLATE_EXCEPTION_DESCRIPTION_NOT_VALID_TYPE, deserialized));
        }
        return type;
    }

    private static final class SingletonHolder {
        private static final Deserializer<Latitude> LATITUDE_DESERIALIZER
                = new LatitudeDeserializer(LatitudeBuilder::new);
    }
}
