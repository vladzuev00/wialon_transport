package by.zuevvlad.wialontransport.dao.serializer;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public final class AnalogInputsSerializer implements Serializer<double[]> {
    private static final String DELIMITER_ANALOG_INPUTS = ",";

    public static Serializer<double[]> create() {
        return SingletonHolder.ANALOG_INPUTS_SERIALIZER;
    }

    private AnalogInputsSerializer() {

    }

    @Override
    public String serialize(final double[] analogInputs) {
        return stream(analogInputs)
                .mapToObj(Double::toString)
                .collect(joining(DELIMITER_ANALOG_INPUTS));
    }

    private static final class SingletonHolder {
        private static final Serializer<double[]> ANALOG_INPUTS_SERIALIZER = new AnalogInputsSerializer();
    }
}
