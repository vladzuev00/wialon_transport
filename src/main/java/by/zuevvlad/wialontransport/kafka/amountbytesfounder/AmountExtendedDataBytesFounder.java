package by.zuevvlad.wialontransport.kafka.amountbytesfounder;

import by.zuevvlad.wialontransport.kafka.amountbytesfounder.exception.ImpossibleToFindAmountObjectBytesException;
import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

import static java.nio.charset.StandardCharsets.UTF_8;
import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter;
import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter.ValueType;
import static by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter.ValueType.*;
import static java.util.Arrays.stream;

public final class AmountExtendedDataBytesFounder implements AmountObjectBytesFounder<ExtendedDataEntity> {
    private final AmountObjectBytesFounder<double[]> amountDoubleArrayBytesFounder;
    private final AmountObjectBytesFounder<String> amountDriverKeyCodeBytesFounder;
    private final AmountObjectBytesFounder<List<Parameter>> amountParametersBytesFounder;

    public static AmountObjectBytesFounder<ExtendedDataEntity> create() {
        return SingletonHolder.AMOUNT_EXTENDED_DATA_BYTES_FOUNDER;
    }

    private AmountExtendedDataBytesFounder(final AmountObjectBytesFounder<double[]> amountDoubleArrayBytesFounder,
                                           final AmountObjectBytesFounder<String> amountDriverKeyCodeBytesFounder,
                                           final AmountObjectBytesFounder<List<Parameter>> amountParametersBytesFounder) {
        this.amountDoubleArrayBytesFounder = amountDoubleArrayBytesFounder;
        this.amountDriverKeyCodeBytesFounder = amountDriverKeyCodeBytesFounder;
        this.amountParametersBytesFounder = amountParametersBytesFounder;
    }

    @Override
    public int find(final ExtendedDataEntity extendedData) {
        final int analogInputsAmountOfBytes = this.amountDoubleArrayBytesFounder.find(extendedData.getAnalogInputs());
        final int driverKeyCodeAmountOfBytes = this.amountDriverKeyCodeBytesFounder
                .find(extendedData.getDriverKeyCode());
        final int parametersAmountOfBytes = this.amountParametersBytesFounder.find(extendedData.getParameters());
        return AmountDataBytesFounder.AMOUNT_OF_BYTES          //DATA::ALL_FIELD
                + Double.BYTES                                 //ExtendedData::reductionPrecision
                + Integer.BYTES                                //ExtendedData::inputs
                + Integer.BYTES                                //ExtendedData::outputs
                + analogInputsAmountOfBytes
                + driverKeyCodeAmountOfBytes
                + parametersAmountOfBytes;
    }

    private static final class AmountDoubleArrayBytesFounder implements AmountObjectBytesFounder<double[]> {

        @Override
        public int find(final double[] array) {
            //array's size + array's elements
            return Integer.BYTES + stream(array)
                    .mapToInt(arrayElement -> Double.BYTES)
                    .sum();
        }
    }

    private static final class AmountStringBytesFounder implements AmountObjectBytesFounder<String> {

        @Override
        public int find(final String string) {
            final byte[] stringBytes = string.getBytes(UTF_8);
            return Integer.BYTES + stringBytes.length;  //length of array of bytes and array of bytes
        }
    }

    private static final class AmountParametersBytesFounder implements AmountObjectBytesFounder<List<Parameter>> {
        private static final int AMOUNT_EMPTY_LIST_PARAMETERS_BYTES = 0;

        private final AmountObjectBytesFounder<Parameter> amountParameterBytesFounder;

        public AmountParametersBytesFounder(final AmountObjectBytesFounder<Parameter> amountParameterBytesFounder) {
            this.amountParameterBytesFounder = amountParameterBytesFounder;
        }

        @Override
        public int find(final List<Parameter> parameters) {
            final int sumAmountParametersBytes = parameters.stream()
                    .map(this.amountParameterBytesFounder::find)
                    .reduce(AMOUNT_EMPTY_LIST_PARAMETERS_BYTES, Integer::sum);
            return Integer.BYTES                  //amount of parameters
                    + sumAmountParametersBytes;
        }

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private static final class AmountParameterBytesFounder implements AmountObjectBytesFounder<Parameter> {

            private final Map<ValueType, ToIntFunction<Parameter>> mapValueTypeToFunctionParameterToAmountOfBytesOfValue;

            public AmountParameterBytesFounder() {
                this.mapValueTypeToFunctionParameterToAmountOfBytesOfValue = new EnumMap<>(ValueType.class) {
                    {
                        super.put(NOT_DEFINED, notDefinedParameter -> {
                            throw new ImpossibleToFindAmountObjectBytesException("Impossible to find amount of bytes "
                                    + "of parameter with not defined type.");
                        });
                        super.put(INTEGER, integerParameter -> Integer.BYTES);
                        super.put(DOUBLE, doubleParameter -> Double.BYTES);
                        super.put(STRING, stringParameter -> {
                            final AmountObjectBytesFounder<String> amountStringBytesFounder
                                    = new AmountStringBytesFounder();
                            final String value = (String) stringParameter.getValue();
                            return amountStringBytesFounder.find(value);
                        });
                    }
                };
            }

            @Override
            public int find(final Parameter parameter) {
                final String name = parameter.getName();
                final byte[] nameBytes = name.getBytes(UTF_8);

                final ToIntFunction<Parameter> functionParameterToAmountOfBytesOfValue
                        = this.mapValueTypeToFunctionParameterToAmountOfBytesOfValue.get(parameter.getValueType());

                return Integer.BYTES + nameBytes.length                                    //Length of arrays of name's bytes and name's bytes
                        + Byte.BYTES                                                       //type's value
                        + functionParameterToAmountOfBytesOfValue.applyAsInt(parameter);   //value's bytes
            }
        }
    }

    private static final class SingletonHolder {
        private static final AmountObjectBytesFounder<ExtendedDataEntity> AMOUNT_EXTENDED_DATA_BYTES_FOUNDER
                = new AmountExtendedDataBytesFounder(
                new AmountDoubleArrayBytesFounder(),
                new AmountStringBytesFounder(),
                new AmountParametersBytesFounder(new AmountParametersBytesFounder.AmountParameterBytesFounder()));
    }
}
