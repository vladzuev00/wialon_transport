package by.zuevvlad.wialontransport.dao.resultsetmapper;

import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.UserResultRowMapper;
import by.zuevvlad.wialontransport.entity.UserEntity;

public final class UserResultSetMapper extends ResultSetMapper<UserEntity> {

    public static ResultSetMapper<UserEntity> create() {
        return SingletonHolder.USER_RESULT_SET_MAPPER;
    }

    private UserResultSetMapper(final ResultRowMapper<UserEntity> userResultRowMapper) {
        super(userResultRowMapper);
    }

    private static final class SingletonHolder {
        private static final ResultSetMapper<UserEntity> USER_RESULT_SET_MAPPER
                = new UserResultSetMapper(UserResultRowMapper.create());
    }
}
