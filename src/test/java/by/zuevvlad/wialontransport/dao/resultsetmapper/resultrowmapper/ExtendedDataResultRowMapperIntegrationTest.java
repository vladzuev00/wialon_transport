package by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper;

import by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter;

import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter.ValueType.*;
import static java.lang.Class.forName;
import static org.junit.Assert.assertEquals;

//public final class ExtendedDataResultRowMapperIntegrationTest {
//    private static final String CLASS_NAME_PARAMETER_DESERIALIZER
//            = "by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ExtendedDataResultRowMapper"
//            + "$FromStringParametersDeserializer$FromStringParameterDeserializer";
//    private static final String CLASS_NAME_PARAMETERS_DESERIALIZER
//            = "by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ExtendedDataResultRowMapper"
//            + "$FromStringParametersDeserializer";
//
//    @Test
//    public void integerParameterShouldBeDeserialized()
//            throws Exception {
//        final FromStringDeserializer<Parameter> fromStringParameterDeserializer = this.createParameterDeserializer();
//        final String deserialized = "parameter:1:643435";
//        final Parameter actual = fromStringParameterDeserializer.deserialize(deserialized);
//        final Parameter expected = new Parameter("parameter", INTEGER, 643435);
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void doubleParameterShouldBeDeserialized()
//            throws Exception {
//        final FromStringDeserializer<Parameter> fromStringParameterDeserializer = this.createParameterDeserializer();
//        final String deserialized = "parameter:2:563434.34453";
//        final Parameter actual = fromStringParameterDeserializer.deserialize(deserialized);
//        final Parameter expected = new Parameter("parameter", DOUBLE, 563434.34453);
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void stringParameterShouldBeDeserialized()
//            throws Exception {
//        final FromStringDeserializer<Parameter> fromStringParameterDeserializer = this.createParameterDeserializer();
//        final String deserialized = "parameter:3:parameter:value";
//        final Parameter actual = fromStringParameterDeserializer.deserialize(deserialized);
//        final Parameter expected = new Parameter("parameter", STRING, "parameter:value");
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void notDefinedParameterShouldBeDeserialized()
//            throws Exception {
//        final FromStringDeserializer<Parameter> fromStringParameterDeserializer = this.createParameterDeserializer();
//        final String deserialized = "parameter:4:parameter:value";
//        final Parameter actual = fromStringParameterDeserializer.deserialize(deserialized);
//        final Parameter expected = new Parameter("parameter", NOT_DEFINED, null);
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void parametersShouldBeDeserialized()
//            throws Exception {
//        final FromStringDeserializer<List<Parameter>> fromStringParametersDeserializer
//                = this.createParametersDeserializer();
//        final String deserialized = "parameter:1:643435,     "
//                + "parameter:2:563434.34453,parameter:3:parameter:value, parameter:4:parameter:value";
//        final List<Parameter> actual = fromStringParametersDeserializer.deserialize(deserialized);
//        final List<Parameter> expected = List.of(
//                new Parameter("parameter", INTEGER, 643435),
//                new Parameter("parameter", DOUBLE, 563434.34453),
//                new Parameter("parameter", STRING, "parameter:value"),
//                new Parameter("parameter", NOT_DEFINED, null));
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void emptyParametersShouldBeDeserialized()
//            throws Exception {
//        final FromStringDeserializer<List<Parameter>> fromStringParametersDeserializer
//                = this.createParametersDeserializer();
//        final String deserialized = "";
//        final List<Parameter> actual = fromStringParametersDeserializer.deserialize(deserialized);
//        final List<Parameter> expected = emptyList();
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void singletonShouldBeLazyThreadSafe() {
//        final int startedThreadAmount = 50;
//        final BlockingQueue<ResultRowMapper<ExtendedData>> createdRowMappers
//                = new ArrayBlockingQueue<>(startedThreadAmount);
//        rangeClosed(1, startedThreadAmount).forEach(i -> {
//            final Thread startedThread = new Thread(() -> {
//                try {
//                    createdRowMappers.put(ExtendedDataResultRowMapper.create());
//                } catch (InterruptedException cause) {
//                    throw new RuntimeException(cause);
//                }
//            });
//            startedThread.start();
//        });
//        while (createdRowMappers.size() < startedThreadAmount) {
//            Thread.yield();
//        }
//        final long actualAmountOfCreatedRowMappers = createdRowMappers.stream().distinct().count();
//        final long expectedAmountOfCreatedRowMappers = 1;
//        assertEquals(expectedAmountOfCreatedRowMappers, actualAmountOfCreatedRowMappers);
//    }
//
//
//
//    @SuppressWarnings("unchecked")
//    private FromStringDeserializer<Parameter> createParameterDeserializer()
//            throws Exception {
//        final Class<?> parameterDeserializerClass = forName(CLASS_NAME_PARAMETER_DESERIALIZER);
//        final Constructor<?> parameterDeserializerConstructor = parameterDeserializerClass.getConstructor();
//        return (FromStringDeserializer<Parameter>) parameterDeserializerConstructor.newInstance();
//    }
//
//    @SuppressWarnings("unchecked")
//    private FromStringDeserializer<List<Parameter>> createParametersDeserializer()
//            throws Exception {
//        final Class<?> parametersDeserializerClass = forName(CLASS_NAME_PARAMETERS_DESERIALIZER);
//        final Constructor<?> parametersDeserializerConstructor = parametersDeserializerClass.getConstructor();
//        return (FromStringDeserializer<List<Parameter>>) parametersDeserializerConstructor.newInstance();
//    }
//}
