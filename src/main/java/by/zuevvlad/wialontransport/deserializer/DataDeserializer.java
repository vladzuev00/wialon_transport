package by.zuevvlad.wialontransport.deserializer;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.deserializer.exception.DeserializationException;
import by.zuevvlad.wialontransport.entity.Data;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static by.zuevvlad.wialontransport.entity.Data.Latitude;
import static by.zuevvlad.wialontransport.entity.Data.Longitude;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.LocalDateTime.parse;
import static java.nio.ByteBuffer.wrap;

public final class DataDeserializer implements Deserializer<Data> {
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static final byte[] DATE_TIME_PATTERN_BYTES = DATE_TIME_PATTERN.getBytes(UTF_8);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern(DATE_TIME_PATTERN);

    private static final int REQUIRED_AMOUNT_OF_BYTES =
            Long.BYTES                                            //DATA::id
                    + DATE_TIME_PATTERN.getBytes(UTF_8).length    //Data::dateTime
                    + Integer.BYTES                               //Data::Latitude::degrees
                    + Integer.BYTES                               //Data::Latitude::minutes
                    + Integer.BYTES                               //Data::Latitude::minuteShare
                    + Character.BYTES                             //Data::Latitude::Type::value
                    + Integer.BYTES                               //Data::Longitude::degrees
                    + Integer.BYTES                               //Data::Longitude::minutes
                    + Integer.BYTES                               //Data::Longitude::minuteShare
                    + Character.BYTES                             //Data::Longitude::Type::value
                    + Integer.BYTES                               //Data::speed
                    + Integer.BYTES                               //Data::course
                    + Integer.BYTES                               //Data::height
                    + Integer.BYTES;                              //Data::amountOfSatellites

    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;

    public static Deserializer<Data> create() {
        return SingletonHolder.DATA_DESERIALIZER;
    }

    private DataDeserializer(final Supplier<LatitudeBuilder> latitudeBuilderSupplier,
                             final Supplier<LongitudeBuilder> longitudeBuilderSupplier,
                             final Supplier<DataBuilder> dataBuilderSupplier) {
        this.latitudeBuilderSupplier = latitudeBuilderSupplier;
        this.longitudeBuilderSupplier = longitudeBuilderSupplier;
        this.dataBuilderSupplier = dataBuilderSupplier;
    }

    @Override
    public Data deserialize(final String topic, final byte[] deserializedData) {
        if (deserializedData == null) {
            throw new DeserializationException("Deserialized array of bytes is null.");
        }
        if (deserializedData.length != REQUIRED_AMOUNT_OF_BYTES) {
            throw new DeserializationException("Amount of bytes of received serialized data "
                    + "isn't expected. Expected amount of bytes: " + REQUIRED_AMOUNT_OF_BYTES);
        }
        final ByteBuffer byteBuffer = wrap(deserializedData);
        final long id = byteBuffer.getLong();
        final LocalDateTime dateTime = this.readDateTime(byteBuffer);
        final Latitude latitude = this.readLatitude(byteBuffer);
        final Longitude longitude = this.readLongitude(byteBuffer);
        final int speed = byteBuffer.getInt();
        final int course = byteBuffer.getInt();
        final int height = byteBuffer.getInt();
        final int amountOfSatellites = byteBuffer.getInt();

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        return dataBuilder
                .catalogId(id)
                .catalogDateTime(dateTime)
                .catalogLatitude(latitude)
                .catalogLongitude(longitude)
                .catalogSpeed(speed)
                .catalogCourse(course)
                .catalogHeight(height)
                .catalogAmountSatellites(amountOfSatellites)
                .build();
    }

    private LocalDateTime readDateTime(final ByteBuffer byteBuffer) {
        final byte[] dateTimeDescriptionBytes = new byte[DATE_TIME_PATTERN_BYTES.length];
        byteBuffer.get(dateTimeDescriptionBytes);
        final String dateTimeDescription = new String(dateTimeDescriptionBytes, UTF_8);
        return parse(dateTimeDescription, DATE_TIME_FORMATTER);
    }

    private Latitude readLatitude(final ByteBuffer byteBuffer) {
        final int degrees = byteBuffer.getInt();
        final int minutes = byteBuffer.getInt();
        final int minuteShare = byteBuffer.getInt();

        final char typeValue = byteBuffer.getChar();
        final Latitude.Type type = Latitude.Type.findByValue(typeValue);

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        return latitudeBuilder
                .catalogDegrees(degrees)
                .catalogMinutes(minutes)
                .catalogMinuteShare(minuteShare)
                .catalogType(type)
                .build();
    }

    private Longitude readLongitude(final ByteBuffer byteBuffer) {
        final int degrees = byteBuffer.getInt();
        final int minutes = byteBuffer.getInt();
        final int minuteShare = byteBuffer.getInt();

        final char typeValue = byteBuffer.getChar();
        final Longitude.Type type = Longitude.Type.findByValue(typeValue);

        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        return longitudeBuilder
                .catalogDegrees(degrees)
                .catalogMinutes(minutes)
                .catalogMinuteShare(minuteShare)
                .catalogType(type)
                .build();
    }

    private static final class SingletonHolder {
        private static final Deserializer<Data> DATA_DESERIALIZER
                = new DataDeserializer(LatitudeBuilder::new, LongitudeBuilder::new, DataBuilder::new);
    }
}
