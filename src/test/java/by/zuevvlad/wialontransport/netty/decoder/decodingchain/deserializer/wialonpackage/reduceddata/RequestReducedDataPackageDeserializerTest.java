package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.reduceddata;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.entity.DataEntity;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.reduceddata.RequestReducedDataPackage;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.DataEntity.Longitude.Type.EAST;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class RequestReducedDataPackageDeserializerTest {
    private final PackageDeserializer packageDeserializer;
    private final Supplier<DataBuilder> dataBuilderSupplier;
    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;

    public RequestReducedDataPackageDeserializerTest() {
        this.packageDeserializer = RequestReducedDataPackageDeserializer.create();
        this.dataBuilderSupplier = DataBuilder::new;
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<PackageDeserializer> createdDeserializers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(RequestReducedDataPackageDeserializer.create());
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
    public void packageShouldBeDeserialized() {
        final String givenDeserialized = "#SD#151122;145643;5544.6025;N;03739.6834;E;100;15;10;177\r\n";

        final Package actual = this.packageDeserializer.deserialize(givenDeserialized);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataEntity data = dataBuilder
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
        final Package expected = new RequestReducedDataPackage(data);

        assertEquals(expected, actual);
    }
}
