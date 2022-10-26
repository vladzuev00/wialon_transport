package by.zuevvlad.wialontransport.kafka.serializer;

import by.zuevvlad.wialontransport.kafka.amountbytesfounder.AmountObjectBytesFounder;
import by.zuevvlad.wialontransport.entity.Data;
import org.apache.kafka.common.serialization.Serializer;
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

import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static by.zuevvlad.wialontransport.entity.Data.Latitude;
import static by.zuevvlad.wialontransport.entity.Data.Longitude;
import static by.zuevvlad.wialontransport.entity.Data.Latitude.Type.NORTH;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class DataSerializerTest {
    private static final String METHOD_NAME_WRITING_DATE_TIME_IN_BYTE_BUFFER = "writeDateTime";
    private static final String METHOD_NAME_WRITING_LATITUDE_IN_BYTE_BUFFER = "writeLatitude";
    private static final String METHOD_NAME_WRITING_LONGITUDE_IN_BYTE_BUFFER = "writeLongitude";

    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static final byte[] DATE_TIME_PATTERN_BYTES = DATE_TIME_PATTERN.getBytes(UTF_8);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern(DATE_TIME_PATTERN);
    private static final int AMOUNT_DATA_BYTES =
            Long.BYTES                                            //Entity::id
                    + DATE_TIME_PATTERN_BYTES.length              //Data::dateTime
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

    @Mock
    private ByteBuffer mockedByteBuffer;

    @Mock
    private AmountObjectBytesFounder<Data> mockedAmountDataBytesFounder;

    @Captor
    private ArgumentCaptor<byte[]> byteArrayArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Captor
    private ArgumentCaptor<Character> characterArgumentCaptor;

    @Captor
    private ArgumentCaptor<Data> dataArgumentCaptor;

    @Test
    public void dateTimeShouldBeWrittenInByteBuffer()
            throws Exception {
        final LocalDateTime writtenDateTime = now();
        final Method methodWritingDateTime = DataSerializer.class
                .getDeclaredMethod(METHOD_NAME_WRITING_DATE_TIME_IN_BYTE_BUFFER, ByteBuffer.class, LocalDateTime.class);
        methodWritingDateTime.setAccessible(true);
        try {
            methodWritingDateTime.invoke(null, this.mockedByteBuffer, writtenDateTime);
        } finally {
            methodWritingDateTime.setAccessible(false);
        }

        final String expectedDateTimeDescription = writtenDateTime.format(DATE_TIME_FORMATTER);
        final byte[] expectedDateTimeDescriptionBytes = expectedDateTimeDescription.getBytes(UTF_8);
        verify(this.mockedByteBuffer, times(1)).put(this.byteArrayArgumentCaptor.capture());
        assertArrayEquals(expectedDateTimeDescriptionBytes, this.byteArrayArgumentCaptor.getValue());
    }

    @Test
    public void latitudeShouldBeWrittenInByteBuffer()
            throws Exception {
        final int degrees = 1;
        final int minutes = 2;
        final int minuteShare = 3;
        final Latitude.Type type = NORTH;
        final Latitude mockedLatitude = this.createMockedLatitude(degrees, minutes, minuteShare, type);

        final Method methodWritingLatitude = DataSerializer.class
                .getDeclaredMethod(METHOD_NAME_WRITING_LATITUDE_IN_BYTE_BUFFER, ByteBuffer.class, Latitude.class);
        methodWritingLatitude.setAccessible(true);
        try {
            methodWritingLatitude.invoke(null, this.mockedByteBuffer, mockedLatitude);
        } finally {
            methodWritingLatitude.setAccessible(false);
        }

        verify(mockedLatitude, times(1)).getDegrees();
        verify(mockedLatitude, times(1)).getMinutes();
        verify(mockedLatitude, times(1)).getMinuteShare();
        verify(this.mockedByteBuffer, times(3)).putInt(this.integerArgumentCaptor.capture());
        verify(mockedLatitude, times(1)).getType();
        verify(this.mockedByteBuffer, times(1)).putChar(this.characterArgumentCaptor.capture());

        final List<Integer> expectedCapturedIntegerArguments = List.of(degrees, minutes, minuteShare);
        assertEquals(expectedCapturedIntegerArguments, this.integerArgumentCaptor.getAllValues());

        assertEquals(type.getValue(), this.characterArgumentCaptor.getValue().charValue());
    }

    @Test
    public void longitudeShouldBeWrittenInByteBuffer()
            throws Exception {
        final int degrees = 1;
        final int minutes = 2;
        final int minuteShare = 3;
        final Longitude.Type type = EAST;
        final Longitude mockedLongitude = this.createMockedLongitude(degrees, minutes, minuteShare, type);

        final Method methodWritingLongitude = DataSerializer.class
                .getDeclaredMethod(METHOD_NAME_WRITING_LONGITUDE_IN_BYTE_BUFFER, ByteBuffer.class, Longitude.class);
        methodWritingLongitude.setAccessible(true);
        try {
            methodWritingLongitude.invoke(null, this.mockedByteBuffer, mockedLongitude);
        } finally {
            methodWritingLongitude.setAccessible(false);
        }

        verify(mockedLongitude, times(1)).getDegrees();
        verify(mockedLongitude, times(1)).getMinutes();
        verify(mockedLongitude, times(1)).getMinuteShare();
        verify(this.mockedByteBuffer, times(3)).putInt(this.integerArgumentCaptor.capture());
        verify(mockedLongitude, times(1)).getType();
        verify(this.mockedByteBuffer, times(1)).putChar(this.characterArgumentCaptor.capture());

        final List<Integer> expectedCapturedIntegerArguments = List.of(degrees, minutes, minuteShare);
        assertEquals(expectedCapturedIntegerArguments, this.integerArgumentCaptor.getAllValues());

        assertEquals(type.getValue(), this.characterArgumentCaptor.getValue().charValue());
    }

    @SuppressWarnings("all")
    @Test
    public void dataShouldBeSerialized()
            throws Exception {
        final long id = 255;
        final LocalDateTime dateTime = LocalDateTime
                .of(2022, 10, 10, 10, 10, 10);

        final int latitudeDegrees = 1;
        final int latitudeMinutes = 2;
        final int latitudeMinuteShare = 3;
        final Latitude mockedLatitude = this.createMockedLatitude(latitudeDegrees, latitudeMinutes, latitudeMinuteShare,
                NORTH);

        final int longitudeDegrees = 4;
        final int longitudeMinutes = 5;
        final int longitudeMinuteShare = 6;
        final Longitude mockedLongitude = this.createMockedLongitude(longitudeDegrees, longitudeMinutes,
                longitudeMinuteShare, EAST);

        final int speed = 7;
        final int course = 8;
        final int height = 9;
        final int amountSatellites = 10;

        final Data mockedData = this.createMockedData(id, dateTime, mockedLatitude, mockedLongitude, speed, course,
                height, amountSatellites);
        final Serializer<Data> dataSerializer = this.createDataSerializer();
        when(this.mockedAmountDataBytesFounder.find(any(Data.class))).thenReturn(AMOUNT_DATA_BYTES);

        final byte[] actual = dataSerializer.serialize(null, mockedData);
        final byte[] expected = {0, 0, 0, 0, 0, 0, 0, -1, 49, 48, 46, 49, 48, 46, 50, 48, 50, 50, 32, 49, 48, 58,
                49, 48, 58, 49, 48, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 78, 0, 0, 0, 4, 0, 0, 0, 5, 0, 0, 0, 6, 0,
                69, 0, 0, 0, 7, 0, 0, 0, 8, 0, 0, 0, 9, 0, 0, 0, 10};
        assertArrayEquals(expected, actual);

        verify(this.mockedAmountDataBytesFounder, times(1))
                .find(this.dataArgumentCaptor.capture());
        verify(mockedData, times(1)).getId();
        verify(mockedData, times(1)).getDateTime();

        verify(mockedData, times(1)).getLatitude();
        verify(mockedLatitude, times(1)).getDegrees();
        verify(mockedLatitude, times(1)).getMinutes();
        verify(mockedLatitude, times(1)).getMinuteShare();
        verify(mockedLatitude, times(1)).getType();

        verify(mockedData, times(1)).getLongitude();
        verify(mockedLongitude, times(1)).getDegrees();
        verify(mockedLongitude, times(1)).getMinutes();
        verify(mockedLongitude, times(1)).getMinuteShare();
        verify(mockedLongitude, times(1)).getType();

        verify(mockedData, times(1)).getSpeed();
        verify(mockedData, times(1)).getCourse();
        verify(mockedData, times(1)).getHeight();
        verify(mockedData, times(1)).getAmountSatellites();

        assertSame(mockedData, this.dataArgumentCaptor.getValue());
    }

    private Serializer<Data> createDataSerializer()
            throws Exception {
        final Class<? extends Serializer<Data>> serializerClass = DataSerializer.class;
        final Constructor<? extends Serializer<Data>> serializerConstructor = serializerClass
                .getDeclaredConstructor(AmountObjectBytesFounder.class);
        serializerConstructor.setAccessible(true);
        try {
            return serializerConstructor.newInstance(this.mockedAmountDataBytesFounder);
        } finally {
            serializerConstructor.setAccessible(false);
        }
    }

    @SuppressWarnings("all")
    private Latitude createMockedLatitude(final int degrees, final int minutes, final int minuteShare,
                                          final Latitude.Type type) {
        final Latitude mockedLatitude = mock(Latitude.class);
        when(mockedLatitude.getDegrees()).thenReturn(degrees);
        when(mockedLatitude.getMinutes()).thenReturn(minutes);
        when(mockedLatitude.getMinuteShare()).thenReturn(minuteShare);
        when(mockedLatitude.getType()).thenReturn(type);
        return mockedLatitude;
    }

    @SuppressWarnings("all")
    private Longitude createMockedLongitude(final int degrees, final int minutes, final int minuteShare,
                                            final Longitude.Type type) {
        final Longitude mockedLongitude = mock(Longitude.class);
        when(mockedLongitude.getDegrees()).thenReturn(degrees);
        when(mockedLongitude.getMinutes()).thenReturn(minutes);
        when(mockedLongitude.getMinuteShare()).thenReturn(minuteShare);
        when(mockedLongitude.getType()).thenReturn(type);
        return mockedLongitude;
    }

    @SuppressWarnings("all")
    private Data createMockedData(final long id, final LocalDateTime dateTime, final Latitude latitude,
                                  final Longitude longitude, final int speed, final int course, final int height,
                                  final int amountSatellites) {
        final Data mockedData = mock(Data.class);
        when(mockedData.getId()).thenReturn(id);
        when(mockedData.getDateTime()).thenReturn(dateTime);
        when(mockedData.getLatitude()).thenReturn(latitude);
        when(mockedData.getLongitude()).thenReturn(longitude);
        when(mockedData.getSpeed()).thenReturn(speed);
        when(mockedData.getCourse()).thenReturn(course);
        when(mockedData.getHeight()).thenReturn(height);
        when(mockedData.getAmountSatellites()).thenReturn(amountSatellites);
        return mockedData;
    }
}
