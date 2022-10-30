package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.entity.DataEntity.Latitude;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception.NotValidInboundSerializedLatitudeException;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type.NORTH;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class LatitudeDeserializerTest {
    private final Deserializer<Latitude> latitudeDeserializer;
    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;

    public LatitudeDeserializerTest() {
        this.latitudeDeserializer = LatitudeDeserializer.create();
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<Deserializer<Latitude>> createdDeserializers
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(LatitudeDeserializer.create());
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
    public void latitudeShouldBeDeserialized() {
        final String givenDeserialized = "5544.6025;N";

        final Latitude actual = this.latitudeDeserializer.deserialize(givenDeserialized);

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final Latitude expected = latitudeBuilder
                .catalogDegrees(55)
                .catalogMinutes(44)
                .catalogMinuteShare(6025)
                .catalogType(NORTH)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void notDefinedLatitudeShouldDeserialized() {
        final String givenDeserialized = "NA;NA";

        final Latitude actual = this.latitudeDeserializer.deserialize(givenDeserialized);

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final Latitude expected = latitudeBuilder.build();

        assertEquals(expected, actual);
    }

    @Test(expected = NotValidInboundSerializedLatitudeException.class)
    public void notValidLatitudeShouldNotBeDeserialized() {
        final String givenDeserialized = "55544.6025;N";
        this.latitudeDeserializer.deserialize(givenDeserialized);
    }

    @Test(expected = NotValidInboundSerializedLatitudeException.class)
    public void latitudeWithNotExistedTypeShouldNotBeDeserialized() {
        final String givenDeserialized = "5544.6025;A";
        this.latitudeDeserializer.deserialize(givenDeserialized);
    }
}
