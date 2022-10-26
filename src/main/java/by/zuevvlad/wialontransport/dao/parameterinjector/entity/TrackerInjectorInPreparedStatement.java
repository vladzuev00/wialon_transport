package by.zuevvlad.wialontransport.dao.parameterinjector.entity;

import by.zuevvlad.wialontransport.dao.cryptographer.Cryptographer;
import by.zuevvlad.wialontransport.dao.parameterinjector.exception.InjectionParameterPreparedStatementException;
import by.zuevvlad.wialontransport.entity.Tracker;
import by.zuevvlad.wialontransport.entity.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class TrackerInjectorInPreparedStatement extends EntityInjectorInPreparedStatement<Tracker> {
    private final int parameterIndexImei;
    private final int parameterIndexEncryptedPassword;
    private final int parameterIndexPhoneNumber;
    private final int parameterIndexUserId;
    private final Cryptographer<String, String> cryptographer;

    public TrackerInjectorInPreparedStatement(final int parameterIndexImei, final int parameterIndexEncryptedPassword,
                                              final int parameterIndexPhoneNumber, final int parameterIndexUserId,
                                              final Cryptographer<String, String> cryptographer) {
        this.parameterIndexImei = parameterIndexImei;
        this.parameterIndexEncryptedPassword = parameterIndexEncryptedPassword;
        this.parameterIndexPhoneNumber = parameterIndexPhoneNumber;
        this.parameterIndexUserId = parameterIndexUserId;
        this.cryptographer = cryptographer;
    }

    public TrackerInjectorInPreparedStatement(final int parameterIndexId, final int parameterIndexImei,
                                              final int parameterIndexEncryptedPassword,
                                              final int parameterIndexPhoneNumber, final int parameterIndexUserId,
                                              final Cryptographer<String, String> cryptographer) {
        super(parameterIndexId);
        this.parameterIndexImei = parameterIndexImei;
        this.parameterIndexEncryptedPassword = parameterIndexEncryptedPassword;
        this.parameterIndexPhoneNumber = parameterIndexPhoneNumber;
        this.parameterIndexUserId = parameterIndexUserId;
        this.cryptographer = cryptographer;
    }

    @Override
    public void inject(final Tracker injectedTracker, final PreparedStatement preparedStatement) {
        try {
            super.inject(injectedTracker, preparedStatement);
            preparedStatement.setString(this.parameterIndexImei, injectedTracker.getImei());

            final String encryptedPassword = this.cryptographer.encrypt(injectedTracker.getPassword());
            preparedStatement.setString(this.parameterIndexEncryptedPassword, encryptedPassword);

            preparedStatement.setString(this.parameterIndexPhoneNumber, injectedTracker.getPassword());

            final User user = injectedTracker.getUser();
            preparedStatement.setLong(this.parameterIndexUserId, user.getId());
        } catch (final SQLException cause) {
            throw new InjectionParameterPreparedStatementException(cause);
        }
    }
}
