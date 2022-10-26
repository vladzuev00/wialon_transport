package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.entity.Data.Longitude;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception.NotValidInboundSerializedLongitudeException;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.EAST;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class LongitudeDeserializerTest {
    private final Deserializer<Longitude> longitudeDeserializer;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;

    public LongitudeDeserializerTest() {
        this.longitudeDeserializer = LongitudeDeserializer.create();
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<Deserializer<Longitude>> createdDeserializers
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(LongitudeDeserializer.create());
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
    public void longitudeShouldBeDeserialized() {
        final String givenDeserialized = "03739.6834;E";

        final Longitude actual = this.longitudeDeserializer.deserialize(givenDeserialized);

        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Longitude expected = longitudeBuilder
                .catalogDegrees(37)
                .catalogMinutes(39)
                .catalogMinuteShare(6834)
                .catalogType(EAST)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void notDefinedLongitudeShouldBeDeserialized() {
        final String givenDeserialized = "NA;NA";

        final Longitude actual = this.longitudeDeserializer.deserialize(givenDeserialized);

        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Longitude expected = longitudeBuilder.build();

        assertEquals(expected, actual);
    }

    @Test(expected = NotValidInboundSerializedLongitudeException.class)
    public void notValidLongitudeShouldNotBeDeserialized() {
        final String givenDeserialized = "3739.6834;E";
        this.longitudeDeserializer.deserialize(givenDeserialized);
    }

    @Test(expected = NotValidInboundSerializedLongitudeException.class)
    public void longitudeWithNotExistedTypeShouldNotBeDeserialized() {
        final String givenDeserialized = "03739.6834;A";
        this.longitudeDeserializer.deserialize(givenDeserialized);
    }
}
