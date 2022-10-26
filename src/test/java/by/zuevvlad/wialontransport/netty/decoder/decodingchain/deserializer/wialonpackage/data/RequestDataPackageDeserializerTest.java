package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.data;

import by.zuevvlad.wialontransport.builder.ParameterBuilder;
import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.entity.ExtendedDataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.entity.ExtendedData;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.data.RequestDataPackage;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.Data.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.EAST;
import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType.*;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class RequestDataPackageDeserializerTest {
    private final PackageDeserializer packageDeserializer;
    private final Supplier<ExtendedDataBuilder> extendedDataBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;
    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<ParameterBuilder> parameterBuilderSupplier;

    public RequestDataPackageDeserializerTest() {
        this.packageDeserializer = RequestDataPackageDeserializer.create();
        this.extendedDataBuilderSupplier = ExtendedDataBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.parameterBuilderSupplier = ParameterBuilder::new;
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<PackageDeserializer> createdDeserializers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(RequestDataPackageDeserializer.create());
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
    public void requestDataPackageShouldBeDeserialized() {
        final String givenDeserialized = "#D#151122;145643;5544.6025;N;03739.6834;E;100;15;10;177;545.4554;17;18;"
                + "5.5,4343.454544334,454.433,1;"
                + "keydrivercode;"
                + "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value\r\n";

        final Package actual = this.packageDeserializer.deserialize(givenDeserialized);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedData extendedData = extendedDataBuilder
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
        final RequestDataPackage expected = new RequestDataPackage(extendedData);

        assertEquals(expected, actual);
    }
}
