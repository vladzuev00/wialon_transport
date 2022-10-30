package by.zuevvlad.wialontransport.kafka.amountbytesfounder;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.entity.DataEntity;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.DataEntity.Longitude.Type.EAST;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.now;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class AmountDataBytesFounderTest {
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static final byte[] DATE_TIME_PATTERN_BYTES = DATE_TIME_PATTERN.getBytes(UTF_8);

    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;

    public AmountDataBytesFounderTest() {
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<AmountObjectBytesFounder<DataEntity>> createdBytesFounders
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdBytesFounders.put(AmountDataBytesFounder.create());
                } catch (InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdBytesFounders.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfBytesFounders = createdBytesFounders.stream().distinct().count();
        final long expectedAmountOfBytesFounders = 1;
        assertEquals(expectedAmountOfBytesFounders, actualAmountOfBytesFounders);
    }

    @Test
    public void amountDataBytesShouldBeFound()
            throws Exception {
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final DataEntity data = dataBuilder
                .catalogId(255)
                .catalogDateTime(now())
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(23)
                        .catalogMinutes(24)
                        .catalogMinuteShare(25)
                        .catalogType(NORTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(26)
                        .catalogMinutes(27)
                        .catalogMinuteShare(28)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(29)
                .catalogHeight(30)
                .catalogAmountSatellites(31)
                .build();

        final AmountObjectBytesFounder<DataEntity> amountDataBytesFounder = this.createAmountDataBytesFounder();
        final int actual = amountDataBytesFounder.find(data);
        final int expected = Long.BYTES                      //Entity::id
                + DATE_TIME_PATTERN_BYTES.length             //Data::dateTime
                + Integer.BYTES * 3 + Character.BYTES        //Data::latitude
                + Integer.BYTES * 3 + Character.BYTES        //Data::longitude
                + Integer.BYTES                              //Data::speed
                + Integer.BYTES                              //Data::course
                + Integer.BYTES                              //Data::height
                + Integer.BYTES;                             //Data::amountSatellites
        assertEquals(expected, actual);
    }

    private AmountObjectBytesFounder<DataEntity> createAmountDataBytesFounder()
            throws Exception {
        final Class<? extends AmountObjectBytesFounder<DataEntity>> amountDataBytesFounderClass
                = AmountDataBytesFounder.class;
        final Constructor<? extends AmountObjectBytesFounder<DataEntity>> amountDataBytesFounderConstructor
                = amountDataBytesFounderClass.getDeclaredConstructor();
        amountDataBytesFounderConstructor.setAccessible(true);
        try {
            return amountDataBytesFounderConstructor.newInstance();
        } finally {
            amountDataBytesFounderConstructor.setAccessible(false);
        }
    }
}
