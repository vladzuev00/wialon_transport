package by.zuevvlad.wialontransport.netty.tostringserializer;

import by.zuevvlad.wialontransport.entity.DataEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static by.zuevvlad.wialontransport.entity.DataEntity.*;
import static by.zuevvlad.wialontransport.netty.tostringserializer.configuration.SerializationDataConfiguration.*;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.format.DateTimeFormatter.ofPattern;

public final class DataToStringSerializer implements ToStringSerializer<DataEntity> {
    private final DateTimeFormatter dateFormatter;
    private final DateTimeFormatter timeFormatter;
    private final ToStringSerializer<Latitude> latitudeToStringSerializer;
    private final ToStringSerializer<Longitude> longitudeToStringSerializer;

    public static ToStringSerializer<DataEntity> create() {
        return SingletonHolder.DATA_TO_STRING_SERIALIZER;
    }

    private DataToStringSerializer(final DateTimeFormatter dateFormatter, final DateTimeFormatter timeFormatter,
                                   final ToStringSerializer<Latitude> latitudeToStringSerializer,
                                   final ToStringSerializer<Longitude> longitudeToStringSerializer) {
        this.dateFormatter = dateFormatter;
        this.timeFormatter = timeFormatter;
        this.latitudeToStringSerializer = latitudeToStringSerializer;
        this.longitudeToStringSerializer = longitudeToStringSerializer;
    }

    @Override
    public String serialize(final DataEntity serializedData) {
        final String serializedDate = this.findSerializedDate(serializedData);
        final String serializedTime = this.findSerializedTime(serializedData);
        final String serializedLatitude = this.findSerializedLatitude(serializedData);
        final String serializedLongitude = this.findSerializedLongitude(serializedData);
        final String serializedSpeed = this.findSerializedSpeed(serializedData);
        final String serializedCourse = this.findSerializedCourse(serializedData);
        final String serializedHeight = this.findSerializedHeight(serializedData);
        final String serializedAmountSatellites = this.findSerializedAmountSatellites(serializedData);
        return join(COMPONENTS_DELIMITER.getValue(), serializedDate, serializedTime, serializedLatitude,
                serializedLongitude, serializedSpeed, serializedCourse, serializedHeight, serializedAmountSatellites);
    }

    private String findSerializedDate(final DataEntity serializedData) {
        final LocalDateTime dateTime = serializedData.getDateTime();
        final LocalDate date = dateTime.toLocalDate();
        return !date.equals(NOT_DEFINED_DATE)
                ? this.dateFormatter.format(dateTime)
                : SERIALIZED_NOT_DEFINED_VALUE.getValue();
    }

    private String findSerializedTime(final DataEntity serializedData) {
        final LocalDateTime dateTime = serializedData.getDateTime();
        final LocalTime time = dateTime.toLocalTime();
        return !time.equals(NOT_DEFINED_TIME)
                ? this.timeFormatter.format(dateTime)
                : SERIALIZED_NOT_DEFINED_VALUE.getValue();
    }

    private String findSerializedLatitude(final DataEntity serializedData) {
        final Latitude latitude = serializedData.getLatitude();
        return !latitude.equals(NOT_DEFINED_LATITUDE_SUPPLIER.get())
                ? this.latitudeToStringSerializer.serialize(latitude)
                : SERIALIZED_NOT_DEFINED_GEOGRAPHIC_COORDINATE.getValue();
    }

    private String findSerializedLongitude(final DataEntity serializedData) {
        final Longitude longitude = serializedData.getLongitude();
        return !longitude.equals(NOT_DEFINED_LONGITUDE_SUPPLIER.get())
                ? this.longitudeToStringSerializer.serialize(longitude)
                : SERIALIZED_NOT_DEFINED_GEOGRAPHIC_COORDINATE.getValue();
    }

    private String findSerializedSpeed(final DataEntity serializedData) {
        final int speed = serializedData.getSpeed();
        return speed != NOT_DEFINED_SPEED
                ? Integer.toString(speed)
                : SERIALIZED_NOT_DEFINED_VALUE.getValue();
    }

    private String findSerializedCourse(final DataEntity serializedData) {
        final int course = serializedData.getCourse();
        return course != NOT_DEFINED_COURSE
                ? Integer.toString(course)
                : SERIALIZED_NOT_DEFINED_VALUE.getValue();
    }

    private String findSerializedHeight(final DataEntity serializedData) {
        final int height = serializedData.getHeight();
        return height != NOT_DEFINED_HEIGHT
                ? Integer.toString(height)
                : SERIALIZED_NOT_DEFINED_VALUE.getValue();
    }

    private String findSerializedAmountSatellites(final DataEntity serializedData) {
        final int amountSatellites = serializedData.getAmountSatellites();
        return amountSatellites != NOT_DEFINED_AMOUNT_SATELLITES
                ? Integer.toString(amountSatellites)
                : SERIALIZED_NOT_DEFINED_VALUE.getValue();
    }

    private static final class LatitudeToStringSerializer implements ToStringSerializer<Latitude> {
        private static final String SERIALIZED_LATITUDE_TEMPLATE = "%02d%02d.%d;%c";

        @Override
        public String serialize(final Latitude serializedLatitude) {
            final Latitude.Type type = serializedLatitude.getType();
            return format(SERIALIZED_LATITUDE_TEMPLATE, serializedLatitude.getDegrees(),
                    serializedLatitude.getMinutes(), serializedLatitude.getMinuteShare(), type.getValue());
        }
    }

    private static final class LongitudeToStringSerializer implements ToStringSerializer<Longitude> {
        private static final String SERIALIZED_LONGITUDE_TEMPLATE = "%03d%02d.%d;%c";

        @Override
        public String serialize(final Longitude serializedLongitude) {
            final Longitude.Type type = serializedLongitude.getType();
            return format(SERIALIZED_LONGITUDE_TEMPLATE, serializedLongitude.getDegrees(),
                    serializedLongitude.getMinutes(), serializedLongitude.getMinuteShare(), type.getValue());
        }
    }

    private static final class SingletonHolder {
        private static final ToStringSerializer<DataEntity> DATA_TO_STRING_SERIALIZER
                = new DataToStringSerializer(ofPattern(DATE_FORMAT.getValue()), ofPattern(TIME_FORMAT.getValue()),
                new LatitudeToStringSerializer(), new LongitudeToStringSerializer());
    }
}
