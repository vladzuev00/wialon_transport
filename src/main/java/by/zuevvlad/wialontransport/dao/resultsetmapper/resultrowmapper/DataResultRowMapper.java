package by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.dao.EntityRepository;
import by.zuevvlad.wialontransport.dao.TrackerRepository;
import by.zuevvlad.wialontransport.dao.resultsetmapper.exception.ResultSetMappingException;
import by.zuevvlad.wialontransport.entity.TrackerEntity;
import by.zuevvlad.wialontransport.entity.DataEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude;
import static by.zuevvlad.wialontransport.entity.DataEntity.Longitude;
import static java.lang.String.format;
import static java.time.LocalDateTime.of;

public final class DataResultRowMapper implements ResultRowMapper<DataEntity> {
    private static final String TEMPLATE_EXCEPTION_DESCRIPTION_NO_TRACKER = "No tracker with id '%d'";

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_SPEED = "speed";
    private static final String COLUMN_NAME_COURSE = "course";
    private static final String COLUMN_NAME_HEIGHT = "height";
    private static final String COLUMN_NAME_AMOUNT_SATELLITES = "amount_of_satellites";
    private static final String COLUMN_NAME_TRACKER_ID = "tracker_id";

    private final ResultRowMapper<LocalDateTime> dateTimeResultRowMapper;
    private final ResultRowMapper<Latitude> latitudeResultRowMapper;
    private final ResultRowMapper<Longitude> longitudeResultRowMapper;
    private final EntityRepository<TrackerEntity> trackerRepository;
    private final Supplier<DataBuilder> dataBuilderSupplier;

    public static ResultRowMapper<DataEntity> create() {
        return SingletonHolder.DATA_RESULT_ROW_MAPPER;
    }

    private DataResultRowMapper(final ResultRowMapper<LocalDateTime> dateTimeResultRowMapper,
                                final ResultRowMapper<Latitude> latitudeResultRowMapper,
                                final ResultRowMapper<Longitude> longitudeResultRowMapper,
                                final EntityRepository<TrackerEntity> trackerRepository,
                                final Supplier<DataBuilder> dataBuilderSupplier) {
        this.dateTimeResultRowMapper = dateTimeResultRowMapper;
        this.latitudeResultRowMapper = latitudeResultRowMapper;
        this.longitudeResultRowMapper = longitudeResultRowMapper;
        this.trackerRepository = trackerRepository;
        this.dataBuilderSupplier = dataBuilderSupplier;
    }

    @Override
    public DataEntity map(final ResultSet resultSet) {
        try {
            final long id = resultSet.getLong(COLUMN_NAME_ID);
            final LocalDateTime dateTime = this.dateTimeResultRowMapper.map(resultSet);
            final Latitude latitude = this.latitudeResultRowMapper.map(resultSet);
            final Longitude longitude = this.longitudeResultRowMapper.map(resultSet);
            final int speed = resultSet.getInt(COLUMN_NAME_SPEED);
            final int course = resultSet.getInt(COLUMN_NAME_COURSE);
            final int height = resultSet.getInt(COLUMN_NAME_HEIGHT);
            final int amountSatellites = resultSet.getInt(COLUMN_NAME_AMOUNT_SATELLITES);

            final long trackerId = resultSet.getLong(COLUMN_NAME_TRACKER_ID);
            final Optional<TrackerEntity> optionalTracker = this.trackerRepository.findById(trackerId);
            final TrackerEntity tracker = optionalTracker
                    .orElseThrow(() -> new ResultSetMappingException(
                            format(TEMPLATE_EXCEPTION_DESCRIPTION_NO_TRACKER, trackerId)));

            return this.dataBuilderSupplier.get()
                    .catalogId(id)
                    .catalogDateTime(dateTime)
                    .catalogLatitude(latitude)
                    .catalogLongitude(longitude)
                    .catalogSpeed(speed)
                    .catalogCourse(course)
                    .catalogHeight(height)
                    .catalogAmountSatellites(amountSatellites)
                    .catalogTracker(tracker)
                    .build();
        } catch (final SQLException cause) {
            throw new ResultSetMappingException(cause);
        }
    }

    private static final class DateTimeResultRowMapper implements ResultRowMapper<LocalDateTime> {
        private static final String COLUMN_NAME_DATE = "date";
        private static final String COLUMN_NAME_TIME = "time";

        @Override
        public LocalDateTime map(final ResultSet resultSet) {
            try {
                final Timestamp timestampDate = resultSet.getTimestamp(COLUMN_NAME_DATE);
                final LocalDate date = timestampDate.toLocalDateTime().toLocalDate();

                final Timestamp timestampTime = resultSet.getTimestamp(COLUMN_NAME_TIME);
                final LocalTime time = timestampTime.toLocalDateTime().toLocalTime();

                return of(date, time);
            } catch (final SQLException cause) {
                throw new ResultSetMappingException(cause);
            }
        }
    }

    private static final class LatitudeResultRowMapper implements ResultRowMapper<Latitude> {
        private static final String COLUMN_NAME_DEGREES = "latitude_degrees";
        private static final String COLUMN_NAME_MINUTES = "latitude_minutes";
        private static final String COLUMN_NAME_MINUTE_SHARE = "latitude_minute_share";
        private static final String COLUMN_NAME_TYPE = "latitude_type";

        private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;

        public LatitudeResultRowMapper(final Supplier<LatitudeBuilder> latitudeBuilderSupplier) {
            this.latitudeBuilderSupplier = latitudeBuilderSupplier;
        }

        @Override
        public Latitude map(final ResultSet resultSet) {
            try {
                final int degrees = resultSet.getInt(COLUMN_NAME_DEGREES);
                final int minutes = resultSet.getInt(COLUMN_NAME_MINUTES);
                final int minuteShare = resultSet.getInt(COLUMN_NAME_MINUTE_SHARE);

                final char typeDescription = resultSet.getString(COLUMN_NAME_TYPE).charAt(0);
                final Latitude.Type type = Latitude.Type.findByValue(typeDescription);

                return this.latitudeBuilderSupplier.get()
                        .catalogDegrees(degrees)
                        .catalogMinutes(minutes)
                        .catalogMinuteShare(minuteShare)
                        .catalogType(type)
                        .build();
            } catch (final SQLException cause) {
                throw new ResultSetMappingException(cause);
            }
        }
    }

    private static final class LongitudeResultRowMapper implements ResultRowMapper<Longitude> {
        private static final String COLUMN_NAME_DEGREES = "longitude_degrees";
        private static final String COLUMN_NAME_MINUTES = "longitude_minutes";
        private static final String COLUMN_NAME_MINUTE_SHARE = "longitude_minute_share";
        private static final String COLUMN_NAME_TYPE = "longitude_type";

        private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;

        public LongitudeResultRowMapper(final Supplier<LongitudeBuilder> longitudeBuilderSupplier) {
            this.longitudeBuilderSupplier = longitudeBuilderSupplier;
        }

        @Override
        public Longitude map(final ResultSet resultSet) {
            try {
                final int degrees = resultSet.getInt(COLUMN_NAME_DEGREES);
                final int minutes = resultSet.getInt(COLUMN_NAME_MINUTES);
                final int minuteShare = resultSet.getInt(COLUMN_NAME_MINUTE_SHARE);

                final char typeDescription = resultSet.getString(COLUMN_NAME_TYPE).charAt(0);
                final Longitude.Type type = Longitude.Type.findByValue(typeDescription);

                return this.longitudeBuilderSupplier.get()
                        .catalogDegrees(degrees)
                        .catalogMinutes(minutes)
                        .catalogMinuteShare(minuteShare)
                        .catalogType(type)
                        .build();
            } catch (final SQLException cause) {
                throw new ResultSetMappingException(cause);
            }
        }
    }

    private static final class SingletonHolder {
        public static final ResultRowMapper<DataEntity> DATA_RESULT_ROW_MAPPER = new DataResultRowMapper(
                new DateTimeResultRowMapper(),
                new LatitudeResultRowMapper(LatitudeBuilder::new),
                new LongitudeResultRowMapper(LongitudeBuilder::new),
                TrackerRepository.create(),
                DataBuilder::new);
    }
}
