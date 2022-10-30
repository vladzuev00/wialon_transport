package by.zuevvlad.wialontransport.dao.parameterinjector.entity;

import by.zuevvlad.wialontransport.dao.parameterinjector.exception.InjectionParameterPreparedStatementException;
import by.zuevvlad.wialontransport.dao.serializer.Serializer;
import by.zuevvlad.wialontransport.entity.DataEntity;
import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;
import by.zuevvlad.wialontransport.entity.ExtendedDataEntity.Parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public final class ExtendedDataInjectorInPreparedStatement extends EntityInjectorInPreparedStatement<ExtendedDataEntity> {
    private final Serializer<double[]> analogInputsSerializer;
    private final Serializer<List<Parameter>> parametersSerializer;
    private final EntityInjectorInPreparedStatement<DataEntity> dataInjector;
    private final int parameterIndexReductionPrecision;
    private final int parameterIndexInputs;
    private final int parameterIndexOutputs;
    private final int parameterIndexSerializedAnalogInputs;
    private final int parameterIndexDriverKeyCode;
    private final int parameterIndexParameters;

    public ExtendedDataInjectorInPreparedStatement(final Serializer<double[]> analogInputsSerializer,
                                                   final Serializer<List<Parameter>> parametersSerializer,
                                                   final int parameterIndexDate,
                                                   final int parameterIndexTime,
                                                   final int parameterIndexLatitudeDegrees,
                                                   final int parameterIndexLatitudeMinutes,
                                                   final int parameterIndexLatitudeMinuteShare,
                                                   final int parameterIndexLatitudeType,
                                                   final int parameterIndexLongitudeDegrees,
                                                   final int parameterIndexLongitudeMinutes,
                                                   final int parameterIndexLongitudeMinuteShare,
                                                   final int parameterIndexLongitudeType,
                                                   final int parameterIndexSpeed,
                                                   final int parameterIndexCourse,
                                                   final int parameterIndexHeight,
                                                   final int parameterIndexAmountSatellites,
                                                   final int parameterIndexTrackerId,
                                                   final int parameterIndexReductionPrecision,
                                                   final int parameterIndexInputs,
                                                   final int parameterIndexOutputs,
                                                   final int parameterIndexSerializedAnalogInputs,
                                                   final int parameterIndexDriverKeyCode,
                                                   final int parameterIndexParameters) {
        this.analogInputsSerializer = analogInputsSerializer;
        this.parametersSerializer = parametersSerializer;
        this.dataInjector = new DataInjectorInPreparedStatement(parameterIndexDate, parameterIndexTime,
                parameterIndexLatitudeDegrees, parameterIndexLatitudeMinutes, parameterIndexLatitudeMinuteShare,
                parameterIndexLatitudeType, parameterIndexLongitudeDegrees, parameterIndexLongitudeMinutes,
                parameterIndexLongitudeMinuteShare, parameterIndexLongitudeType, parameterIndexSpeed,
                parameterIndexCourse, parameterIndexHeight, parameterIndexAmountSatellites, parameterIndexTrackerId);
        this.parameterIndexReductionPrecision = parameterIndexReductionPrecision;
        this.parameterIndexInputs = parameterIndexInputs;
        this.parameterIndexOutputs = parameterIndexOutputs;
        this.parameterIndexSerializedAnalogInputs = parameterIndexSerializedAnalogInputs;
        this.parameterIndexDriverKeyCode = parameterIndexDriverKeyCode;
        this.parameterIndexParameters = parameterIndexParameters;
    }

    public ExtendedDataInjectorInPreparedStatement(final Serializer<double[]> analogInputsSerializer,
                                                   final Serializer<List<Parameter>> parametersSerializer,
                                                   final int parameterIndexId,
                                                   final int parameterIndexDate,
                                                   final int parameterIndexTime,
                                                   final int parameterIndexLatitudeDegrees,
                                                   final int parameterIndexLatitudeMinutes,
                                                   final int parameterIndexLatitudeMinuteShare,
                                                   final int parameterIndexLatitudeType,
                                                   final int parameterIndexLongitudeDegrees,
                                                   final int parameterIndexLongitudeMinutes,
                                                   final int parameterIndexLongitudeMinuteShare,
                                                   final int parameterIndexLongitudeType,
                                                   final int parameterIndexSpeed,
                                                   final int parameterIndexCourse,
                                                   final int parameterIndexHeight,
                                                   final int parameterIndexAmountSatellites,
                                                   final int parameterIndexTrackerId,
                                                   final int parameterIndexReductionPrecision,
                                                   final int parameterIndexInputs,
                                                   final int parameterIndexOutputs,
                                                   final int parameterIndexSerializedAnalogInputs,
                                                   final int parameterIndexDriverKeyCode,
                                                   final int parameterIndexParameters) {
        super(parameterIndexId);
        this.analogInputsSerializer = analogInputsSerializer;
        this.parametersSerializer = parametersSerializer;
        this.dataInjector = new DataInjectorInPreparedStatement(parameterIndexDate, parameterIndexTime,
                parameterIndexLatitudeDegrees, parameterIndexLatitudeMinutes, parameterIndexLatitudeMinuteShare,
                parameterIndexLatitudeType, parameterIndexLongitudeDegrees, parameterIndexLongitudeMinutes,
                parameterIndexLongitudeMinuteShare, parameterIndexLongitudeType, parameterIndexSpeed,
                parameterIndexCourse, parameterIndexHeight, parameterIndexAmountSatellites, parameterIndexTrackerId);
        this.parameterIndexReductionPrecision = parameterIndexReductionPrecision;
        this.parameterIndexInputs = parameterIndexInputs;
        this.parameterIndexOutputs = parameterIndexOutputs;
        this.parameterIndexSerializedAnalogInputs = parameterIndexSerializedAnalogInputs;
        this.parameterIndexDriverKeyCode = parameterIndexDriverKeyCode;
        this.parameterIndexParameters = parameterIndexParameters;
    }

    @Override
    public void inject(final ExtendedDataEntity injectedExtendedData, final PreparedStatement preparedStatement) {
        try {
            super.inject(injectedExtendedData, preparedStatement);
            this.dataInjector.inject(injectedExtendedData, preparedStatement);
            preparedStatement.setDouble(this.parameterIndexReductionPrecision,
                    injectedExtendedData.getReductionPrecision());
            preparedStatement.setInt(this.parameterIndexInputs, injectedExtendedData.getInputs());
            preparedStatement.setInt(this.parameterIndexOutputs, injectedExtendedData.getOutputs());

            final String serializedAnalogInputs = this.analogInputsSerializer
                    .serialize(injectedExtendedData.getAnalogInputs());
            preparedStatement.setString(this.parameterIndexSerializedAnalogInputs, serializedAnalogInputs);

            preparedStatement.setString(this.parameterIndexDriverKeyCode, injectedExtendedData.getDriverKeyCode());

            final String serializedParameters = this.parametersSerializer
                    .serialize(injectedExtendedData.getParameters());
            preparedStatement.setString(this.parameterIndexParameters, serializedParameters);
        } catch (final SQLException cause) {
            throw new InjectionParameterPreparedStatementException(cause);
        }
    }
}
