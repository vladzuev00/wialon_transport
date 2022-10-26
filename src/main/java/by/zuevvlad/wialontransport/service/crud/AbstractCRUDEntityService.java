package by.zuevvlad.wialontransport.service.crud;

import by.zuevvlad.wialontransport.dao.EntityRepository;
import by.zuevvlad.wialontransport.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCRUDEntityService<EntityType extends Entity, EntityRepositoryType extends EntityRepository<EntityType>>
        implements CRUDEntityService<EntityType> {
    protected final EntityRepositoryType entityRepository;

    public AbstractCRUDEntityService(final EntityRepositoryType entityRepository) {
        this.entityRepository = entityRepository;
    }

    @Override
    public final Optional<EntityType> findById(final long id) {
        return this.entityRepository.findById(id);
    }

    @Override
    public final Collection<EntityType> findAll() {
        return this.entityRepository.findAll();
    }

    @Override
    public final void save(final EntityType savedEntity) {
        this.entityRepository.insert(savedEntity);
    }

    @Override
    public final void save(final List<EntityType> savedEntities) {
        this.entityRepository.insert(savedEntities);
    }

    @Override
    public final void update(final EntityType updatedEntity) {
        this.entityRepository.update(updatedEntity);
    }

    @Override
    public final void delete(final EntityType deletedEntity) {
        this.entityRepository.delete(deletedEntity);
    }
}
