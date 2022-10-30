package by.zuevvlad.wialontransport.dao.parameterinjector.entity;

import by.zuevvlad.wialontransport.dao.parameterinjector.CompositeParameterInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.parameterinjector.DateTimeInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.parameterinjector.exception.InjectionParameterPreparedStatementException;
import by.zuevvlad.wialontransport.dao.parameterinjector.geographiccoordinate.LatitudeInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.parameterinjector.geographiccoordinate.LongitudeInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.entity.DataEntity;
import by.zuevvlad.wialontransport.entity.DataEntity.Latitude;
import by.zuevvlad.wialontransport.entity.DataEntity.Longitude;
import by.zuevvlad.wialontransport.entity.TrackerEntity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public final class DataInjectorInPreparedStatement extends EntityInjectorInPreparedStatement<DataEntity> {
    private final int parameterIndexSpeed;
    private final int parameterIndexCourse;
    private final int parameterIndexHeight;
    private final int parameterIndexAmountSatellites;
    private final int parameterIndexTrackerId;
    private final CompositeParameterInjectorInPreparedStatement<LocalDateTime> dateTimeInjector;
    private final CompositeParameterInjectorInPreparedStatement<Latitude> latitudeInjector;
    private final CompositeParameterInjectorInPreparedStatement<Longitude> longitudeInjector;

    public DataInjectorInPreparedStatement(final int parameterIndexDate,
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
                                           final int parameterIndexTrackerId) {
        this.parameterIndexSpeed = parameterIndexSpeed;
        this.parameterIndexCourse = parameterIndexCourse;
        this.parameterIndexHeight = parameterIndexHeight;
        this.parameterIndexAmountSatellites = parameterIndexAmountSatellites;
        this.parameterIndexTrackerId = parameterIndexTrackerId;
        this.dateTimeInjector = new DateTimeInjectorInPreparedStatement(parameterIndexDate, parameterIndexTime);
        this.latitudeInjector = new LatitudeInjectorInPreparedStatement(
                parameterIndexLatitudeDegrees,
                parameterIndexLatitudeMinutes,
                parameterIndexLatitudeMinuteShare,
                parameterIndexLatitudeType);
        this.longitudeInjector = new LongitudeInjectorInPreparedStatement(
                parameterIndexLongitudeDegrees,
                parameterIndexLongitudeMinutes,
                parameterIndexLongitudeMinuteShare,
                parameterIndexLongitudeType);
    }

    public DataInjectorInPreparedStatement(final int parameterIndexId,
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
                                           final int parameterIndexTrackerId) {
        super(parameterIndexId);
        this.parameterIndexSpeed = parameterIndexSpeed;
        this.parameterIndexCourse = parameterIndexCourse;
        this.parameterIndexHeight = parameterIndexHeight;
        this.parameterIndexAmountSatellites = parameterIndexAmountSatellites;
        this.parameterIndexTrackerId = parameterIndexTrackerId;
        this.dateTimeInjector = new DateTimeInjectorInPreparedStatement(parameterIndexDate, parameterIndexTime);
        this.latitudeInjector = new LatitudeInjectorInPreparedStatement(
                parameterIndexLatitudeDegrees,
                parameterIndexLatitudeMinutes,
                parameterIndexLatitudeMinuteShare,
                parameterIndexLatitudeType);
        this.longitudeInjector = new LongitudeInjectorInPreparedStatement(
                parameterIndexLongitudeDegrees,
                parameterIndexLongitudeMinutes,
                parameterIndexLongitudeMinuteShare,
                parameterIndexLongitudeType);
    }

    @Override
    public void inject(final DataEntity injectedData, final PreparedStatement preparedStatement) {
        try {
            super.inject(injectedData, preparedStatement);
            preparedStatement.setInt(this.parameterIndexSpeed, injectedData.getSpeed());
            preparedStatement.setInt(this.parameterIndexCourse, injectedData.getCourse());
            preparedStatement.setInt(this.parameterIndexHeight, injectedData.getHeight());
            preparedStatement.setInt(this.parameterIndexAmountSatellites, injectedData.getAmountSatellites());

            final TrackerEntity tracker = injectedData.getTracker();
            preparedStatement.setLong(this.parameterIndexTrackerId, tracker.getId());

            this.dateTimeInjector.inject(injectedData.getDateTime(), preparedStatement);
            this.latitudeInjector.inject(injectedData.getLatitude(), preparedStatement);
            this.longitudeInjector.inject(injectedData.getLongitude(), preparedStatement);
        } catch (final SQLException cause) {
            throw new InjectionParameterPreparedStatementException(cause);
        }
    }
}
