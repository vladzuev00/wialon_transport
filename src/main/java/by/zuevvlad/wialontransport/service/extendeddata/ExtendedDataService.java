package by.zuevvlad.wialontransport.service.extendeddata;

import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;
import by.zuevvlad.wialontransport.wialonpackage.reduceddata.ResponseReducedDataPackage.Status;

public interface ExtendedDataService {
    Status save(final ExtendedDataEntity savedData);
}
