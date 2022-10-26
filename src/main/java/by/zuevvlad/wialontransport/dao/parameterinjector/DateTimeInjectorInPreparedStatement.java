package by.zuevvlad.wialontransport.dao.parameterinjector;

import by.zuevvlad.wialontransport.dao.parameterinjector.exception.InjectionParameterPreparedStatementException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class DateTimeInjectorInPreparedStatement
        implements CompositeParameterInjectorInPreparedStatement<LocalDateTime> {
    private final int parameterIndexDate;
    private final int parameterIndexTime;

    public DateTimeInjectorInPreparedStatement(final int parameterIndexDate, final int parameterIndexTime) {
        this.parameterIndexDate = parameterIndexDate;
        this.parameterIndexTime = parameterIndexTime;
    }

    @Override
    public void inject(final LocalDateTime injectedDateTime, final PreparedStatement preparedStatement) {
        try {
            final LocalDate date = injectedDateTime.toLocalDate();
            preparedStatement.setDate(this.parameterIndexDate, Date.valueOf(date));

            final LocalTime time = injectedDateTime.toLocalTime();
            preparedStatement.setTime(this.parameterIndexTime, Time.valueOf(time));
        } catch (final SQLException cause) {
            throw new InjectionParameterPreparedStatementException(cause);
        }
    }
}
