package by.zuevvlad.wialontransport.dao.resultsetmapper;

import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.ResultRowMapper;
import by.zuevvlad.wialontransport.dao.resultsetmapper.resultrowmapper.UserResultRowMapper;
import by.zuevvlad.wialontransport.entity.User;

public final class UserResultSetMapper extends ResultSetMapper<User> {

    public static ResultSetMapper<User> create() {
        return SingletonHolder.USER_RESULT_SET_MAPPER;
    }

    private UserResultSetMapper(final ResultRowMapper<User> userResultRowMapper) {
        super(userResultRowMapper);
    }

    private static final class SingletonHolder {
        private static final ResultSetMapper<User> USER_RESULT_SET_MAPPER
                = new UserResultSetMapper(UserResultRowMapper.create());
    }
}
