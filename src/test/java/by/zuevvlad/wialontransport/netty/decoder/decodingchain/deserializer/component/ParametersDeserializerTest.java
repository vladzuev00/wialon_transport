package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.builder.ParameterBuilder;
import by.zuevvlad.wialontransport.entity.ExtendedData.Parameter;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType.*;
import static java.lang.Class.forName;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;

public final class ParametersDeserializerTest {
    private static final String CLASS_NAME_PARAMETER_DESERIALIZER
            = "by.zuevvlad.wialontransport.netty.requestdecoder.packagedecoderchain.deserializer.component.ParametersDeserializer$ParameterDeserializer";

    private final Deserializer<Parameter> parameterDeserializer;
    private final Supplier<ParameterBuilder> parameterBuilderSupplier;
    private final Deserializer<List<Parameter>> parametersDeserializer;

    public ParametersDeserializerTest()
            throws Exception {
        this.parameterDeserializer = createParameterDeserializer();
        this.parameterBuilderSupplier = ParameterBuilder::new;
        this.parametersDeserializer = ParametersDeserializer.create();
    }

    @Test
    public void integerParameterShouldBeDeserialized() {
        final String givenDeserialized = "param-name:1:654321";

        final Parameter actual = this.parameterDeserializer.deserialize(givenDeserialized);

        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final Parameter expected = parameterBuilder
                .catalogName("param-name")
                .catalogValueType(INTEGER)
                .catalogValue(654321)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void doubleParameterShouldBeDeserialized() {
        final String givenDeserialized = "param-name:2:65.4321";

        final Parameter actual = this.parameterDeserializer.deserialize(givenDeserialized);

        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final Parameter expected = parameterBuilder
                .catalogName("param-name")
                .catalogValueType(DOUBLE)
                .catalogValue(65.4321)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void stringParameterShouldBeDeserialized() {
        final String givenDeserialized = "param-name:3:param-value";

        final Parameter actual = this.parameterDeserializer.deserialize(givenDeserialized);

        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final Parameter expected = parameterBuilder
                .catalogName("param-name")
                .catalogValueType(STRING)
                .catalogValue("param-value")
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void notDefinedParameterShouldBeDeserialized() {
        final String givenDeserialized = "param-name:454543:56";

        final Parameter actual = this.parameterDeserializer.deserialize(givenDeserialized);

        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final Parameter expected = parameterBuilder
                .catalogName("param-name")
                .catalogValueType(NOT_DEFINED)
                .catalogValue(null)
                .build();

        assertEquals(expected, actual);
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<Deserializer<List<Parameter>>> createdDeserializers
                = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdDeserializers.put(ParametersDeserializer.create());
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
    public void parametersShouldBeDeserialized() {
        final String givenDeserialized = "param-name:1:654321,param-name:2:65.4321,param-name:3:param-value,"
                + "param-name:454543:56";

        final List<Parameter> actual = this.parametersDeserializer.deserialize(givenDeserialized);

        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final List<Parameter> expected = List.of(
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
                        .build(),
                parameterBuilder
                        .catalogName("param-name")
                        .catalogValueType(NOT_DEFINED)
                        .catalogValue(null)
                        .build());

        assertEquals(expected, actual);
    }

    @SuppressWarnings("unchecked")
    private static Deserializer<Parameter> createParameterDeserializer()
            throws Exception {
        final Class<?> parameterDeserializerClass = forName(CLASS_NAME_PARAMETER_DESERIALIZER);
        final Constructor<?> parameterDeserializerConstructor = parameterDeserializerClass
                .getConstructor(Supplier.class);
        final Supplier<ParameterBuilder> parameterBuilderSupplier = ParameterBuilder::new;
        return (Deserializer<Parameter>) parameterDeserializerConstructor.newInstance(parameterBuilderSupplier);
    }
}
