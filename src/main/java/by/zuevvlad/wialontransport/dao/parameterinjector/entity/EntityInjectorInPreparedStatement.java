package by.zuevvlad.wialontransport.dao.parameterinjector.entity;

import by.zuevvlad.wialontransport.dao.parameterinjector.CompositeParameterInjectorInPreparedStatement;
import by.zuevvlad.wialontransport.dao.parameterinjector.exception.InjectionParameterPreparedStatementException;
import by.zuevvlad.wialontransport.entity.Entity;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.lang.Integer.MIN_VALUE;


public abstract class EntityInjectorInPreparedStatement<EntityType extends Entity>
        implements CompositeParameterInjectorInPreparedStatement<EntityType> {
    private static final int NOT_DEFINED_PARAMETER_INDEX_ID = MIN_VALUE;

    private final int parameterIndexId;

    public EntityInjectorInPreparedStatement() {
        this.parameterIndexId = NOT_DEFINED_PARAMETER_INDEX_ID;
    }

    public EntityInjectorInPreparedStatement(final int parameterIndexId) {
        this.parameterIndexId = parameterIndexId;
    }

    @Override
    public void inject(final EntityType injectedEntity, final PreparedStatement preparedStatement) {
        try {
            //inserting prepared statements doesn't have id
            if (this.parameterIndexId != NOT_DEFINED_PARAMETER_INDEX_ID) {
                preparedStatement.setLong(this.parameterIndexId, injectedEntity.getId());
            }
        } catch (final SQLException cause) {
            throw new InjectionParameterPreparedStatementException(cause);
        }
    }
}
