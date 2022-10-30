package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.builder.ParameterBuilder;
import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.entity.ExtendedDataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.DataEntity.*;
import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.DataEntity.Longitude.Type.EAST;
import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.*;
import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter.ValueType.*;
import static java.util.Collections.emptyList;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class ExtendedDataDeserializerTest {
    private final Deserializer<ExtendedDataEntity> extendedDataDeserializer;
    private final Supplier<ExtendedDataBuilder> extendedDataBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;
    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<ParameterBuilder> parameterBuilderSupplier;

    public ExtendedDataDeserializerTest() {
        this.extendedDataDeserializer = ExtendedDataDeserializer.create();
        this.extendedDataBuilderSupplier = ExtendedDataBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.parameterBuilderSupplier = ParameterBuilder::new;
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<Deserializer<ExtendedDataEntity>> createdDeserializers
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(ExtendedDataDeserializer.create());
                } catch (InterruptedException cause) {
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
    public void extendedDataShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(17)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataShouldBeDeserializedWithNotDefinedDate() {
        final String givenDeserialized = "NA;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
                        .catalogDateTime(LocalDateTime.of(NOT_DEFINED_DATE, LocalTime.of(14, 56, 43)))
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(17)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataShouldBeDeserializedWithNotDefinedTime() {
        final String givenDeserialized = "151122;NA;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
                        .catalogDateTime(LocalDateTime.of(LocalDate.of(2022, 11, 15), NOT_DEFINED_TIME))
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(17)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataWithNotDefinedLatitudeShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;NA;NA;03739.6834;E;100;15;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(17)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataWithNotDefinedLongitudeShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;NA;NA;100;15;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(17)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataWithNotDefinedSpeedShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;NA;15;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(17)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataWithNotDefinedCourseShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;NA;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(17)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataWithNotDefinedHeightShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;NA;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(17)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataWithNotDefinedAmountSatellitesShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;NA;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(17)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataWithNotDefinedReductionPrecisionShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;NA;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(NOT_DEFINED_REDUCTION_PRECISION)
                .catalogInputs(17)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataWithNotDefinedAmountInputsShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;NA;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(NOT_DEFINED_INPUTS)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataWithNotDefinedOutputsShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;NA;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(17)
                .catalogOutputs(NOT_DEFINED_OUTPUTS)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataWithNoAnalogInputsShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;18;"
                + ";"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(17)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataWithNotDefinedDriverKeyCodeShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "NA;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(17)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode(NOT_DEFINED_DRIVER_KEY_CODE)
                .catalogParameters(List.of(
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(INTEGER)
                                .catalogValue(654321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(DOUBLE)
                                .catalogValue(65.4321)
                                .build(),
                        parameterBuilder
                                .catalogName("param-name")
                                .catalogValueType(STRING)
                                .catalogValue("param-value")
                                .build()))
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void extendedDataWithNotParametersShouldBeDeserialized() {
        final String givenDeserialized = "151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "";

        final ExtendedDataEntity actual = this.extendedDataDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ExtendedDataEntity expected = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(545.4554)
                .catalogInputs(17)
                .catalogOutputs(18)
                .catalogAnalogInputs(new double[]{5.5, 4343.454544334, 454.433, 1})
                .catalogDriverKeyCode("keydrivercode")
                .catalogParameters(emptyList())
                .build();

        assertEquals(expected, actual);
    }
}
