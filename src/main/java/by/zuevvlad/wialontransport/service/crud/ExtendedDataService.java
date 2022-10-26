package by.zuevvlad.wialontransport.service.crud;

import by.zuevvlad.wialontransport.dao.EntityRepository;
import by.zuevvlad.wialontransport.dao.ExtendedDataRepository;
import by.zuevvlad.wialontransport.entity.ExtendedData;

public final class ExtendedDataService extends AbstractCRUDEntityService<ExtendedData> {

    private ExtendedDataService(final EntityRepository<ExtendedData> extendedDataRepository) {
        super(extendedDataRepository);
    }

    public static CRUDEntityService<ExtendedData> create() {
        return SingletonHolder.CRUD_SERVICE;
    }

    private static final class SingletonHolder {
        private static final CRUDEntityService<ExtendedData> CRUD_SERVICE
                = new ExtendedDataService(ExtendedDataRepository.create());
    }
}
