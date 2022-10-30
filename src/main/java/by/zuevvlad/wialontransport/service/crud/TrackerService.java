package by.zuevvlad.wialontransport.service.crud;

import by.zuevvlad.wialontransport.dao.TrackerRepository;
import by.zuevvlad.wialontransport.entity.TrackerEntity;

import java.util.Optional;

public final class TrackerService extends AbstractCRUDEntityService<TrackerEntity, TrackerRepository> {

    private TrackerService(TrackerRepository trackerRepository) {
        super(trackerRepository);
    }

    public final Optional<TrackerEntity> findByImei(final String imei) {
        return super.entityRepository.findByImei(imei);
    }
}
