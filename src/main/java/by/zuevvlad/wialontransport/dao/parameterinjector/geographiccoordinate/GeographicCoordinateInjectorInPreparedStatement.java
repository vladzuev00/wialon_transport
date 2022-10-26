package by.zuevvlad.wialontransport.dao.parameterinjector.geographiccoordinate;

import by.zuevvlad.wialontransport.dao.parameterinjector.CompositeParameterInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.parameterinjector.exception.InjectionParameterPreparedStatementException;
import by.zuevvlad.wialontransport.entity.Data.GeographicCoordinate;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class GeographicCoordinateInjectorInPreparedStatement<GeographicCoordinateType extends GeographicCoordinate>
        implements CompositeParameterInjectorInPreparedStatement<GeographicCoordinateType> {
    private final int parameterIndexDegrees;
    private final int parameterIndexMinutes;
    private final int parameterIndexMinuteShare;
    private final int parameterIndexType;

    public GeographicCoordinateInjectorInPreparedStatement(final int parameterIndexDegrees,
                                                           final int parameterIndexMinutes,
                                                           final int parameterIndexMinuteShare,
                                                           final int parameterIndexType) {
        this.parameterIndexDegrees = parameterIndexDegrees;
        this.parameterIndexMinutes = parameterIndexMinutes;
        this.parameterIndexMinuteShare = parameterIndexMinuteShare;
        this.parameterIndexType = parameterIndexType;
    }

    @Override
    public final void inject(final GeographicCoordinateType injectedGeographicCoordinate,
                             final PreparedStatement preparedStatement) {
        try {
            preparedStatement.setInt(this.parameterIndexDegrees, injectedGeographicCoordinate.getDegrees());
            preparedStatement.setInt(this.parameterIndexMinutes, injectedGeographicCoordinate.getMinutes());
            preparedStatement.setInt(this.parameterIndexMinuteShare, injectedGeographicCoordinate.getMinuteShare());

            final char typeValue = this.findTypeValue(injectedGeographicCoordinate);
            preparedStatement.setString(this.parameterIndexType, Character.toString(typeValue));
        } catch (final SQLException cause) {
            throw new InjectionParameterPreparedStatementException(cause);
        }
    }

    protected abstract char findTypeValue(final GeographicCoordinateType injectedGeographicCoordinate);
}
