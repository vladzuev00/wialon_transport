package by.zuevvlad.wialontransport.dao.parameterinjector;

import java.sql.PreparedStatement;

@FunctionalInterface
public interface CompositeParameterInjectorInPreparedStatement<ParameterType> {
    void inject(final ParameterType injectedParameter, final PreparedStatement preparedStatement);
}
