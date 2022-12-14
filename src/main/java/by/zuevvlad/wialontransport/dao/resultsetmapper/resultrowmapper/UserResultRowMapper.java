package by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper;

import by.zuevvlad.wialontransport.dao.cryptographer.Cryptographer;
import by.zuevvlad.wialontransport.dao.cryptographer.StringToStringCryptographer;
import by.zuevvlad.wialontransport.dao.resultsetmapper.exception.ResultSetMappingException;
import by.zuevvlad.wialontransport.entity.UserEntity;
import by.zuevvlad.wialontransport.entity.UserEntity.Role;

import java.sql.ResultSet;
import java.sql.SQLException;

import static by.zuevvlad.wialontransport.entity.UserEntity.Role.valueOf;

public final class UserResultRowMapper implements ResultRowMapper<UserEntity> {
    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_EMAIL = "email";
    private static final String COLUMN_NAME_ENCRYPTED_PASSWORD = "encrypted_password";
    private static final String COLUMN_NAME_ROLE = "role";

    private final Cryptographer<String, String> cryptographer;

    private UserResultRowMapper(final Cryptographer<String, String> cryptographer) {
        this.cryptographer = cryptographer;
    }

    public static ResultRowMapper<UserEntity> create() {
        return SingletonHolder.USER_RESULT_ROW_MAPPER;
    }

    @Override
    public UserEntity map(final ResultSet resultSet) {
        try {
            final long id = resultSet.getLong(COLUMN_NAME_ID);
            final String email = resultSet.getString(COLUMN_NAME_EMAIL);

            final String encryptedPassword = resultSet.getString(COLUMN_NAME_ENCRYPTED_PASSWORD);
            final String password = this.cryptographer.decrypt(encryptedPassword);

            final String roleName = resultSet.getString(COLUMN_NAME_ROLE);
            final Role role = valueOf(roleName);

            return new UserEntity(id, email, password, role);
        } catch (final SQLException cause) {
            throw new ResultSetMappingException(cause);
        }
    }

    private static final class SingletonHolder {
        private static final ResultRowMapper<UserEntity> USER_RESULT_ROW_MAPPER = new UserResultRowMapper(
                StringToStringCryptographer.create());
    }
}
