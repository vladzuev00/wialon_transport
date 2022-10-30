package by.zuevvlad.wialontransport.kafka.serializer;

import by.zuevvlad.wialontransport.entity.DataEntity;
import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;
import by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter;
import by.zuevvlad.wialontransport.kafka.amountbytesfounder.AmountObjectBytesFounder;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.ByteBuffer;
import java.util.List;

import static java.nio.ByteBuffer.allocate;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.stream;

public final class ExtendedDataSerializer implements Serializer<ExtendedDataEntity> {
    private final AmountObjectBytesFounder<ExtendedDataEntity> extendedDataAmountObjectBytesFounder;
    private final Serializer<DataEntity> dataSerializer;

    private ExtendedDataSerializer(final AmountObjectBytesFounder<ExtendedDataEntity> extendedDataAmountObjectBytesFounder,
                                   final Serializer<DataEntity> dataSerializer) {
        this.extendedDataAmountObjectBytesFounder = extendedDataAmountObjectBytesFounder;
        this.dataSerializer = dataSerializer;
    }

    @Override
    public byte[] serialize(final String topic, final ExtendedDataEntity serializedExtendedData) {
        final int amountOfBytes = this.extendedDataAmountObjectBytesFounder.find(serializedExtendedData);
        final ByteBuffer byteBuffer = allocate(amountOfBytes);

        this.writeAsData(byteBuffer, serializedExtendedData);
        byteBuffer.putDouble(serializedExtendedData.getReductionPrecision());
        byteBuffer.putInt(serializedExtendedData.getInputs());
        byteBuffer.putInt(serializedExtendedData.getOutputs());
        writeAnalogInputs(byteBuffer, serializedExtendedData);
        writeDriverKeyCode(byteBuffer, serializedExtendedData);

        return null;
    }

    private void writeAsData(final ByteBuffer byteBuffer, final ExtendedDataEntity serializedExtendedData) {
        final byte[] serializedAsData = this.dataSerializer.serialize(null, serializedExtendedData);
        byteBuffer.put(serializedAsData);
    }

    private static void writeAnalogInputs(final ByteBuffer byteBuffer, final ExtendedDataEntity serializedExtendedData) {
        final double[] serializedAnalogInputs = serializedExtendedData.getAnalogInputs();
        byteBuffer.putInt(serializedAnalogInputs.length);
        stream(serializedAnalogInputs).forEach(byteBuffer::putDouble);
    }

    private static void writeDriverKeyCode(final ByteBuffer byteBuffer, final ExtendedDataEntity serializedExtendedData) {
        final String driverKeyCode = serializedExtendedData.getDriverKeyCode();
        final byte[] driverKeyCodeBytes = driverKeyCode.getBytes(UTF_8);
        byteBuffer.putInt(driverKeyCodeBytes.length);
        byteBuffer.put(driverKeyCodeBytes);
    }

    private static void writeParameters(final ByteBuffer byteBuffer, final ExtendedDataEntity serializedExtendedData) {
        final List<Parameter> parameters = serializedExtendedData.getParameters();
        byteBuffer.putInt(parameters.size());

    }
}
