package by.zuevvlad.wialontransport.netty.tostringserializer;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.entity.Data;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.Data.*;
import static by.zuevvlad.wialontransport.entity.Data.Latitude.Type.SOUTH;
import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;
import static java.lang.Class.forName;
import static by.zuevvlad.wialontransport.entity.Data.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.EAST;

public final class DataToStringSerializerTest {
    private static final String CLASS_NAME_LATITUDE_TO_STRING_SERIALIZER
            = "by.zuevvlad.wialontransport.netty.tostringserializer.DataToStringSerializer$LatitudeToStringSerializer";
    private static final String CLASS_NAME_LONGITUDE_TO_STRING_SERIALIZER
            = "by.zuevvlad.wialontransport.netty.tostringserializer.DataToStringSerializer$LongitudeToStringSerializer";
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern(DATE_TIME_PATTERN);

    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;

    public DataToStringSerializerTest() {
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<ToStringSerializer<Data>> createdSerializers
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdSerializers.put(DataToStringSerializer.create());
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
    public void latitudeShouldBeSerialized()
            throws Exception {
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final Latitude latitude = latitudeBuilder
                .catalogDegrees(1)
                .catalogMinutes(2)
                .catalogMinuteShare(3)
                .catalogType(NORTH)
                .build();
        final ToStringSerializer<Latitude> latitudeToStringSerializer = this.createLatitudeToStringSerializer();
        final String actual = latitudeToStringSerializer.serialize(latitude);
        final String expected = "0102.3;N";
        assertEquals(expected, actual);
    }

    @Test
    public void longitudeShouldBeSerialized()
            throws Exception {
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Longitude longitude = longitudeBuilder
                .catalogDegrees(1)
                .catalogMinutes(2)
                .catalogMinuteShare(3)
                .catalogType(EAST)
                .build();
        final ToStringSerializer<Longitude> longitudeToStringSerializer = this.createLongitudeToStringSerializer();
        final String actual = longitudeToStringSerializer.serialize(longitude);
        final String expected = "00102.3;E";
        assertEquals(expected, actual);
    }

    @Test
    public void dataShouldBeSerialized() {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data data = dataBuilder
                .catalogId(255)
                .catalogDateTime(parse("12.11.2003 11:30:13", DATE_TIME_FORMATTER))
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(10)
                        .catalogMinutes(11)
                        .catalogMinuteShare(12)
                        .catalogType(SOUTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(13)
                        .catalogMinutes(14)
                        .catalogMinuteShare(15)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(16)
                .catalogCourse(17)
                .catalogHeight(18)
                .catalogAmountSatellites(19)
                .build();
        final ToStringSerializer<Data> dataToStringSerializer = DataToStringSerializer.create();
        final String actual = dataToStringSerializer.serialize(data);
        final String expected = "121103;113013;1011.12;S;01314.15;E;16;17;18;19";
        assertEquals(expected, actual);
    }

    @Test
    public void dataWithNotDefinedDateShouldBeSerialized() {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();

        final LocalTime time = LocalTime.of(11, 30, 13);
        final LocalDateTime dateTime = LocalDateTime.of(NOT_DEFINED_DATE, time);

        final Data data = dataBuilder
                .catalogId(255)
                .catalogDateTime(dateTime)
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(10)
                        .catalogMinutes(11)
                        .catalogMinuteShare(12)
                        .catalogType(SOUTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(13)
                        .catalogMinutes(14)
                        .catalogMinuteShare(15)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(16)
                .catalogCourse(17)
                .catalogHeight(18)
                .catalogAmountSatellites(19)
                .build();
        final ToStringSerializer<Data> dataToStringSerializer = DataToStringSerializer.create();
        final String actual = dataToStringSerializer.serialize(data);
        final String expected = "NA;113013;1011.12;S;01314.15;E;16;17;18;19";
        assertEquals(expected, actual);
    }

    @Test
    public void dataWithNotDefinedTimeShouldBeSerialized() {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();

        final LocalDate date = LocalDate.of(2003, 11, 12);
        final LocalDateTime dateTime = LocalDateTime.of(date, NOT_DEFINED_TIME);

        final Data data = dataBuilder
                .catalogId(255)
                .catalogDateTime(dateTime)
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(10)
                        .catalogMinutes(11)
                        .catalogMinuteShare(12)
                        .catalogType(SOUTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(13)
                        .catalogMinutes(14)
                        .catalogMinuteShare(15)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(16)
                .catalogCourse(17)
                .catalogHeight(18)
                .catalogAmountSatellites(19)
                .build();
        final ToStringSerializer<Data> dataToStringSerializer = DataToStringSerializer.create();
        final String actual = dataToStringSerializer.serialize(data);
        final String expected = "121103;NA;1011.12;S;01314.15;E;16;17;18;19";
        assertEquals(expected, actual);
    }

    @Test
    public void dataWithNotDefinedLatitudeShouldBeSerialized() {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data data = dataBuilder
                .catalogId(255)
                .catalogDateTime(parse("12.11.2003 11:30:13", DATE_TIME_FORMATTER))
                .catalogLatitude(NOT_DEFINED_LATITUDE_SUPPLIER.get())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(13)
                        .catalogMinutes(14)
                        .catalogMinuteShare(15)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(16)
                .catalogCourse(17)
                .catalogHeight(18)
                .catalogAmountSatellites(19)
                .build();
        final ToStringSerializer<Data> dataToStringSerializer = DataToStringSerializer.create();
        final String actual = dataToStringSerializer.serialize(data);
        final String expected = "121103;113013;NA;NA;01314.15;E;16;17;18;19";
        assertEquals(expected, actual);
    }

    @Test
    public void dataWithNotDefinedLongitudeShouldBeSerialized() {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final Data data = dataBuilder
                .catalogId(255)
                .catalogDateTime(parse("12.11.2003 11:30:13", DATE_TIME_FORMATTER))
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(10)
                        .catalogMinutes(11)
                        .catalogMinuteShare(12)
                        .catalogType(SOUTH)
                        .build())
                .catalogLongitude(NOT_DEFINED_LONGITUDE_SUPPLIER.get())
                .catalogSpeed(16)
                .catalogCourse(17)
                .catalogHeight(18)
                .catalogAmountSatellites(19)
                .build();
        final ToStringSerializer<Data> dataToStringSerializer = DataToStringSerializer.create();
        final String actual = dataToStringSerializer.serialize(data);
        final String expected = "121103;113013;1011.12;S;NA;NA;16;17;18;19";
        assertEquals(expected, actual);
    }

    @Test
    public void dataWithNotDefinedSpeedShouldBeSerialized() {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data data = dataBuilder
                .catalogId(255)
                .catalogDateTime(parse("12.11.2003 11:30:13", DATE_TIME_FORMATTER))
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(10)
                        .catalogMinutes(11)
                        .catalogMinuteShare(12)
                        .catalogType(SOUTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(13)
                        .catalogMinutes(14)
                        .catalogMinuteShare(15)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(NOT_DEFINED_SPEED)
                .catalogCourse(17)
                .catalogHeight(18)
                .catalogAmountSatellites(19)
                .build();
        final ToStringSerializer<Data> dataToStringSerializer = DataToStringSerializer.create();
        final String actual = dataToStringSerializer.serialize(data);
        final String expected = "121103;113013;1011.12;S;01314.15;E;NA;17;18;19";
        assertEquals(expected, actual);
    }

    @Test
    public void dataWithNotDefinedCourseShouldBeSerialized() {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data data = dataBuilder
                .catalogId(255)
                .catalogDateTime(parse("12.11.2003 11:30:13", DATE_TIME_FORMATTER))
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(10)
                        .catalogMinutes(11)
                        .catalogMinuteShare(12)
                        .catalogType(SOUTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(13)
                        .catalogMinutes(14)
                        .catalogMinuteShare(15)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(16)
                .catalogCourse(NOT_DEFINED_COURSE)
                .catalogHeight(18)
                .catalogAmountSatellites(19)
                .build();
        final ToStringSerializer<Data> dataToStringSerializer = DataToStringSerializer.create();
        final String actual = dataToStringSerializer.serialize(data);
        final String expected = "121103;113013;1011.12;S;01314.15;E;16;NA;18;19";
        assertEquals(expected, actual);
    }

    @Test
    public void dataWithNotDefinedHeightShouldBeSerialized() {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data data = dataBuilder
                .catalogId(255)
                .catalogDateTime(parse("12.11.2003 11:30:13", DATE_TIME_FORMATTER))
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(10)
                        .catalogMinutes(11)
                        .catalogMinuteShare(12)
                        .catalogType(SOUTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(13)
                        .catalogMinutes(14)
                        .catalogMinuteShare(15)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(16)
                .catalogCourse(17)
                .catalogHeight(NOT_DEFINED_HEIGHT)
                .catalogAmountSatellites(19)
                .build();
        final ToStringSerializer<Data> dataToStringSerializer = DataToStringSerializer.create();
        final String actual = dataToStringSerializer.serialize(data);
        final String expected = "121103;113013;1011.12;S;01314.15;E;16;17;NA;19";
        assertEquals(expected, actual);
    }

    @Test
    public void dataWithNotDefinedAmountSatellitesShouldBeSerialized() {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data data = dataBuilder
                .catalogId(255)
                .catalogDateTime(parse("12.11.2003 11:30:13", DATE_TIME_FORMATTER))
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(10)
                        .catalogMinutes(11)
                        .catalogMinuteShare(12)
                        .catalogType(SOUTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(13)
                        .catalogMinutes(14)
                        .catalogMinuteShare(15)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(16)
                .catalogCourse(17)
                .catalogHeight(18)
                .catalogAmountSatellites(NOT_DEFINED_AMOUNT_SATELLITES)
                .build();
        final ToStringSerializer<Data> dataToStringSerializer = DataToStringSerializer.create();
        final String actual = dataToStringSerializer.serialize(data);
        final String expected = "121103;113013;1011.12;S;01314.15;E;16;17;18;NA";
        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    private ToStringSerializer<Latitude> createLatitudeToStringSerializer()
            throws Exception {
        final Class<?> latitudeToStringSerializerClass = forName(CLASS_NAME_LATITUDE_TO_STRING_SERIALIZER);
        final Constructor<?> latitudeToStringSerializerConstructor = latitudeToStringSerializerClass
                .getDeclaredConstructor();
        latitudeToStringSerializerConstructor.setAccessible(true);
        try {
            return (ToStringSerializer<Latitude>) latitudeToStringSerializerConstructor.newInstance();
        } finally {
            latitudeToStringSerializerConstructor.setAccessible(false);
        }
    }

    @SuppressWarnings("unchecked")
    private ToStringSerializer<Longitude> createLongitudeToStringSerializer()
            throws Exception {
        final Class<?> longitudeToStringSerializerClass = forName(CLASS_NAME_LONGITUDE_TO_STRING_SERIALIZER);
        final Constructor<?> longitudeToStringSerializerConstructor = longitudeToStringSerializerClass
                .getDeclaredConstructor();
        longitudeToStringSerializerConstructor.setAccessible(true);
        try {
            return (ToStringSerializer<Longitude>) longitudeToStringSerializerConstructor.newInstance();
        } finally {
            longitudeToStringSerializerConstructor.setAccessible(false);
        }
    }
}
