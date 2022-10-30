package by.zuevvlad.wialontransport.builder.entity;

import by.zuevvlad.wialontransport.builder.Builder;
import by.zuevvlad.wialontransport.entity.TrackerEntity;
import by.zuevvlad.wialontransport.entity.DataEntity;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude;
import static by.zuevvlad.wialontransport.entity.DataEntity.Longitude;

import static java.time.LocalDateTime.MIN;

public final class DataBuilder implements Builder<DataEntity> {
    private static final long NOT_DEFINED_VALUE_ID = Long.MIN_VALUE;
    private static final LocalDateTime NOT_DEFINED_VALUE_DATE_TIME = MIN;
    private static final int NOT_DEFINED_VALUE_SPEED = Integer.MIN_VALUE;
    private static final int NOT_DEFINED_VALUE_COURSE = Integer.MIN_VALUE;
    private static final int NOT_DEFINED_VALUE_HEIGHT = Integer.MIN_VALUE;
    private static final int NOT_DEFINED_VALUE_AMOUNT_SATELLITES = Integer.MIN_VALUE;
    private static final Supplier<TrackerEntity> NOT_DEFINED_TRACKER_SUPPLIER = TrackerEntity::new;

    private long id;
    private LocalDateTime dateTime;
    private Latitude latitude;
    private Longitude longitude;
    private int speed;
    private int course;
    private int height;
    private int amountSatellites;
    private TrackerEntity tracker;

    public DataBuilder() {
        this.id = NOT_DEFINED_VALUE_ID;
        this.dateTime = NOT_DEFINED_VALUE_DATE_TIME;
        this.latitude = new Latitude();
        this.longitude = new Longitude();
        this.speed = NOT_DEFINED_VALUE_SPEED;
        this.course = NOT_DEFINED_VALUE_COURSE;
        this.height = NOT_DEFINED_VALUE_HEIGHT;
        this.amountSatellites = NOT_DEFINED_VALUE_AMOUNT_SATELLITES;
        this.tracker = NOT_DEFINED_TRACKER_SUPPLIER.get();
    }

    public DataBuilder catalogId(final long id) {
        this.id = id;
        return this;
    }

    public DataBuilder catalogDateTime(final LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public DataBuilder catalogLatitude(final Latitude latitude) {
        this.latitude = latitude;
        return this;
    }

    public DataBuilder catalogLongitude(final Longitude longitude) {
        this.longitude = longitude;
        return this;
    }

    public DataBuilder catalogSpeed(final int speed) {
        this.speed = speed;
        return this;
    }

    public DataBuilder catalogCourse(final int course) {
        this.course = course;
        return this;
    }

    public DataBuilder catalogHeight(final int height) {
        this.height = height;
        return this;
    }

    public DataBuilder catalogAmountSatellites(final int amountSatellites) {
        this.amountSatellites = amountSatellites;
        return this;
    }

    public DataBuilder catalogTracker(final TrackerEntity tracker) {
        this.tracker = tracker;
        return this;
    }

    @Override
    public DataEntity build() {
        return new DataEntity(this.id, this.dateTime, this.latitude, this.longitude, this.speed, this.course,
                this.height, this.amountSatellites, this.tracker);
    }
}
