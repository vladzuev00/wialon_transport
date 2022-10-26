package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception.NotValidInboundSerializedAnalogInputsException;

import static java.util.Arrays.stream;

public final class AnalogInputsDeserializer implements Deserializer<double[]> {
    private static final String DELIMITER_ANALOG_INPUTS = ",";
    private static final String EMPTY_STRING = "";

    private AnalogInputsDeserializer() {

    }

    public static Deserializer<double[]> create() {
        return SingletonHolder.ANALOG_INPUTS_DESERIALIZER;
    }

    @Override
    public double[] deserialize(final String deserialized) {
        try {
            if (deserialized.equals(EMPTY_STRING)) {
                return new double[0];
            }
            return stream(deserialized.split(DELIMITER_ANALOG_INPUTS))
                    .mapToDouble(Double::parseDouble)
                    .toArray();
        } catch (final NumberFormatException cause) {
            throw new NotValidInboundSerializedAnalogInputsException(cause);
        }
    }

    private static final class SingletonHolder {
        private static final Deserializer<double[]> ANALOG_INPUTS_DESERIALIZER = new AnalogInputsDeserializer();
    }
}
