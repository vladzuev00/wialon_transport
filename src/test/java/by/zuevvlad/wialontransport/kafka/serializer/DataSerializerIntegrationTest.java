package by.zuevvlad.wialontransport.kafka.serializer;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.entity.Data;
import by.zuevvlad.wialontransport.entity.Data.Latitude;
import by.zuevvlad.wialontransport.entity.Data.Longitude;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.Test;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.Data.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.EAST;
import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public final class DataSerializerIntegrationTest {
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static final byte[] DATE_TIME_PATTERN_BYTES = DATE_TIME_PATTERN.getBytes(UTF_8);
    private static final String METHOD_NAME_WRITING_DATE_TIME_IN_BYTE_BUFFER = "writeDateTime";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern(DATE_TIME_PATTERN);
    private static final String METHOD_NAME_WRITING_LATITUDE_IN_BYTE_BUFFER = "writeLatitude";
    private static final String METHOD_NAME_WRITING_LONGITUDE_IN_BYTE_BUFFER = "writeLongitude";

    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;

    public DataSerializerIntegrationTest() {
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<Serializer<Data>> createdSerializers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdSerializers.put(DataSerializer.create());
                } catch (InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdSerializers.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfSerializers = createdSerializers.stream().distinct().count();
        final long expectedAmountOfSerializers = 1;
        assertEquals(expectedAmountOfSerializers, actualAmountOfSerializers);
    }

    @Test
    public void dateTimeShouldBeWrittenInByteBuffer()
            throws Exception {
        final LocalDateTime writtenDateTime
                = LocalDateTime.of(2022, 10, 10, 10, 10, 10);
        final ByteBuffer byteBuffer = allocate(DATE_TIME_PATTERN_BYTES.length);

        final Method methodWritingDateTime = DataSerializer.class
                .getDeclaredMethod(METHOD_NAME_WRITING_DATE_TIME_IN_BYTE_BUFFER, ByteBuffer.class, LocalDateTime.class);
        methodWritingDateTime.setAccessible(true);
        try {
            methodWritingDateTime.invoke(null, byteBuffer, writtenDateTime);
        } finally {
            methodWritingDateTime.setAccessible(false);
        }

        final byte[] dateTimeDescriptionBytes = byteBuffer.array();
        final String dateTimeDescription = new String(dateTimeDescriptionBytes, UTF_8);
        final LocalDateTime dateTimeFromBuffer = parse(dateTimeDescription, DATE_TIME_FORMATTER);

        assertEquals(writtenDateTime, dateTimeFromBuffer);
    }

    @Test
    public void latitudeShouldBeWrittenInByteBuffer()
            throws Exception {
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final Latitude writtenLatitude = latitudeBuilder
                .catalogDegrees(1)
                .catalogMinutes(2)
                .catalogMinuteShare(3)
                .catalogType(NORTH)
                .build();
        final ByteBuffer byteBuffer = allocate(Integer.BYTES * 3 + Character.BYTES);

        final Method methodWritingLatitude = DataSerializer.class
                .getDeclaredMethod(METHOD_NAME_WRITING_LATITUDE_IN_BYTE_BUFFER, ByteBuffer.class, Latitude.class);
        methodWritingLatitude.setAccessible(true);
        try {
            methodWritingLatitude.invoke(null, byteBuffer, writtenLatitude);
        } finally {
            methodWritingLatitude.setAccessible(false);
        }

        final byte[] actual = byteBuffer.array();
        final byte[] expected = {0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 78};
        assertArrayEquals(expected, actual);
    }

    @Test
    public void longitudeShouldBeWrittenInByteBuffer()
            throws Exception {
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Longitude writtenLongitude = longitudeBuilder
                .catalogDegrees(1)
                .catalogMinutes(2)
                .catalogMinuteShare(3)
                .catalogType(EAST)
                .build();
        final ByteBuffer byteBuffer = allocate(Integer.BYTES * 3 + Character.BYTES);

        final Method methodWritingLongitude = DataSerializer.class
                .getDeclaredMethod(METHOD_NAME_WRITING_LONGITUDE_IN_BYTE_BUFFER, ByteBuffer.class, Longitude.class);
        methodWritingLongitude.setAccessible(true);
        try {
            methodWritingLongitude.invoke(null, byteBuffer, writtenLongitude);
        } finally {
            methodWritingLongitude.setAccessible(false);
        }

        final byte[] actual = byteBuffer.array();
        final byte[] expected = {0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 69};
        assertArrayEquals(expected, actual);
    }

    @Test
    public void dataShouldBeSerialized() {
        final long id = 255;
        final LocalDateTime dateTime = LocalDateTime
                .of(2022, 10, 10, 10, 10, 10);

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final int latitudeDegrees = 1;
        final int latitudeMinutes = 2;
        final int latitudeMinuteShare = 3;
        final Latitude latitude = latitudeBuilder
                .catalogDegrees(latitudeDegrees)
                .catalogMinutes(latitudeMinutes)
                .catalogMinuteShare(latitudeMinuteShare)
                .catalogType(NORTH)
                .build();

        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final int longitudeDegrees = 4;
        final int longitudeMinutes = 5;
        final int longitudeMinuteShare = 6;
        final Longitude longitude = longitudeBuilder
                .catalogDegrees(longitudeDegrees)
                .catalogMinutes(longitudeMinutes)
                .catalogMinuteShare(longitudeMinuteShare)
                .catalogType(EAST)
                .build();

        final int speed = 7;
        final int course = 8;
        final int height = 9;
        final int amountSatellites = 10;

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final Data data = dataBuilder
                .catalogId(id)
                .catalogDateTime(dateTime)
                .catalogLatitude(latitude)
                .catalogLongitude(longitude)
                .catalogSpeed(speed)
                .catalogCourse(course)
                .catalogHeight(height)
                .catalogAmountSatellites(amountSatellites)
                .build();

        final Serializer<Data> dataSerializer = DataSerializer.create();
        final byte[] actual = dataSerializer.serialize("", data);
        final byte[] expected = {0, 0, 0, 0, 0, 0, 0, -1, 49, 48, 46, 49, 48, 46, 50, 48, 50, 50, 32, 49, 48, 58, 49,
                48, 58, 49, 48, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 78, 0, 0, 0, 4, 0, 0, 0, 5, 0, 0, 0, 6, 0, 69,
                0, 0, 0, 7, 0, 0, 0, 8, 0, 0, 0, 9, 0, 0, 0, 10};
        assertArrayEquals(expected, actual);
    }
}
