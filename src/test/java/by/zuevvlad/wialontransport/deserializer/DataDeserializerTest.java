package by.zuevvlad.wialontransport.deserializer;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.deserializer.exception.DeserializationException;
import by.zuevvlad.wialontransport.entity.Data;
import org.apache.kafka.common.serialization.Deserializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import static java.nio.ByteBuffer.allocate;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.ByteBuffer.wrap;
import static java.time.LocalDateTime.parse;
import static by.zuevvlad.wialontransport.entity.Data.Latitude;
import static by.zuevvlad.wialontransport.entity.Data.Latitude.Type.SOUTH;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static by.zuevvlad.wialontransport.entity.Data.Longitude;
import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.EAST;
import static java.util.List.of;

@RunWith(MockitoJUnitRunner.class)
public final class DataDeserializerTest {

    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern(DATE_TIME_PATTERN);

    private static final String METHOD_NAME_READING_DATE_TIME = "readDateTime";
    private static final String METHOD_NAME_READING_LATITUDE = "readLatitude";
    private static final String METHOD_NAME_READING_LONGITUDE = "readLongitude";

    private static final int AMOUNT_BYTES_LATITUDE =
            Integer.BYTES                        //Data::Latitude::degrees
                    + Integer.BYTES              //Data::Latitude::minutes
                    + Integer.BYTES              //Data::Latitude::minuteShare
                    + Character.BYTES;           //Data::Latitude::Type::value
    private static final int AMOUNT_BYTES_LONGITUDE =
            Integer.BYTES                        //Data::Longitude::degrees
                    + Integer.BYTES              //Data::Longitude::minutes
                    + Integer.BYTES              //Data::Longitude::minuteShare
                    + Character.BYTES;           //Data::Longitude::Type::value
    private static final int AMOUNT_BYTES_DATA =
            Long.BYTES                                            //DATA::id
                    + DATE_TIME_PATTERN.getBytes(UTF_8).length    //Data::dateTime
                    + Integer.BYTES                               //Data::Latitude::degrees
                    + Integer.BYTES                               //Data::Latitude::minutes
                    + Integer.BYTES                               //Data::Latitude::minuteShare
                    + Character.BYTES                             //Data::Latitude::Type::value
                    + Integer.BYTES                               //Data::Longitude::degrees
                    + Integer.BYTES                               //Data::Longitude::minutes
                    + Integer.BYTES                               //Data::Longitude::minuteShare
                    + Character.BYTES                             //Data::Longitude::Type::value
                    + Integer.BYTES                               //Data::speed
                    + Integer.BYTES                               //Data::course
                    + Integer.BYTES                               //Data::height
                    + Integer.BYTES;                              //Data::amountOfSatellites

    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;

    @Mock
    private Supplier<LatitudeBuilder> mockedLatitudeBuilderSupplier;

    @Mock
    private Supplier<LongitudeBuilder> mockedLongitudeBuilderSupplier;

    @Mock
    private Supplier<DataBuilder> mockedDataBuilderSupplier;

    @Mock
    private LatitudeBuilder mockedLatitudeBuilder;

    @Mock
    private LongitudeBuilder mockedLongitudeBuilder;

    @Mock
    private DataBuilder mockedDataBuilder;

    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Captor
    private ArgumentCaptor<Latitude.Type> latitudeTypeArgumentCaptor;

    @Captor
    private ArgumentCaptor<Longitude.Type> longitudeTypeArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Captor
    private ArgumentCaptor<LocalDateTime> localDateTimeArgumentCaptor;

    @Captor
    private ArgumentCaptor<Latitude> latitudeArgumentCaptor;

    @Captor
    private ArgumentCaptor<Longitude> longitudeArgumentCaptor;

    public DataDeserializerTest() {
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<Deserializer<Data>> createdDeserializers = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(DataDeserializer.create());
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

    @SuppressWarnings("all")
    @Test
    public void dateTimeShouldBeReadFromByteBuffer()
            throws Exception {
        final String dateTimeDescription = "12.11.2003 11:30:13";
        final byte[] dateTimeDescriptionBytes = dateTimeDescription.getBytes(UTF_8);
        final ByteBuffer byteBuffer = wrap(dateTimeDescriptionBytes);

        final LocalDateTime actual;
        final Method readingMethod = DataDeserializer.class
                .getDeclaredMethod(METHOD_NAME_READING_DATE_TIME, ByteBuffer.class);
        final Deserializer<Data> dataDeserializer = this.createDataDeserializer();
        readingMethod.setAccessible(true);
        try {
            actual = (LocalDateTime) readingMethod.invoke(dataDeserializer, byteBuffer);
        } finally {
            readingMethod.setAccessible(false);
        }

        final LocalDateTime expected = parse(dateTimeDescription, DATE_TIME_FORMATTER);
        assertEquals(expected, actual);
    }

    @Test
    public void latitudeShouldBeReadFromByteBuffer()
            throws Exception {
        final int degrees = 10;
        final int minutes = 11;
        final int minuteShare = 12;
        final Latitude.Type type = SOUTH;

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final Latitude expected = latitudeBuilder
                .catalogDegrees(degrees)
                .catalogMinutes(minutes)
                .catalogMinuteShare(minuteShare)
                .catalogType(type)
                .build();
        when(this.mockedLatitudeBuilder.build()).thenReturn(expected);

        final ByteBuffer byteBuffer = allocate(AMOUNT_BYTES_LATITUDE)
                .putInt(degrees)
                .putInt(minutes)
                .putInt(minuteShare)
                .putChar(type.getValue())
                .rewind();

        final Latitude actual;
        final Method readingMethod = DataDeserializer.class
                .getDeclaredMethod(METHOD_NAME_READING_LATITUDE, ByteBuffer.class);
        final Deserializer<Data> dataDeserializer = this.createDataDeserializer();
        readingMethod.setAccessible(true);
        try {
            actual = (Latitude) readingMethod.invoke(dataDeserializer, byteBuffer);
        } finally {
            readingMethod.setAccessible(false);
        }

        assertEquals(expected, actual);

        verify(this.mockedLatitudeBuilder, times(1)).catalogDegrees(this.integerArgumentCaptor.capture());
        assertEquals(degrees, this.integerArgumentCaptor.getValue().intValue());

        verify(this.mockedLatitudeBuilder, times(1)).catalogMinutes(this.integerArgumentCaptor.capture());
        assertEquals(minutes, this.integerArgumentCaptor.getValue().intValue());

        verify(this.mockedLatitudeBuilder, times(1)).catalogMinuteShare(this.integerArgumentCaptor.capture());
        assertEquals(minuteShare, this.integerArgumentCaptor.getValue().intValue());

        verify(this.mockedLatitudeBuilder, times(1)).catalogType(this.latitudeTypeArgumentCaptor.capture());
        assertEquals(type, this.latitudeTypeArgumentCaptor.getValue());

        verify(this.mockedLatitudeBuilder, times(1)).build();
    }

    @Test
    public void longitudeShouldBeReadFromByteBuffer()
            throws Exception {
        final int degrees = 10;
        final int minutes = 11;
        final int minuteShare = 12;
        final Longitude.Type type = EAST;

        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Longitude expected = longitudeBuilder
                .catalogDegrees(degrees)
                .catalogMinutes(minutes)
                .catalogMinuteShare(minuteShare)
                .catalogType(type)
                .build();
        when(this.mockedLongitudeBuilder.build()).thenReturn(expected);

        final ByteBuffer byteBuffer = allocate(AMOUNT_BYTES_LONGITUDE)
                .putInt(degrees)
                .putInt(minutes)
                .putInt(minuteShare)
                .putChar(type.getValue())
                .rewind();

        final Longitude actual;
        final Method readingMethod = DataDeserializer.class
                .getDeclaredMethod(METHOD_NAME_READING_LONGITUDE, ByteBuffer.class);
        final Deserializer<Data> dataDeserializer = this.createDataDeserializer();
        readingMethod.setAccessible(true);
        try {
            actual = (Longitude) readingMethod.invoke(dataDeserializer, byteBuffer);
        } finally {
            readingMethod.setAccessible(false);
        }

        assertEquals(expected, actual);

        verify(this.mockedLongitudeBuilder, times(1)).catalogDegrees(this.integerArgumentCaptor.capture());
        assertEquals(degrees, this.integerArgumentCaptor.getValue().intValue());

        verify(this.mockedLongitudeBuilder, times(1)).catalogMinutes(this.integerArgumentCaptor.capture());
        assertEquals(minutes, this.integerArgumentCaptor.getValue().intValue());

        verify(this.mockedLongitudeBuilder, times(1)).catalogMinuteShare(this.integerArgumentCaptor.capture());
        assertEquals(minuteShare, this.integerArgumentCaptor.getValue().intValue());

        verify(this.mockedLongitudeBuilder, times(1)).catalogType(this.longitudeTypeArgumentCaptor.capture());
        assertEquals(type, this.longitudeTypeArgumentCaptor.getValue());

        verify(this.mockedLongitudeBuilder, times(1)).build();
    }

    @Test
    public void dataShouldBeDeserialized()
            throws Exception {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();

        final long id = 255;

        final String dateTimeDescription = "12.11.2003 11:30:13";
        final LocalDateTime dateTime = parse(dateTimeDescription, DATE_TIME_FORMATTER);

        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final int latitudeDegrees = 10;
        final int latitudeMinutes = 11;
        final int latitudeMinuteShare = 12;
        final Latitude latitude = latitudeBuilder
                .catalogDegrees(latitudeDegrees)
                .catalogMinutes(latitudeMinutes)
                .catalogMinuteShare(latitudeMinuteShare)
                .catalogType(SOUTH)
                .build();

        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final int longitudeDegrees = 13;
        final int longitudeMinutes = 14;
        final int longitudeMinuteShare = 15;
        final Longitude longitude = longitudeBuilder
                .catalogDegrees(longitudeDegrees)
                .catalogMinutes(longitudeMinutes)
                .catalogMinuteShare(longitudeMinuteShare)
                .catalogType(EAST)
                .build();

        final int speed = 16;
        final int course = 17;
        final int height = 18;
        final int amountSatellites = 19;

        final Data expected = dataBuilder
                .catalogId(id)
                .catalogDateTime(dateTime)
                .catalogLatitude(latitude)
                .catalogLongitude(longitude)
                .catalogSpeed(speed)
                .catalogCourse(course)
                .catalogHeight(height)
                .catalogAmountSatellites(amountSatellites)
                .build();
        final byte[] serializedExpectedData = this.findSerializedData(expected);

        when(this.mockedLatitudeBuilder.build()).thenReturn(latitude);
        when(this.mockedLongitudeBuilder.build()).thenReturn(longitude);
        when(this.mockedDataBuilder.build()).thenReturn(expected);

        final Deserializer<Data> dataDeserializer = this.createDataDeserializer();
        final Data actual = dataDeserializer.deserialize(null, serializedExpectedData);
        assertEquals(expected, actual);

        verify(this.mockedDataBuilder, times(1)).catalogId(this.longArgumentCaptor.capture());
        assertEquals(expected.getId(), this.longArgumentCaptor.getValue().longValue());

        verify(this.mockedDataBuilder, times(1)).catalogDateTime(this.localDateTimeArgumentCaptor.capture());
        assertEquals(expected.getDateTime(), this.localDateTimeArgumentCaptor.getValue());

        verify(this.mockedDataBuilder, times(1)).catalogLatitude(this.latitudeArgumentCaptor.capture());
        assertEquals(expected.getLatitude(), this.latitudeArgumentCaptor.getValue());

        verify(this.mockedDataBuilder, times(1)).catalogLongitude(this.longitudeArgumentCaptor.capture());
        assertEquals(expected.getLongitude(), this.longitudeArgumentCaptor.getValue());

        verify(this.mockedDataBuilder, times(1)).catalogSpeed(this.integerArgumentCaptor.capture());
        verify(this.mockedDataBuilder, times(1)).catalogCourse(this.integerArgumentCaptor.capture());
        verify(this.mockedDataBuilder, times(1)).catalogHeight(this.integerArgumentCaptor.capture());
        verify(this.mockedDataBuilder, times(1)).catalogAmountSatellites(this.integerArgumentCaptor.capture());
        final List<Integer> expectedCapturedIntegerArguments = of(expected.getSpeed(), expected.getCourse(),
                expected.getHeight(), expected.getAmountSatellites());
        assertEquals(expectedCapturedIntegerArguments, this.integerArgumentCaptor.getAllValues());
    }

    @Test(expected = DeserializationException.class)
    public void dataShouldNotBeDeserializedBecauseByteArrayIsNull()
            throws Exception {
        final Deserializer<Data> dataDeserializer = this.createDataDeserializer();
        dataDeserializer.deserialize("", null);
    }

    @Test(expected = DeserializationException.class)
    public void dataShouldNotBeDeserializedBecauseOfLengthByteArrayIsNotAsRequired()
            throws Exception {
        final Deserializer<Data> dataDeserializer = this.createDataDeserializer();
        dataDeserializer.deserialize("", new byte[AMOUNT_BYTES_DATA * 2]);
    }

    private Deserializer<Data> createDataDeserializer()
            throws Exception {
        when(this.mockedLatitudeBuilderSupplier.get()).thenReturn(this.mockedLatitudeBuilder);
        when(this.mockedLatitudeBuilder.catalogDegrees(anyInt())).thenReturn(this.mockedLatitudeBuilder);
        when(this.mockedLatitudeBuilder.catalogMinutes(anyInt())).thenReturn(this.mockedLatitudeBuilder);
        when(this.mockedLatitudeBuilder.catalogMinuteShare(anyInt())).thenReturn(this.mockedLatitudeBuilder);
        when(this.mockedLatitudeBuilder.catalogType(any(Latitude.Type.class))).thenReturn(this.mockedLatitudeBuilder);

        when(this.mockedLongitudeBuilderSupplier.get()).thenReturn(this.mockedLongitudeBuilder);
        when(this.mockedLongitudeBuilder.catalogDegrees(anyInt())).thenReturn(this.mockedLongitudeBuilder);
        when(this.mockedLongitudeBuilder.catalogMinutes(anyInt())).thenReturn(this.mockedLongitudeBuilder);
        when(this.mockedLongitudeBuilder.catalogMinuteShare(anyInt())).thenReturn(this.mockedLongitudeBuilder);
        when(this.mockedLongitudeBuilder.catalogType(any(Longitude.Type.class)))
                .thenReturn(this.mockedLongitudeBuilder);

        when(this.mockedDataBuilderSupplier.get()).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogId(anyLong())).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogDateTime(any(LocalDateTime.class))).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogLatitude(any(Latitude.class))).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogLongitude(any(Longitude.class))).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogSpeed(anyInt())).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogCourse(anyInt())).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogHeight(anyInt())).thenReturn(this.mockedDataBuilder);
        when(this.mockedDataBuilder.catalogAmountSatellites(anyInt())).thenReturn(this.mockedDataBuilder);

        final Class<? extends Deserializer<Data>> deserializerClass = DataDeserializer.class;
        final Constructor<? extends Deserializer<Data>> deserializerConstructor = deserializerClass
                .getDeclaredConstructor(Supplier.class, Supplier.class, Supplier.class);
        deserializerConstructor.setAccessible(true);
        try {
            return deserializerConstructor.newInstance(this.mockedLatitudeBuilderSupplier,
                    this.mockedLongitudeBuilderSupplier, this.mockedDataBuilderSupplier);
        } finally {
            deserializerConstructor.setAccessible(false);
        }
    }

    private byte[] findSerializedData(final Data data) {
        final byte[] serializedData = new byte[AMOUNT_BYTES_DATA];
        final ByteBuffer byteBufferSerializedData = wrap(serializedData);
        byteBufferSerializedData.putLong(data.getId());

        final LocalDateTime dateTime = data.getDateTime();
        final String dateTimeDescription = dateTime.format(DATE_TIME_FORMATTER);
        final byte[] dateTimeDescriptionBytes = dateTimeDescription.getBytes(UTF_8);
        byteBufferSerializedData.put(dateTimeDescriptionBytes);

        final Latitude latitude = data.getLatitude();
        byteBufferSerializedData.putInt(latitude.getDegrees());
        byteBufferSerializedData.putInt(latitude.getMinutes());
        byteBufferSerializedData.putInt(latitude.getMinuteShare());

        final Latitude.Type latitudeType = latitude.getType();
        byteBufferSerializedData.putChar(latitudeType.getValue());

        final Longitude longitude = data.getLongitude();
        byteBufferSerializedData.putInt(longitude.getDegrees());
        byteBufferSerializedData.putInt(longitude.getMinutes());
        byteBufferSerializedData.putInt(longitude.getMinuteShare());

        final Longitude.Type longitudeType = longitude.getType();
        byteBufferSerializedData.putChar(longitudeType.getValue());

        byteBufferSerializedData.putInt(data.getSpeed());
        byteBufferSerializedData.putInt(data.getCourse());
        byteBufferSerializedData.putInt(data.getHeight());
        byteBufferSerializedData.putInt(data.getAmountSatellites());

        byteBufferSerializedData.rewind();

        return byteBufferSerializedData.array();
    }
}
