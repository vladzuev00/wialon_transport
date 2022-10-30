package by.zuevvlad.wialontransport.dao.parameterinjector.geographiccoordinate;

import by.zuevvlad.wialontransport.entity.DataEntity.Latitude;
import by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type;

public final class LatitudeInjectorInPreparedStatement
        extends GeographicCoordinateInjectorInPreparedStatement<Latitude> {

    public LatitudeInjectorInPreparedStatement(final int parameterIndexDegrees, final int parameterIndexMinutes,
                                               final int parameterIndexMinuteShare, final int parameterIndexType) {
        super(parameterIndexDegrees, parameterIndexMinutes, parameterIndexMinuteShare, parameterIndexType);
    }

    @Override
    protected char findTypeValue(final Latitude injectedLatitude) {
        final Type type = injectedLatitude.getType();
        return type.getValue();
    }
}
