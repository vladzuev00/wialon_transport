package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception.NotValidInboundSerializedDateTimeException;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.time.Duration.between;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class DateTimeDeserializerTest {
    private final Deserializer<LocalDateTime> dateTimeDeserializer;

    public DateTimeDeserializerTest() {
        this.dateTimeDeserializer = DateTimeDeserializer.create();
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<Deserializer<LocalDateTime>> createdDeserializers
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(DateTimeDeserializer.create());
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
    public void dateTimeShouldBeDeserialized() {
        final String givenDeserialized = "180200;165432";

        final LocalDateTime actual = this.dateTimeDeserializer.deserialize(givenDeserialized);
        final LocalDateTime expected = LocalDateTime.of(2000, 2, 18, 16, 54, 32);
        assertEquals(expected, actual);
    }

    @Test
    public void dateTimeWithNotDefinedDateAndTimeShouldBeDeserialized() {
        final String givenDeserialized = "NA;NA";

        final LocalDateTime actual = this.dateTimeDeserializer.deserialize(givenDeserialized);
        final LocalDateTime expected = now();
        assertTrue(between(expected, actual).get(SECONDS) < 1);
    }

    @Test(expected = NotValidInboundSerializedDateTimeException.class)
    public void dateTimeShouldNotBeDeserializedBecauseOfNotValidFormat() {
        final String givenDeserialized = "1180200;165432";  //not valid date

        this.dateTimeDeserializer.deserialize(givenDeserialized);
    }
}
