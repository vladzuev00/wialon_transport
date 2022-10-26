package by.zuevvlad.wialontransport.service.crud;

import by.zuevvlad.wialontransport.dao.DataRepository;
import by.zuevvlad.wialontransport.dao.EntityRepository;
import by.zuevvlad.wialontransport.entity.Data;

public final class DataService extends AbstractCRUDEntityService<Data> {
    private DataService(final EntityRepository<Data> dataRepository) {
        super(dataRepository);
    }

    public static DataService create() {
        return SingletonHolder.DATA_SERVICE;
    }

    private static final class SingletonHolder {
        private static final DataService DATA_SERVICE = new DataService(DataRepository.create());
    }
}
