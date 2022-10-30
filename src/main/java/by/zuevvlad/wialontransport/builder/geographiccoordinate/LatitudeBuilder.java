package by.zuevvlad.wialontransport.builder.geographiccoordinate;

import by.zuevvlad.wialontransport.builder.Builder;

import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude;
import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type;
import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type.NOT_DEFINED;
import static java.lang.Integer.MIN_VALUE;

public final class LatitudeBuilder implements Builder<Latitude> {
    private static final int NOT_DEFINED_VALUE_DEGREES = MIN_VALUE;
    private static final int NOT_DEFINED_VALUE_MINUTES = MIN_VALUE;
    private static final int NOT_DEFINE_VALUE_MINUTE_SHARE = MIN_VALUE;

    private int degrees;
    private int minutes;
    private int minuteShare;
    private Type type;

    public LatitudeBuilder() {
        this.degrees = NOT_DEFINED_VALUE_DEGREES;
        this.minutes = NOT_DEFINED_VALUE_MINUTES;
        this.minuteShare = NOT_DEFINE_VALUE_MINUTE_SHARE;
        this.type = NOT_DEFINED;
    }

    public LatitudeBuilder catalogDegrees(final int degrees) {
        this.degrees = degrees;
        return this;
    }

    public LatitudeBuilder catalogMinutes(final int minutes) {
        this.minutes = minutes;
        return this;
    }

    public LatitudeBuilder catalogMinuteShare(final int minuteShare) {
        this.minuteShare = minuteShare;
        return this;
    }

    public LatitudeBuilder catalogType(final Type type) {
        this.type = type;
        return this;
    }

    @Override
    public Latitude build() {
        return new Latitude(this.degrees, this.minutes, this.minuteShare, this.type);
    }
}
