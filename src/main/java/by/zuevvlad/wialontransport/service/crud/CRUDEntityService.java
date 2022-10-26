package by.zuevvlad.wialontransport.service.crud;

import by.zuevvlad.wialontransport.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CRUDEntityService<EntityType extends Entity> {
    Optional<EntityType> findById(final long id);
    Collection<EntityType> findAll();
    void save(final EntityType savedEntity);
    void save(final List<EntityType> savedEntities);
    void update(final EntityType updatedEntity);
    void delete(final EntityType deletedEntity);
}
