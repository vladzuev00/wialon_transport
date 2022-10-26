package by.zuevvlad.wialontransport.kafka.serializer;

import by.zuevvlad.wialontransport.kafka.amountbytesfounder.AmountDataBytesFounder;
import by.zuevvlad.wialontransport.kafka.amountbytesfounder.AmountObjectBytesFounder;
import by.zuevvlad.wialontransport.entity.Data;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.nio.charset.StandardCharsets.UTF_8;
import static by.zuevvlad.wialontransport.entity.Data.Latitude;
import static by.zuevvlad.wialontransport.entity.Data.Longitude;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.nio.ByteBuffer.allocate;

public final class DataSerializer implements Serializer<Data> {
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss";;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern(DATE_TIME_PATTERN);

    private final AmountObjectBytesFounder<Data> amountDataBytesFounder;

    public static Serializer<Data> create() {
        return SingletonHolder.DATA_SERIALIZER;
    }

    private DataSerializer(final AmountObjectBytesFounder<Data> amountDataBytesFounder) {
        this.amountDataBytesFounder = amountDataBytesFounder;
    }

    @Override
    public byte[] serialize(final String topic, final Data serializedData) {
        final int amountOfBytes = this.amountDataBytesFounder.find(serializedData);
        final ByteBuffer byteBuffer = allocate(amountOfBytes);
        byteBuffer.putLong(serializedData.getId());
        writeDateTime(byteBuffer, serializedData.getDateTime());
        writeLatitude(byteBuffer, serializedData.getLatitude());
        writeLongitude(byteBuffer, serializedData.getLongitude());
        byteBuffer.putInt(serializedData.getSpeed());
        byteBuffer.putInt(serializedData.getCourse());
        byteBuffer.putInt(serializedData.getHeight());
        byteBuffer.putInt(serializedData.getAmountSatellites());
        return byteBuffer.array();
    }

    private static void writeDateTime(final ByteBuffer byteBuffer, final LocalDateTime dateTime) {
        final String dateTimeDescription = dateTime.format(DATE_TIME_FORMATTER);
        final byte[] dateTimeDescriptionBytes = dateTimeDescription.getBytes(UTF_8);
        byteBuffer.put(dateTimeDescriptionBytes);
    }

    private static void writeLatitude(final ByteBuffer byteBuffer, final Latitude latitude) {
        byteBuffer.putInt(latitude.getDegrees());
        byteBuffer.putInt(latitude.getMinutes());
        byteBuffer.putInt(latitude.getMinuteShare());

        final Latitude.Type type = latitude.getType();
        byteBuffer.putChar(type.getValue());
    }

    private static void writeLongitude(final ByteBuffer byteBuffer, final Longitude longitude) {
        byteBuffer.putInt(longitude.getDegrees());
        byteBuffer.putInt(longitude.getMinutes());
        byteBuffer.putInt(longitude.getMinuteShare());

        final Longitude.Type type = longitude.getType();
        byteBuffer.putChar(type.getValue());
    }

    private static final class SingletonHolder {
        private static final Serializer<Data> DATA_SERIALIZER = new DataSerializer(AmountDataBytesFounder.create());
    }
}
