package by.zuevvlad.wialontransport.dao;

import by.zuevvlad.wialontransport.dao.cryptographer.StringToStringCryptographer;
import by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPool;
import by.zuevvlad.wialontransport.dao.dbconnectionpool.DataBaseConnectionPoolImplementation;
import by.zuevvlad.wialontransport.dao.foundergeneratedid.FounderGeneratedId;
import by.zuevvlad.wialontransport.dao.foundergeneratedid.FounderGeneratedLongId;
import by.zuevvlad.wialontransport.dao.parameterinjector.entity.EntityInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.parameterinjector.entity.UserInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.resultsetmapper.ResultSetMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.UserResultSetMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.UserResultRowMapper;
import by.zuevvlad.wialontransport.entity.User;

import java.util.logging.Logger;

public final class UserRepository extends AbstractEntityRepository<User> {
    private static final String QUERY_TO_SELECT_BY_ID = "SELECT users.id, users.email, users.encrypted_password, "
            + "users.role FROM users WHERE users.id = ?";

    private static final String QUERY_TO_SELECT_ALL = "SELECT users.id, users.email, users.encrypted_password, "
            + "users.role FROM users";

    private static final String QUERY_TO_INSERT = "INSERT INTO users(email, encrypted_password, role) VALUES(?, ?, ?)";
    private static final int PARAMETER_INDEX_EMAIL_IN_QUERY_TO_INSERT = 1;
    private static final int PARAMETER_INDEX_ENCRYPTED_PASSWORD_IN_QUERY_TO_INSERT = 2;
    private static final int PARAMETER_INDEX_ROLE_IN_QUERY_TO_INSERT = 3;

    private static final String QUERY_TO_UPDATE = "UPDATE users SET email = ?, encrypted_password = ?, role = ? "
            + "WHERE id = ?";
    private static final int PARAMETER_INDEX_EMAIL_IN_QUERY_TO_UPDATE = 1;
    private static final int PARAMETER_INDEX_ENCRYPTED_PASSWORD_IN_QUERY_TO_UPDATE = 2;
    private static final int PARAMETER_INDEX_ROLE_IN_QUERY_TO_UPDATE = 3;
    private static final int PARAMETER_INDEX_ID_IN_QUERY_TO_UPDATE = 4;

    private static final String QUERY_TO_DELETE_BY_ID = "DELETE FROM users WHERE users.id = ?";

    private UserRepository(final DataBaseConnectionPool dataBaseConnectionPool,
                           final ResultRowMapper<User> userResultRowMapper,
                           final ResultSetMapper<User> userResultSetMapper,
                           final FounderGeneratedId<Long> founderGeneratedId,
                           final EntityInjectorInPreparedStatement<User> entityInjectorToInsert,
                           final EntityInjectorInPreparedStatement<User> entityInjectorToUpdate,
                           final Logger logger) {
        super(dataBaseConnectionPool, userResultRowMapper, userResultSetMapper, founderGeneratedId,
                entityInjectorToInsert, entityInjectorToUpdate, logger);
    }

    public static EntityRepository<User> create() {
        return SingletonHolder.USER_REPOSITORY;
    }

    @Override
    protected String findQueryToSelectById() {
        return QUERY_TO_SELECT_BY_ID;
    }

    @Override
    protected String findQueryToSelectAll() {
        return QUERY_TO_SELECT_ALL;
    }

    @Override
    protected String findQueryToInsertEntity() {
        return QUERY_TO_INSERT;
    }

    @Override
    protected String findQueryToUpdateEntity() {
        return QUERY_TO_UPDATE;
    }

    @Override
    protected String findQueryToDeleteEntityById() {
        return QUERY_TO_DELETE_BY_ID;
    }

    private static final class SingletonHolder {
        private static final EntityRepository<User> USER_REPOSITORY = new UserRepository(
                DataBaseConnectionPoolImplementation.create(),
                UserResultRowMapper.create(),
                UserResultSetMapper.create(),
                FounderGeneratedLongId.create(),
                new UserInjectorInPreparedStatement(
                        PARAMETER_INDEX_EMAIL_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_ENCRYPTED_PASSWORD_IN_QUERY_TO_INSERT,
                        PARAMETER_INDEX_ROLE_IN_QUERY_TO_INSERT,
                        StringToStringCryptographer.create()
                ),
                new UserInjectorInPreparedStatement(
                        PARAMETER_INDEX_ID_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_EMAIL_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_ENCRYPTED_PASSWORD_IN_QUERY_TO_UPDATE,
                        PARAMETER_INDEX_ROLE_IN_QUERY_TO_UPDATE,
                        StringToStringCryptographer.create()
                ),
                Logger.getLogger(UserRepository.class.getName()));
    }
}
