package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.entity.DataEntity;
import by.zuevvlad.wialontransport.entity.DataEntity.Latitude;
import by.zuevvlad.wialontransport.entity.DataEntity.Longitude;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception.*;

import java.time.LocalDateTime;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static by.zuevvlad.wialontransport.entity.DataEntity.*;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.regex.Pattern.compile;

public final class DataDeserializer implements Deserializer<DataEntity> {
    private static final String REGEX_SERIALIZED_DATA
            = "((\\d{6}|(NA));(\\d{6}|(NA)));"   //date, time
            + "((\\d{4}\\.\\d+;[NS])|(NA;NA));"  //latitude
            + "((\\d{5}\\.\\d+;[EW])|(NA;NA));"  //longitude
            + "(\\d+|(NA));"                     //speed
            + "(\\d+|(NA));"                     //course
            + "(\\d+|(NA));"                     //height
            + "(\\d+|(NA))";                     //amountSatellites
    private static final Pattern PATTERN_SERIALIZED_DATA = compile(REGEX_SERIALIZED_DATA);

    private static final String TEMPLATE_EXCEPTION_DESCRIPTION_NOT_VALID_INBOUND_SERIALIZED_DATA
            = "Not valid inbound serialized data: %s.";

    private static final int GROUP_NUMBER_SERIALIZED_DATE_TIME = 1;
    private static final int GROUP_NUMBER_SERIALIZED_LATITUDE = 6;
    private static final int GROUP_NUMBER_SERIALIZED_LONGITUDE = 9;
    private static final int GROUP_NUMBER_SERIALIZED_SPEED = 12;
    private static final int GROUP_NUMBER_SERIALIZED_COURSE = 14;
    private static final int GROUP_NUMBER_SERIALIZED_HEIGHT = 16;
    private static final int GROUP_NUMBER_SERIALIZED_AMOUNT_SATELLITES = 18;

    private static final String NOT_DEFINED_SERIALIZED_COMPONENT = "NA";

    private final Deserializer<LocalDateTime> dateTimeDeserializer;
    private final Deserializer<Latitude> latitudeDeserializer;
    private final Deserializer<Longitude> longitudeDeserializer;
    private final Supplier<DataBuilder> dataBuilderSupplier;

    private DataDeserializer(final Deserializer<LocalDateTime> dateTimeDeserializer,
                             final Deserializer<Latitude> latitudeDeserializer,
                             final Deserializer<Longitude> longitudeDeserializer,
                             final Supplier<DataBuilder> dataBuilderSupplier) {
        this.dateTimeDeserializer = dateTimeDeserializer;
        this.latitudeDeserializer = latitudeDeserializer;
        this.longitudeDeserializer = longitudeDeserializer;
        this.dataBuilderSupplier = dataBuilderSupplier;
    }

    public static Deserializer<DataEntity> create() {
        return SingletonHolder.DATA_DESERIALIZER;
    }

    @Override
    public DataEntity deserialize(final String deserialized) {
        final Matcher matcher = PATTERN_SERIALIZED_DATA.matcher(deserialized);
        if (!matcher.matches()) {
            throw new NotValidInboundSerializedDataException(
                    format(TEMPLATE_EXCEPTION_DESCRIPTION_NOT_VALID_INBOUND_SERIALIZED_DATA, deserialized));
        }
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        return dataBuilder
                .catalogDateTime(this.deserializeDateTime(matcher))
                .catalogLatitude(this.deserializeLatitude(matcher))
                .catalogLongitude(this.deserializeLongitude(matcher))
                .catalogSpeed(deserializeSpeed(matcher))
                .catalogCourse(deserializeCourse(matcher))
                .catalogHeight(deserializeHeight(matcher))
                .catalogAmountSatellites(deserializeAmountSatellites(matcher))
                .build();
    }

    private LocalDateTime deserializeDateTime(final Matcher matcher) {
        final String serializedDateTime = matcher.group(GROUP_NUMBER_SERIALIZED_DATE_TIME);
        return this.dateTimeDeserializer.deserialize(serializedDateTime);
    }

    private Latitude deserializeLatitude(final Matcher matcher) {
        final String serializedLatitude = matcher.group(GROUP_NUMBER_SERIALIZED_LATITUDE);
        return this.latitudeDeserializer.deserialize(serializedLatitude);
    }

    private Longitude deserializeLongitude(final Matcher matcher) {
        final String serializedLongitude = matcher.group(GROUP_NUMBER_SERIALIZED_LONGITUDE);
        return this.longitudeDeserializer.deserialize(serializedLongitude);
    }

    private static int deserializeSpeed(final Matcher matcher) {
        try {
            final String serializedSpeed = matcher.group(GROUP_NUMBER_SERIALIZED_SPEED);
            return !serializedSpeed.equals(NOT_DEFINED_SERIALIZED_COMPONENT)
                    ? parseInt(serializedSpeed)
                    : NOT_DEFINED_SPEED;
        } catch (final NumberFormatException cause) {
            throw new NotValidInboundSerializedSpeedException(cause);
        }
    }

    private static int deserializeCourse(final Matcher matcher) {
        try {
            final String serializedCourse = matcher.group(GROUP_NUMBER_SERIALIZED_COURSE);
            return !serializedCourse.equals(NOT_DEFINED_SERIALIZED_COMPONENT)
                    ? parseInt(serializedCourse)
                    : NOT_DEFINED_COURSE;
        } catch (final NumberFormatException cause) {
            throw new NotValidInboundSerializedCourseException(cause);
        }
    }

    private static int deserializeHeight(final Matcher matcher) {
        try {
            final String serializedHeight = matcher.group(GROUP_NUMBER_SERIALIZED_HEIGHT);
            return !serializedHeight.equals(NOT_DEFINED_SERIALIZED_COMPONENT)
                    ? parseInt(serializedHeight)
                    : NOT_DEFINED_HEIGHT;
        } catch (final NumberFormatException cause) {
            throw new NotValidInboundSerializedHeightException(cause);
        }
    }

    private static int deserializeAmountSatellites(final Matcher matcher) {
        try {
            final String serializedAmountSatellites = matcher.group(GROUP_NUMBER_SERIALIZED_AMOUNT_SATELLITES);
            return !serializedAmountSatellites.equals(NOT_DEFINED_SERIALIZED_COMPONENT)
                    ? parseInt(serializedAmountSatellites)
                    : NOT_DEFINED_AMOUNT_SATELLITES;
        } catch (final NumberFormatException cause) {
            throw new NotValidInboundSerializedAmountSatellitesException(cause);
        }
    }

    private static final class SingletonHolder {
        private static final Deserializer<DataEntity> DATA_DESERIALIZER = new DataDeserializer(DateTimeDeserializer.create(),
                LatitudeDeserializer.create(), LongitudeDeserializer.create(), DataBuilder::new);
    }
}
