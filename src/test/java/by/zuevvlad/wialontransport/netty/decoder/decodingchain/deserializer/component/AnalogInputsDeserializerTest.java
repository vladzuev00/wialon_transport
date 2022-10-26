package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception.NotValidInboundSerializedAnalogInputsException;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public final class AnalogInputsDeserializerTest {
    private final Deserializer<double[]> deserializer;

    public AnalogInputsDeserializerTest() {
        this.deserializer = AnalogInputsDeserializer.create();
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<Deserializer<double[]>> createdDeserializers
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(AnalogInputsDeserializer.create());
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
    public void analogInputsShouldBeDeserialized() {
        final String givenDeserialized = "5.5,4343.454544334,454.433,1";

        final double[] actual = this.deserializer.deserialize(givenDeserialized);
        final double[] expected = new double[]{5.5, 4343.454544334, 454.433, 1};
        assertArrayEquals(expected, actual, 0.);
    }

    @Test
    public void emptyAnalogInputsShouldBeDeserialized() {
        final String givenDeserialized = "";

        final double[] actual = this.deserializer.deserialize(givenDeserialized);
        final double[] expected = new double[0];
        assertArrayEquals(expected, actual, 0.);
    }

    @Test(expected = NotValidInboundSerializedAnalogInputsException.class)
    public void notValidAnalogInputsShouldNotBeDeserialized() {
        final String givenDeserialized = "5.5,4343.4545a44334,454.433,1";
        this.deserializer.deserialize(givenDeserialized);
    }
}
