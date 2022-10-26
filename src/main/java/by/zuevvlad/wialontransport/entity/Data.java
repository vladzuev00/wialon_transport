package by.zuevvlad.wialontransport.entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.Integer.MIN_VALUE;
import static java.time.LocalDateTime.of;

public class Data extends Entity {
    public static final LocalDate NOT_DEFINED_DATE = LocalDate.MIN;
    public static final LocalTime NOT_DEFINED_TIME = LocalTime.MIN;
    private static final LocalDateTime NOT_DEFINED_DATE_TIME = of(NOT_DEFINED_DATE, NOT_DEFINED_TIME);
    public static final Supplier<Latitude> NOT_DEFINED_LATITUDE_SUPPLIER = Latitude::new;
    public static final Supplier<Longitude> NOT_DEFINED_LONGITUDE_SUPPLIER = Longitude::new;
    public static final int NOT_DEFINED_SPEED = MIN_VALUE;
    public static final int NOT_DEFINED_COURSE = MIN_VALUE;
    public static final int NOT_DEFINED_HEIGHT = MIN_VALUE;
    public static final int NOT_DEFINED_AMOUNT_SATELLITES = MIN_VALUE;
    public static final Supplier<Tracker> NOT_DEFINED_TRACKER_SUPPLIER = Tracker::new;

    private LocalDateTime dateTime;
    private Latitude latitude;
    private Longitude longitude;
    private int speed;
    private int course;
    private int height;
    private int amountSatellites;
    private Tracker tracker;

    public Data() {
        this.dateTime = NOT_DEFINED_DATE_TIME;
        this.latitude = NOT_DEFINED_LATITUDE_SUPPLIER.get();
        this.longitude = NOT_DEFINED_LONGITUDE_SUPPLIER.get();
        this.speed = NOT_DEFINED_SPEED;
        this.course = NOT_DEFINED_COURSE;
        this.height = NOT_DEFINED_HEIGHT;
        this.amountSatellites = NOT_DEFINED_AMOUNT_SATELLITES;
        this.tracker = NOT_DEFINED_TRACKER_SUPPLIER.get();
    }

    public Data(final long id, final LocalDateTime dateTime, final Latitude latitude, final Longitude longitude,
                final int speed, final int course, final int height, final int amountSatellites,
                final Tracker tracker) {
        super(id);
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.course = course;
        this.height = height;
        this.amountSatellites = amountSatellites;
        this.tracker = tracker;
    }

    public Data(final Data other) {
        super(other.getId());
        this.dateTime = other.dateTime;
        this.latitude = new Latitude(other.latitude);
        this.longitude = new Longitude(other.longitude);
        this.speed = other.speed;
        this.course = other.course;
        this.height = other.height;
        this.amountSatellites = other.amountSatellites;
        this.tracker = other.tracker;
    }

    public void setDateTime(final LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    public void setLatitude(final Latitude latitude) {
        this.latitude = latitude;
    }

    public Latitude getLatitude() {
        return this.latitude;
    }

    public void setLongitude(final Longitude longitude) {
        this.longitude = longitude;
    }

    public Longitude getLongitude() {
        return this.longitude;
    }

    public void setSpeed(final int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setCourse(final int course) {
        this.course = course;
    }

    public int getCourse() {
        return this.course;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public int getHeight() {
        return this.height;
    }

    public void setAmountSatellites(final int amountSatellites) {
        this.amountSatellites = amountSatellites;
    }

    public int getAmountSatellites() {
        return this.amountSatellites;
    }

    public void setTracker(final Tracker tracker) {
        this.tracker = tracker;
    }

    public Tracker getTracker() {
        return this.tracker;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (!super.equals(otherObject)) {
            return false;
        }
        final Data other = (Data) otherObject;
        return Objects.equals(this.dateTime, other.dateTime)
                && Objects.equals(this.latitude, other.latitude)
                && Objects.equals(this.longitude, other.longitude)
                && this.speed == other.speed
                && this.course == other.course
                && this.height == other.height
                && this.amountSatellites == other.amountSatellites
                && Objects.equals(this.tracker, other.tracker);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(this.dateTime, this.latitude, this.longitude, this.speed, this.course,
                this.height, this.amountSatellites, this.tracker);
    }

    @Override
    public String toString() {
        return super.toString() + "[dateTime = " + this.dateTime + ", latitude = " + this.latitude
                + ", longitude = " + this.longitude + ", speed = " + this.speed + ", course = " + this.course
                + ", height = " + this.height + ", amountSatellites = " + this.amountSatellites
                + ", tracker = " + this.tracker + "]";
    }

    @Override
    public void writeExternal(final ObjectOutput objectOutput)
            throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeObject(this.dateTime);
        objectOutput.writeObject(this.latitude);
        objectOutput.writeObject(this.longitude);
        objectOutput.writeInt(this.speed);
        objectOutput.writeInt(this.course);
        objectOutput.writeInt(this.height);
        objectOutput.writeInt(this.amountSatellites);
        objectOutput.writeObject(this.tracker);
    }

    @Override
    public void readExternal(final ObjectInput objectInput)
            throws IOException, ClassNotFoundException {
        super.readExternal(objectInput);
        this.dateTime = (LocalDateTime) objectInput.readObject();
        this.latitude = (Latitude) objectInput.readObject();
        this.longitude = (Longitude) objectInput.readObject();
        this.speed = objectInput.readInt();
        this.course = objectInput.readInt();
        this.height = objectInput.readInt();
        this.amountSatellites = objectInput.readInt();
        this.tracker = (Tracker) objectInput.readObject();
    }

    public static abstract class GeographicCoordinate implements Externalizable {
        private static final int NOT_DEFINED_DEGREES = MIN_VALUE;
        private static final int NOT_DEFINED_MINUTES = MIN_VALUE;
        private static final int NOT_DEFINED_MINUTE_SHARE = MIN_VALUE;

        private int degrees;
        private int minutes;
        private int minuteShare;

        public GeographicCoordinate() {
            this.degrees = NOT_DEFINED_DEGREES;
            this.minutes = NOT_DEFINED_MINUTES;
            this.minuteShare = NOT_DEFINED_MINUTE_SHARE;
        }

        public GeographicCoordinate(final int degrees, final int minutes, final int minuteShare) {
            this.degrees = degrees;
            this.minutes = minutes;
            this.minuteShare = minuteShare;
        }

        public GeographicCoordinate(final GeographicCoordinate other) {
            this.degrees = other.degrees;
            this.minutes = other.minutes;
            this.minuteShare = other.minuteShare;
        }

        public final void setDegrees(final int degrees) {
            this.degrees = degrees;
        }

        public final int getDegrees() {
            return this.degrees;
        }

        public final void setMinutes(final int minutes) {
            this.minutes = minutes;
        }

        public final int getMinutes() {
            return this.minutes;
        }

        public final void setMinuteShare(final int minuteShare) {
            this.minuteShare = minuteShare;
        }

        public final int getMinuteShare() {
            return this.minuteShare;
        }

        @Override
        public boolean equals(final Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null) {
                return false;
            }
            if (this.getClass() != otherObject.getClass()) {
                return false;
            }
            final GeographicCoordinate other = (GeographicCoordinate) otherObject;
            return this.degrees == other.degrees
                    && this.minutes == other.minutes
                    && this.minuteShare == other.minuteShare;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.degrees, this.minutes, this.minuteShare);
        }

        @Override
        public String toString() {
            return this.getClass().getName() + "[degrees = " + this.degrees + ", minutes = " + this.minutes
                    + ", minuteShare = " + this.minuteShare + "]";
        }

        @Override
        public void writeExternal(final ObjectOutput objectOutput)
                throws IOException {
            objectOutput.writeInt(this.degrees);
            objectOutput.writeInt(this.minutes);
            objectOutput.writeInt(this.minuteShare);
        }

        @Override
        public void readExternal(final ObjectInput objectInput)
                throws IOException, ClassNotFoundException {
            this.degrees = objectInput.readInt();
            this.minutes = objectInput.readInt();
            this.minuteShare = objectInput.readInt();
        }
    }

    //lat (5544.6025;N)
    //градусы (2 знака) минуты (2 знака).доли минуты (количество знаков кастомное);
    //N-северная широта, S-Южная широта
    public static final class Latitude extends GeographicCoordinate {
        private Type type;

        public Latitude() {
            this.type = Type.NOT_DEFINED;
        }

        public Latitude(final int degrees, final int minutes, int minuteShare, final Type type) {
            super(degrees, minutes, minuteShare);
            this.type = type;
        }

        public Latitude(final Latitude other) {
            super(other);
            this.type = other.type;
        }

        public void setType(final Type type) {
            this.type = type;
        }

        public Type getType() {
            return this.type;
        }

        @Override
        public boolean equals(final Object otherObject) {
            if (!super.equals(otherObject)) {
                return false;
            }
            final Latitude other = (Latitude) otherObject;
            return this.type == other.type;
        }

        @Override
        public int hashCode() {
            return super.hashCode() + Objects.hashCode(this.type);
        }

        @Override
        public String toString() {
            return super.toString() + "[type = " + this.type + "]";
        }

        @Override
        public void writeExternal(final ObjectOutput objectOutput)
                throws IOException {
            super.writeExternal(objectOutput);
            objectOutput.writeObject(this.type);
        }

        @Override
        public void readExternal(final ObjectInput objectInput)
                throws IOException, ClassNotFoundException {
            super.readExternal(objectInput);
            this.type = (Type) objectInput.readObject();
        }

        public enum Type {
            NOT_DEFINED('-'), NORTH('N'), SOUTH('S');

            private final char value;

            Type(final char value) {
                this.value = value;
            }

            public final char getValue() {
                return this.value;
            }

            public static Type findByValue(final char value) {
                return Arrays.stream(Type.values())
                        .filter(type -> type.value == value)
                        .findAny()
                        .orElse(NOT_DEFINED);
            }
        }
    }

    //lon (03739.6834;E),
    //градусы (3 знака) минуты (2 знака).доли минуты (количество знаков кастомное);
    //E- восточная долгота, W-западная долгота
    public static final class Longitude extends GeographicCoordinate {
        private Type type;

        public Longitude() {
            this.type = Type.NOT_DEFINED;
        }

        public Longitude(final int degrees, final int minutes, final int minuteShare,
                         final Type type) {
            super(degrees, minutes, minuteShare);
            this.type = type;
        }

        public Longitude(final Longitude other) {
            super(other);
            this.type = other.type;
        }

        public void setType(final Type type) {
            this.type = type;
        }

        public Type getType() {
            return this.type;
        }

        @Override
        public boolean equals(final Object otherObject) {
            if (!super.equals(otherObject)) {
                return false;
            }
            final Longitude other = (Longitude) otherObject;
            return this.type == other.type;
        }

        @Override
        public int hashCode() {
            return super.hashCode() + Objects.hashCode(this.type);
        }

        @Override
        public String toString() {
            return super.toString() + "[type = " + this.type + "]";
        }

        @Override
        public void writeExternal(final ObjectOutput objectOutput)
                throws IOException {
            super.writeExternal(objectOutput);
            objectOutput.writeObject(this.type);
        }

        @Override
        public void readExternal(final ObjectInput objectInput)
                throws IOException, ClassNotFoundException {
            super.readExternal(objectInput);
            this.type = (Longitude.Type) objectInput.readObject();
        }

        public enum Type {
            NOT_DEFINED('-'), EAST('E'), WESTERN('W');

            private final char value;

            Type(final char value) {
                this.value = value;
            }

            public final char getValue() {
                return this.value;
            }

            public static Type findByValue(final char value) {
                return Arrays.stream(Type.values())
                        .filter(type -> type.value == value)
                        .findAny()
                        .orElse(NOT_DEFINED);
            }
        }
    }
}
