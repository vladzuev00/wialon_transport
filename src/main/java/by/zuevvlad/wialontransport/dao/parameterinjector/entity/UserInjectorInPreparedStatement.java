package by.zuevvlad.wialontransport.dao.parameterinjector.entity;

import by.zuevvlad.wialontransport.dao.cryptographer.Cryptographer;
import by.zuevvlad.wialontransport.dao.parameterinjector.exception.InjectionParameterPreparedStatementException;
import by.zuevvlad.wialontransport.entity.User;
import by.zuevvlad.wialontransport.entity.User.Role;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class UserInjectorInPreparedStatement extends EntityInjectorInPreparedStatement<User> {
    private final int parameterIndexEmail;
    private final int parameterIndexEncryptedPassword;
    private final int parameterIndexRole;
    private final Cryptographer<String, String> cryptographer;

    public UserInjectorInPreparedStatement(final int parameterIndexEmail, final int parameterIndexEncryptedPassword,
                                           final int parameterIndexRole,
                                           final Cryptographer<String, String> cryptographer) {
        this.parameterIndexEmail = parameterIndexEmail;
        this.parameterIndexEncryptedPassword = parameterIndexEncryptedPassword;
        this.parameterIndexRole = parameterIndexRole;
        this.cryptographer = cryptographer;
    }

    public UserInjectorInPreparedStatement(final int parameterIndexId, final int parameterIndexEmail,
                                           final int parameterIndexEncryptedPassword, final int parameterIndexRole,
                                           final Cryptographer<String, String> cryptographer) {
        super(parameterIndexId);
        this.parameterIndexEmail = parameterIndexEmail;
        this.parameterIndexEncryptedPassword = parameterIndexEncryptedPassword;
        this.parameterIndexRole = parameterIndexRole;
        this.cryptographer = cryptographer;
    }

    @Override
    public void inject(final User injectedUser, final PreparedStatement preparedStatement) {
        try {
            super.inject(injectedUser, preparedStatement);
            preparedStatement.setString(this.parameterIndexEmail, injectedUser.getEmail());

            final String encryptedPassword = this.cryptographer.encrypt(injectedUser.getPassword());
            preparedStatement.setString(this.parameterIndexEncryptedPassword, encryptedPassword);

            final Role role = injectedUser.getRole();
            preparedStatement.setString(this.parameterIndexRole, role.name());
        } catch (final SQLException cause) {
            throw new InjectionParameterPreparedStatementException(cause);
        }
    }
}
