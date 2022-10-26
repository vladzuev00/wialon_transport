package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.blackbox;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.*;

public final class RequestBlackBoxPackageDeserializerTest {
    private static final String FIELD_NAME_REGEX_SERIALIZED_DATA = "REGEX_SERIALIZED_DATA";
    private static final String FIELD_NAME_REGEX_SERIALIZED_EXTENDED_DATA = "REGEX_SERIALIZED_EXTENDED_DATA";

    private final String regexSerializedData;
    private final String regexSerializedExtendedData;

    public RequestBlackBoxPackageDeserializerTest()
            throws Exception {
        this.regexSerializedData = findRegexSerializedData();
        this.regexSerializedExtendedData = findRegexSerializedExtendedData();
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<PackageDeserializer> createdDeserializers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(RequestBlackBoxPackageDeserializer.create());
                } catch (InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdDeserializers.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfDeserializers = createdDeserializers.stream()
                .distinct()
                .count();
        final long expectedAmountOfDeserializers = 1;
        assertEquals(expectedAmountOfDeserializers, actualAmountOfDeserializers);
    }

    @Test
    public void givenSerializedShouldBeSerializedData() {
        final String givenSerialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177";
        assertTrue(givenSerialized.matches(this.regexSerializedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedDataWithNotDefinedDate() {
        final String givenSerialized = "NA;145643;5544.6025;N;03739.6834;E;100;15;10;177";
        assertTrue(givenSerialized.matches(this.regexSerializedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedDataWithNotDefinedTime() {
        final String givenSerialized = "151122;NA;5544.6025;N;03739.6834;E;100;15;10;177";
        assertTrue(givenSerialized.matches(this.regexSerializedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedDataWithNotDefinedLatitude() {
        final String givenSerialized = "151122;145643;NA;NA;03739.6834;E;100;15;10;177";
        assertTrue(givenSerialized.matches(this.regexSerializedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedDataWithNotDefinedLongitude() {
        final String givenSerialized = "151122;145643;5544.6025;N;NA;NA;100;15;10;177";
        assertTrue(givenSerialized.matches(this.regexSerializedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedDataWithNotDefinedSpeed() {
        final String givenSerialized = "151122;145643;5544.6025;N;03739.6834;E;NA;15;10;177";
        assertTrue(givenSerialized.matches(this.regexSerializedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedDataWithNotDefinedCourse() {
        final String givenSerialized = "151122;145643;5544.6025;N;03739.6834;E;100;NA;10;177";
        assertTrue(givenSerialized.matches(this.regexSerializedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedDataWithNotDefinedHeight() {
        final String givenSerialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;NA;177";
        assertTrue(givenSerialized.matches(this.regexSerializedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedDataWithNotDefinedAmountSatellites() {
        final String givenSerialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;NA";
        assertTrue(givenSerialized.matches(this.regexSerializedData));
    }

    @Test
    public void givenSerializedShouldNotBeSerializedData() {
        //because of type of latitude
        final String givenSerialized = "151122;145643;5544.6025;A;03739.6834;E;100;15;10;NA";
        assertFalse(givenSerialized.matches(this.regexSerializedExtendedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedExtendedData() {
        final String givenSerialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";
        assertTrue(givenSerialized.matches(this.regexSerializedExtendedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedExtendedDataWithNotDefinedReductionPrecision() {
        final String givenSerialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;NA;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";
        assertTrue(givenSerialized.matches(this.regexSerializedExtendedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedExtendedDataWithNotDefinedInputs() {
        final String givenSerialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;NA;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";
        assertTrue(givenSerialized.matches(this.regexSerializedExtendedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedExtendedDataWithNotDefinedOutputs() {
        final String givenSerialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;NA;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";
        assertTrue(givenSerialized.matches(this.regexSerializedExtendedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedExtendedDataWithNoAnalogInputs() {
        final String givenSerialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;18;"
                + ";"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";
        assertTrue(givenSerialized.matches(this.regexSerializedExtendedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedExtendedDataWithNotDefinedDriverKeyCode() {
        final String givenSerialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "NA;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";
        assertTrue(givenSerialized.matches(this.regexSerializedExtendedData));
    }

    @Test
    public void givenSerializedShouldBeSerializedExtendedDataWithNoParameters() {
        final String givenSerialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "";
        assertTrue(givenSerialized.matches(this.regexSerializedExtendedData));
    }

    @Test
    public void givenSerializedShouldNotBeSerializedExtendedData() {
        //because of first parameter doesn't have type
        final String givenSerialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name::654321,param-name:2:65.4321,param-name:3:param-value";
        assertTrue(givenSerialized.matches(this.regexSerializedExtendedData));
    }

    private static String findRegexSerializedData()
            throws Exception {
        final Field fieldRegexSerializedData = RequestBlackBoxPackageDeserializer.class
                .getDeclaredField(FIELD_NAME_REGEX_SERIALIZED_DATA);
        fieldRegexSerializedData.setAccessible(true);
        try {
            return (String) fieldRegexSerializedData.get(null);
        } finally {
            fieldRegexSerializedData.setAccessible(false);
        }
    }

    private static String findRegexSerializedExtendedData()
            throws Exception {
        final Field fieldRegexSerializedExtendedData = RequestBlackBoxPackageDeserializer.class
                .getDeclaredField(FIELD_NAME_REGEX_SERIALIZED_EXTENDED_DATA);
        fieldRegexSerializedExtendedData.setAccessible(true);
        try {
            return (String) fieldRegexSerializedExtendedData.get(null);
        } finally {
            fieldRegexSerializedExtendedData.setAccessible(false);
        }
    }
}
