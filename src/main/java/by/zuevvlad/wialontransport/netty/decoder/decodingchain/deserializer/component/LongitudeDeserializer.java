package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.entity.Data.Longitude;
import by.zuevvlad.wialontransport.entity.Data.Longitude.Type;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception.NotValidInboundSerializedLongitudeException;

import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.NOT_DEFINED;
import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.findByValue;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;

public final class LongitudeDeserializer implements Deserializer<Longitude> {
    private static final int START_INDEX_DESERIALIZED_DEGREES = 0;
    private static final int END_INDEX_DESERIALIZED_DEGREES = 2;

    private static final int START_INDEX_DESERIALIZED_MINUTES = 3;
    private static final int END_INDEX_DESERIALIZED_MINUTES = 4;

    private static final int START_INDEX_DESERIALIZED_MINUTE_SHARE = 6;
    private static final String DELIMITER_MINUTE_SHARE_AND_TYPE = ";";

    private static final String NOT_DEFINED_SERIALIZED_LONGITUDE = "NA;NA";

    private static final String TEMPLATE_EXCEPTION_DESCRIPTION_NOT_VALID_TYPE
            = "Impossible to deserialize longitude's type in '%s'.";

    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;

    public static Deserializer<Longitude> create() {
        return SingletonHolder.LONGITUDE_DESERIALIZER;
    }

    private LongitudeDeserializer(final Supplier<LongitudeBuilder> longitudeBuilderSupplier) {
        this.longitudeBuilderSupplier = longitudeBuilderSupplier;
    }

    @Override
    public Longitude deserialize(final String deserialized) {
        try {
            final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
            return !deserialized.equals(NOT_DEFINED_SERIALIZED_LONGITUDE)
                    ? longitudeBuilder
                    .catalogDegrees(deserializeDegrees(deserialized))
                    .catalogMinutes(deserializeMinutes(deserialized))
                    .catalogMinuteShare(deserializeMinuteShare(deserialized))
                    .catalogType(deserializeType(deserialized))
                    .build()
                    : longitudeBuilder.build();
        } catch (final IndexOutOfBoundsException | NumberFormatException cause) {
            throw new NotValidInboundSerializedLongitudeException(cause);
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
            throw new NotValidInboundSerializedLongitudeException(
                    format(TEMPLATE_EXCEPTION_DESCRIPTION_NOT_VALID_TYPE, deserialized));
        }
        return type;
    }

    private static final class SingletonHolder {
        private static final Deserializer<Longitude> LONGITUDE_DESERIALIZER = new LongitudeDeserializer(
                LongitudeBuilder::new);
    }
}
