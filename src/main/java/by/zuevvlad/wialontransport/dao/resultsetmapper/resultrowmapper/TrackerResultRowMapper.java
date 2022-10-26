package by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper;

import by.zuevvlad.wialontransport.dao.EntityRepository;
import by.zuevvlad.wialontransport.dao.UserRepository;
import by.zuevvlad.wialontransport.dao.cryptographer.Cryptographer;
import by.zuevvlad.wialontransport.dao.cryptographer.StringToStringCryptographer;
import by.zuevvlad.wialontransport.dao.resultsetmapper.exception.ResultSetMappingException;
import by.zuevvlad.wialontransport.entity.Tracker;
import by.zuevvlad.wialontransport.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static java.lang.String.format;

public final class TrackerResultRowMapper implements ResultRowMapper<Tracker> {
    private static final String TEMPLATE_EXCEPTION_DESCRIPTION_NO_USER = "No user with id = '%d'.";

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_IMEI = "imei";
    private static final String COLUMN_NAME_ENCRYPTED_PASSWORD = "encrypted_password";
    private static final String COLUMN_NAME_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_NAME_USER_ID = "user_id";

    private final Cryptographer<String, String> cryptographer;
    private final EntityRepository<User> userRepository;

    private TrackerResultRowMapper(final Cryptographer<String, String> cryptographer,
                                   final EntityRepository<User> userRepository) {
        this.cryptographer = cryptographer;
        this.userRepository = userRepository;
    }

    public static ResultRowMapper<Tracker> create() {
        return SingletonHolder.TRACKER_RESULT_ROW_MAPPER;
    }

    @Override
    public Tracker map(final ResultSet resultSet) {
        try {
            final long id = resultSet.getLong(COLUMN_NAME_ID);
            final String imei = resultSet.getString(COLUMN_NAME_IMEI);

            final String encryptedPassword = resultSet.getString(COLUMN_NAME_ENCRYPTED_PASSWORD);
            final String password = this.cryptographer.decrypt(encryptedPassword);

            final String phoneNumber = resultSet.getString(COLUMN_NAME_PHONE_NUMBER);

            final long userId = resultSet.getLong(COLUMN_NAME_USER_ID);
            final Optional<User> optionalUser = this.userRepository.findById(userId);
            final User user = optionalUser
                    .orElseThrow(() -> new ResultSetMappingException(
                            format(TEMPLATE_EXCEPTION_DESCRIPTION_NO_USER, userId)));

            return new Tracker(id, imei, password, phoneNumber, user);
        } catch (final SQLException cause) {
            throw new ResultSetMappingException(cause);
        }
    }

    private static final class SingletonHolder {
        private static final ResultRowMapper<Tracker> TRACKER_RESULT_ROW_MAPPER = new TrackerResultRowMapper(
                StringToStringCryptographer.create(), UserRepository.create());
    }
}
