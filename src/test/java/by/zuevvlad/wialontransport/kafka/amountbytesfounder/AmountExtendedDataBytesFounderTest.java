package by.zuevvlad.wialontransport.kafka.amountbytesfounder;

import by.zuevvlad.wialontransport.kafka.amountbytesfounder.exception.ImpossibleToFindAmountObjectBytesException;
import by.zuevvlad.wialontransport.builder.ParameterBuilder;
import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.entity.ExtendedDataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.entity.ExtendedData;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.Data.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.EAST;
import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter;
import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType.*;
import static java.lang.Class.forName;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.now;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;
import static java.util.List.of;

public final class AmountExtendedDataBytesFounderTest {
    private static final String CLASS_NAME_AMOUNT_DOUBLE_ARRAY_BYTES_FOUNDER
            = "by.zuevvlad.wialontransport.kafka.amountbytesfounder.AmountExtendedDataBytesFounder"
            + "$AmountDoubleArrayBytesFounder";
    private static final String CLASS_NAME_AMOUNT_STRING_BYTES_FOUNDER =
            "by.zuevvlad.wialontransport.kafka.amountbytesfounder.AmountExtendedDataBytesFounder"
                    + "$AmountStringBytesFounder";
    private static final String CLASS_NAME_AMOUNT_PARAMETER_BYTES_FOUNDER
            = "by.zuevvlad.wialontransport.kafka.amountbytesfounder.AmountExtendedDataBytesFounder"
            + "$AmountParametersBytesFounder$AmountParameterBytesFounder";
    private static final String CLASS_NAME_AMOUNT_PARAMETERS_BYTES_FOUNDER
            = "by.zuevvlad.wialontransport.kafka.amountbytesfounder.AmountExtendedDataBytesFounder"
            + "$AmountParametersBytesFounder";

    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static final byte[] DATE_TIME_PATTERN_BYTES = DATE_TIME_PATTERN.getBytes(UTF_8);

    private final Supplier<ParameterBuilder> parameterBuilderSupplier;
    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<DataBuilder> dataBuilderSupplier;
    private final Supplier<ExtendedDataBuilder> extendedDataBuilderSupplier;

    public AmountExtendedDataBytesFounderTest() {
        this.parameterBuilderSupplier = ParameterBuilder::new;
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.dataBuilderSupplier = DataBuilder::new;
        this.extendedDataBuilderSupplier = ExtendedDataBuilder::new;
    }

    @Test
    public void amountDoubleArrayBytesShouldBeFound()
            throws Exception {
        final AmountObjectBytesFounder<double[]> amountDoubleArrayBytesFounder
                = this.createAmountDoubleArrayBytesFounder();
        final double[] array = {3., 5., 7.};
        final int actual = amountDoubleArrayBytesFounder.find(array);
        final int expected = Double.BYTES * 3 + Integer.BYTES;
        assertEquals(expected, actual);
    }

    @Test
    public void amountEmptyDoubleArrayBytesShouldBeFound()
            throws Exception {
        final AmountObjectBytesFounder<double[]> amountDoubleArrayBytesFounder
                = this.createAmountDoubleArrayBytesFounder();
        final double[] array = {};
        final int actual = amountDoubleArrayBytesFounder.find(array);
        final int expected = Integer.BYTES;
        assertEquals(expected, actual);
    }

    @Test
    public void amountStringBytesShouldBeFound()
            throws Exception {
        final AmountObjectBytesFounder<String> amountStringBytesFounder = this.createAmountStringBytesFounder();
        final String string = "string";
        final int actual = amountStringBytesFounder.find(string);
        final int expected = Integer.BYTES + string.getBytes(UTF_8).length;
        assertEquals(expected, actual);
    }

    @Test
    public void amountEmptyStringBytesShouldBeFound()
            throws Exception {
        final AmountObjectBytesFounder<String> amountStringBytesFounder = this.createAmountStringBytesFounder();
        final String string = "";
        final int actual = amountStringBytesFounder.find(string);
        final int expected = Integer.BYTES;
        assertEquals(expected, actual);
    }

    @Test
    public void amountIntegerParameterBytesShouldBeFound()
            throws Exception {
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final String name = "integer parameter";
        final int value = 10;
        final Parameter parameter = parameterBuilder
                .catalogName(name)
                .catalogValueType(INTEGER)
                .catalogValue(value)
                .build();

        final AmountObjectBytesFounder<Parameter> amountParameterBytesFounder
                = this.createAmountParameterBytesFounder();
        final int actual = amountParameterBytesFounder.find(parameter);
        final int expected = Integer.BYTES + name.getBytes(UTF_8).length
                + Byte.BYTES
                + Integer.BYTES;

        assertEquals(expected, actual);
    }

    @Test
    public void amountDoubleParameterBytesShouldBeFound()
            throws Exception {
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final String name = "double parameter";
        final double value = 10;
        final Parameter parameter = parameterBuilder
                .catalogName(name)
                .catalogValueType(DOUBLE)
                .catalogValue(value)
                .build();

        final AmountObjectBytesFounder<Parameter> amountParameterBytesFounder
                = this.createAmountParameterBytesFounder();
        final int actual = amountParameterBytesFounder.find(parameter);
        final int expected = Integer.BYTES + name.getBytes(UTF_8).length
                + Byte.BYTES
                + Double.BYTES;

        assertEquals(expected, actual);
    }

    @Test
    public void amountStringParameterBytesShouldBeFound()
            throws Exception {
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final String name = "string parameter";
        final String value = "string value";
        final Parameter parameter = parameterBuilder
                .catalogName(name)
                .catalogValueType(STRING)
                .catalogValue(value)
                .build();

        final AmountObjectBytesFounder<Parameter> amountParameterBytesFounder
                = this.createAmountParameterBytesFounder();
        final int actual = amountParameterBytesFounder.find(parameter);
        final int expected = Integer.BYTES + name.getBytes(UTF_8).length
                + Byte.BYTES
                + Integer.BYTES + value.getBytes(UTF_8).length;

        assertEquals(expected, actual);
    }

    @Test(expected = ImpossibleToFindAmountObjectBytesException.class)
    public void amountNotDefinedParameterBytesShouldNotBeFound()
            throws Exception {
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final String name = "parameter";
        final String value = "string value";
        final Parameter parameter = parameterBuilder
                .catalogName(name)
                .catalogValueType(NOT_DEFINED)
                .catalogValue(value)
                .build();

        final AmountObjectBytesFounder<Parameter> amountParameterBytesFounder
                = this.createAmountParameterBytesFounder();
        amountParameterBytesFounder.find(parameter);
    }

    @Test
    public void amountParametersBytesShouldBeFound()
            throws Exception {
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();

        final String nameIntegerParameter = "integer parameter";
        final int valueIntegerParameter = 10;
        final Parameter integerParameter = parameterBuilder
                .catalogName(nameIntegerParameter)
                .catalogValueType(INTEGER)
                .catalogValue(valueIntegerParameter)
                .build();

        final String nameDoubleParameter = "double parameter";
        final double valueDoubleParameter = 10;
        final Parameter doubleParameter = parameterBuilder
                .catalogName(nameDoubleParameter)
                .catalogValueType(DOUBLE)
                .catalogValue(valueDoubleParameter)
                .build();

        final String nameStringParameter = "string parameter";
        final String valueStringParameter = "string value";
        final Parameter stringParameter = parameterBuilder
                .catalogName(nameStringParameter)
                .catalogValueType(STRING)
                .catalogValue(valueStringParameter)
                .build();

        final List<Parameter> parameters = of(integerParameter, doubleParameter, stringParameter);

        final AmountObjectBytesFounder<List<Parameter>> amountParametersBytesFounder
                = this.createAmountParametersBytesFounder();
        final int actual = amountParametersBytesFounder.find(parameters);
        final int expected =
                Integer.BYTES

                        + Integer.BYTES + nameIntegerParameter.getBytes(UTF_8).length
                        + Byte.BYTES
                        + Integer.BYTES

                        + Integer.BYTES + nameDoubleParameter.getBytes(UTF_8).length
                        + Byte.BYTES
                        + Double.BYTES

                        + Integer.BYTES + nameStringParameter.getBytes(UTF_8).length
                        + Byte.BYTES
                        + Integer.BYTES + valueStringParameter.getBytes(UTF_8).length;

        assertEquals(expected, actual);
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<AmountObjectBytesFounder<ExtendedData>> createdBytesFounders
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdBytesFounders.put(AmountExtendedDataBytesFounder.create());
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
    public void amountExtendedDataBytesShouldBeFound()
            throws Exception {
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();

        final ExtendedData extendedData = extendedDataBuilder
                .catalogData(dataBuilder
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
                        .build())
                .catalogReductionPrecision(32.)
                .catalogInputs(33)
                .catalogOutputs(34)
                .catalogAnalogInputs(new double[]{1., 2., 3.})
                .catalogDriverKeyCode("first driver key code")
                .catalogParameters(of(
                        parameterBuilder
                                .catalogName("first parameter")
                                .catalogValueType(INTEGER)
                                .catalogValue(36)
                                .build(),
                        parameterBuilder
                                .catalogName("second parameter")
                                .catalogValueType(STRING)
                                .catalogValue("37")
                                .build()
                )).build();

        final AmountObjectBytesFounder<ExtendedData> amountExtendedDataBytesFounder
                = this.createAmountExtendedDataBytesFounder();
        final int actual = amountExtendedDataBytesFounder.find(extendedData);
        final int expected = Long.BYTES                      //Entity::id

                + DATE_TIME_PATTERN_BYTES.length             //Data::dateTime
                + Integer.BYTES * 3 + Character.BYTES        //Data::latitude
                + Integer.BYTES * 3 + Character.BYTES        //Data::longitude
                + Integer.BYTES                              //Data::speed
                + Integer.BYTES                              //Data::course
                + Integer.BYTES                              //Data::height
                + Integer.BYTES                              //Data::amountSatellites

                + Double.BYTES                               //ExtendedData::reductionPrecision
                + Integer.BYTES                              //ExtendedData::inputs
                + Integer.BYTES                              //ExtendedData::outputs
                + Integer.BYTES + Double.BYTES * 3           //ExtendedData::analogInputs

                + Integer.BYTES                              //Extended data's amount of parameters

                //ExtendedData::driverKeyCode
                + Integer.BYTES + extendedData.getDriverKeyCode().getBytes(UTF_8).length
                //Extended data's first parameter
                + Integer.BYTES + extendedData.getParameters().get(0).getName().getBytes(UTF_8).length
                + Byte.BYTES
                + Integer.BYTES
                //Extended data's second parameter
                + Integer.BYTES + extendedData.getParameters().get(1).getName().getBytes(UTF_8).length
                + Byte.BYTES
                + Integer.BYTES + ((String) extendedData.getParameters().get(1).getValue()).getBytes(UTF_8).length;

        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    private AmountObjectBytesFounder<double[]> createAmountDoubleArrayBytesFounder()
            throws Exception {
        final Class<?> amountDoubleArrayBytesFounderClass = forName(CLASS_NAME_AMOUNT_DOUBLE_ARRAY_BYTES_FOUNDER);
        final Constructor<?> amountDoubleArrayBytesFounderConstructor = amountDoubleArrayBytesFounderClass
                .getDeclaredConstructor();
        amountDoubleArrayBytesFounderConstructor.setAccessible(true);
        try {
            return (AmountObjectBytesFounder<double[]>) amountDoubleArrayBytesFounderConstructor.newInstance();
        } finally {
            amountDoubleArrayBytesFounderConstructor.setAccessible(false);
        }
    }

    @SuppressWarnings("unchecked")
    private AmountObjectBytesFounder<String> createAmountStringBytesFounder()
            throws Exception {
        final Class<?> amountStringBytesFounderClass = forName(CLASS_NAME_AMOUNT_STRING_BYTES_FOUNDER);
        final Constructor<?> amountStringBytesFounderConstructor = amountStringBytesFounderClass
                .getDeclaredConstructor();
        amountStringBytesFounderConstructor.setAccessible(true);
        try {
            return (AmountObjectBytesFounder<String>) amountStringBytesFounderConstructor.newInstance();
        } finally {
            amountStringBytesFounderConstructor.setAccessible(false);
        }
    }

    @SuppressWarnings("unchecked")
    private AmountObjectBytesFounder<Parameter> createAmountParameterBytesFounder()
            throws Exception {
        final Class<?> amountParameterBytesFounderClass = forName(CLASS_NAME_AMOUNT_PARAMETER_BYTES_FOUNDER);
        final Constructor<?> amountParameterBytesFounderConstructor = amountParameterBytesFounderClass.getConstructor();
        return (AmountObjectBytesFounder<Parameter>) amountParameterBytesFounderConstructor.newInstance();
    }

    @SuppressWarnings("unchecked")
    private AmountObjectBytesFounder<List<Parameter>> createAmountParametersBytesFounder()
            throws Exception {
        final Class<?> amountParametersBytesFounderClass = forName(CLASS_NAME_AMOUNT_PARAMETERS_BYTES_FOUNDER);
        final AmountObjectBytesFounder<Parameter> amountParameterBytesFounder
                = this.createAmountParameterBytesFounder();
        final Constructor<?> amountParametersBytesFounderConstructor = amountParametersBytesFounderClass
                .getConstructor(AmountObjectBytesFounder.class);
        return (AmountObjectBytesFounder<List<Parameter>>) amountParametersBytesFounderConstructor
                .newInstance(amountParameterBytesFounder);
    }

    private AmountObjectBytesFounder<ExtendedData> createAmountExtendedDataBytesFounder()
            throws Exception {
        final Class<? extends AmountObjectBytesFounder<ExtendedData>> amountExtendedDataBytesFounderClass
                = AmountExtendedDataBytesFounder.class;
        final Constructor<? extends AmountObjectBytesFounder<ExtendedData>> amountExtendedDataBytesFounderConstructor
                = amountExtendedDataBytesFounderClass.getDeclaredConstructor(
                AmountObjectBytesFounder.class,
                AmountObjectBytesFounder.class,
                AmountObjectBytesFounder.class);
        amountExtendedDataBytesFounderConstructor.setAccessible(true);
        try {
            return amountExtendedDataBytesFounderConstructor.newInstance(
                    this.createAmountDoubleArrayBytesFounder(),
                    this.createAmountStringBytesFounder(),
                    this.createAmountParametersBytesFounder());
        } finally {
            amountExtendedDataBytesFounderConstructor.setAccessible(false);
        }
    }
}

