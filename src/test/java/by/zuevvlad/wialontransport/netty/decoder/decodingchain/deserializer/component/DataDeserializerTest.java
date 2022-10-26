package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.entity.Data;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception.NotValidInboundSerializedDataException;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.Data.*;
import static by.zuevvlad.wialontransport.entity.Data.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.EAST;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class DataDeserializerTest {
    private static final String FIELD_NAME_REGEX_SERIALIZED_DATA = "REGEX_SERIALIZED_DATA";

    private final String regexSerializedData;
    private final Deserializer<Data> dataDeserializer;
    private final Supplier<DataBuilder> dataBuilderSupplier;
    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;

    public DataDeserializerTest()
            throws Exception {
        this.regexSerializedData = findRegexSerializedData();
        this.dataDeserializer = DataDeserializer.create();
        this.dataBuilderSupplier = DataBuilder::new;
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<Deserializer<Data>> createdDeserializers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(DataDeserializer.create());
                } catch (final InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdDeserializers.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfDeserializers = createdDeserializers.stream().distinct().count();
        final long expectedAmountOfDeserializers = 1;
        assertEquals(expectedAmountOfDeserializers, actualAmountOfDeserializers);
    }

    @Test
    public void dataShouldBeMatchToRegex() {
        final String givenData = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177";
        assertTrue(givenData.matches(this.regexSerializedData));
    }

    @Test
    public void dataWithNotDefinedDateShouldBeMatchToRegex() {
        final String givenData = "NA;145643;5544.6025;N;03739.6834;E;100;15;10;177";
        assertTrue(givenData.matches(this.regexSerializedData));
    }

    @Test
    public void dataWithNotDefinedTimeShouldBeMatchToRegex() {
        final String givenData = "151122;NA;5544.6025;N;03739.6834;E;100;15;10;177";
        assertTrue(givenData.matches(this.regexSerializedData));
    }

    @Test
    public void dataWithNotDefinedLatitudeShouldBeMatchToRegex() {
        final String givenData = "151122;145643;NA;NA;03739.6834;E;100;15;10;177";
        assertTrue(givenData.matches(this.regexSerializedData));
    }

    @Test
    public void dataWithNotDefinedLongitudeShouldBeMatchToRegex() {
        final String givenData = "151122;145643;5544.6025;N;NA;NA;100;15;10;177";
        assertTrue(givenData.matches(this.regexSerializedData));
    }

    @Test
    public void dataWithNotDefinedSpeedShouldBeMatchToRegex() {
        final String givenData = "151122;145643;5544.6025;N;03739.6834;E;NA;15;10;177";
        assertTrue(givenData.matches(this.regexSerializedData));
    }

    @Test
    public void dataWithNotDefinedCourseShouldBeMatchToRegex() {
        final String givenData = "151122;145643;5544.6025;N;03739.6834;E;100;NA;10;177";
        assertTrue(givenData.matches(this.regexSerializedData));
    }

    @Test
    public void dataWithNotDefinedHeightShouldBeMatchToRegex() {
        final String givenData = "151122;145643;5544.6025;N;03739.6834;E;100;15;NA;177";
        assertTrue(givenData.matches(this.regexSerializedData));
    }

    @Test
    public void dataWithNotDefinedAmountSatellitesShouldBeMatchToRegex() {
        final String givenData = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;NA";
        assertTrue(givenData.matches(this.regexSerializedData));
    }

    @Test
    public void dataShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177";

        final Data actual = this.dataDeserializer.deserialize(givenDeserialized);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data expected = dataBuilder
                .catalogDateTime(LocalDateTime.of(2022, 11, 15, 14, 56, 43))
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(55)
                        .catalogMinutes(44)
                        .catalogMinuteShare(6025)
                        .catalogType(NORTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(37)
                        .catalogMinutes(39)
                        .catalogMinuteShare(6834)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(100)
                .catalogCourse(15)
                .catalogHeight(10)
                .catalogAmountSatellites(177)
                .build();

        assertEquals(expected, actual);
    }

    @Test(expected = NotValidInboundSerializedDataException.class)
    public void notValidDataShouldNotBeDeserialized() {
        final String givenDeserialized = "1151122;145643;5544.6025;N;03739.6834;E;100;15;10;177"; //not valid date
        this.dataDeserializer.deserialize(givenDeserialized);
    }

    @Test
    public void dataShouldBeDeserializedWithNotDefinedDate() {
        final String givenDeserialized = "NA;NA;5544.6025;N;03739.6834;E;100;15;10;177";

        final Data actual = this.dataDeserializer.deserialize(givenDeserialized);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data expected = dataBuilder
                .catalogDateTime(now())
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(55)
                        .catalogMinutes(44)
                        .catalogMinuteShare(6025)
                        .catalogType(NORTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(37)
                        .catalogMinutes(39)
                        .catalogMinuteShare(6834)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(100)
                .catalogCourse(15)
                .catalogHeight(10)
                .catalogAmountSatellites(177)
                .build();

        assertTrue(between(expected.getDateTime(), actual.getDateTime()).get(SECONDS) < 1);
        assertEquals(expected.getLatitude(), actual.getLatitude());
        assertEquals(expected.getLongitude(), actual.getLongitude());
        assertEquals(expected.getSpeed(), actual.getSpeed());
        assertEquals(expected.getCourse(), actual.getCourse());
        assertEquals(expected.getHeight(), actual.getHeight());
        assertEquals(expected.getAmountSatellites(), actual.getAmountSatellites());
    }

    @Test
    public void dataShouldBeDeserializedWithNotDefinedLatitude() {
        final String givenDeserialized = "151122;145643;NA;NA;03739.6834;E;100;15;10;177";

        final Data actual = this.dataDeserializer.deserialize(givenDeserialized);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data expected = dataBuilder
                .catalogDateTime(LocalDateTime.of(2022, 11, 15, 14, 56, 43))
                .catalogLatitude(latitudeBuilder.build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(37)
                        .catalogMinutes(39)
                        .catalogMinuteShare(6834)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(100)
                .catalogCourse(15)
                .catalogHeight(10)
                .catalogAmountSatellites(177)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void dataWithNotDefinedLongitudeShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;NA;NA;100;15;10;177";

        final Data actual = this.dataDeserializer.deserialize(givenDeserialized);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data expected = dataBuilder
                .catalogDateTime(LocalDateTime.of(2022, 11, 15, 14, 56, 43))
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(55)
                        .catalogMinutes(44)
                        .catalogMinuteShare(6025)
                        .catalogType(NORTH)
                        .build())
                .catalogLongitude(longitudeBuilder.build())
                .catalogSpeed(100)
                .catalogCourse(15)
                .catalogHeight(10)
                .catalogAmountSatellites(177)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void dataWithNotDefinedSpeedShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;NA;15;10;177";

        final Data actual = this.dataDeserializer.deserialize(givenDeserialized);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data expected = dataBuilder
                .catalogDateTime(LocalDateTime.of(2022, 11, 15, 14, 56, 43))
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(55)
                        .catalogMinutes(44)
                        .catalogMinuteShare(6025)
                        .catalogType(NORTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(37)
                        .catalogMinutes(39)
                        .catalogMinuteShare(6834)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(NOT_DEFINED_SPEED)
                .catalogCourse(15)
                .catalogHeight(10)
                .catalogAmountSatellites(177)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void dataShouldBeDeserializedWithNotDefinedCourse() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;NA;10;177";

        final Data actual = this.dataDeserializer.deserialize(givenDeserialized);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data expected = dataBuilder
                .catalogDateTime(LocalDateTime.of(2022, 11, 15, 14, 56, 43))
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(55)
                        .catalogMinutes(44)
                        .catalogMinuteShare(6025)
                        .catalogType(NORTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(37)
                        .catalogMinutes(39)
                        .catalogMinuteShare(6834)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(100)
                .catalogCourse(NOT_DEFINED_COURSE)
                .catalogHeight(10)
                .catalogAmountSatellites(177)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void dataShouldBeDeserializedWithNotDefinedHeight() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;NA;177";

        final Data actual = this.dataDeserializer.deserialize(givenDeserialized);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data expected = dataBuilder
                .catalogDateTime(LocalDateTime.of(2022, 11, 15, 14, 56, 43))
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(55)
                        .catalogMinutes(44)
                        .catalogMinuteShare(6025)
                        .catalogType(NORTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(37)
                        .catalogMinutes(39)
                        .catalogMinuteShare(6834)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(100)
                .catalogCourse(15)
                .catalogHeight(NOT_DEFINED_HEIGHT)
                .catalogAmountSatellites(177)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void dataShouldBeDeserializedWithNotDefinedAmountSatellites() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;NA";

        final Data actual = this.dataDeserializer.deserialize(givenDeserialized);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data expected = dataBuilder
                .catalogDateTime(LocalDateTime.of(2022, 11, 15, 14, 56, 43))
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(55)
                        .catalogMinutes(44)
                        .catalogMinuteShare(6025)
                        .catalogType(NORTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(37)
                        .catalogMinutes(39)
                        .catalogMinuteShare(6834)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(100)
                .catalogCourse(15)
                .catalogHeight(10)
                .catalogAmountSatellites(NOT_DEFINED_AMOUNT_SATELLITES)
                .build();

        assertEquals(expected, actual);
    }

    private static String findRegexSerializedData()
            throws Exception {
        final Field regexField = DataDeserializer.class.getDeclaredField(FIELD_NAME_REGEX_SERIALIZED_DATA);
        regexField.setAccessible(true);
        try {
            return (String) regexField.get(null);
        } finally {
            regexField.setAccessible(false);
        }
    }
}
