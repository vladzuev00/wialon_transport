package by.zuevvlad.wialontransport.dao;

import by.zuevvlad.wialontransport.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EntityRepository<EntityType extends Entity> {
    Optional<EntityType> findById(final long id);
    Collection<EntityType> findAll();
    void insert(final EntityType insertedEntity);
    void insert(final List<EntityType> insertedEntities);
    void update(final EntityType updatedEntity);
    void delete(final EntityType deletedEntity);
}
