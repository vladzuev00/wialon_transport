package by.zuevvlad.wialontransport.dao.parameterinjector.geographiccoordinate;

import by.zuevvlad.wialontransport.entity.Data.Longitude;
import by.zuevvlad.wialontransport.entity.Data.Longitude.Type;

public final class LongitudeInjectorInPreparedStatement
        extends GeographicCoordinateInjectorInPreparedStatement<Longitude> {
    public LongitudeInjectorInPreparedStatement(final int parameterIndexDegrees, final int parameterIndexMinutes,
                                                final int parameterIndexMinuteShare, final int parameterIndexType) {
        super(parameterIndexDegrees, parameterIndexMinutes, parameterIndexMinuteShare, parameterIndexType);
    }

    @Override
    protected char findTypeValue(final Longitude injectedLongitude) {
        final Type type = injectedLongitude.getType();
        return type.getValue();
    }
}
