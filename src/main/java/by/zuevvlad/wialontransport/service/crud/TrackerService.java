package by.zuevvlad.wialontransport.service.crud;

import by.zuevvlad.wialontransport.dao.TrackerRepository;
import by.zuevvlad.wialontransport.entity.Tracker;

import java.util.Optional;

public final class TrackerService extends AbstractCRUDEntityService<Tracker, TrackerRepository> {

    private TrackerService(TrackerRepository trackerRepository) {
        super(trackerRepository);
    }

    public final Optional<Tracker> findByImei(final String imei) {
        return super.entityRepository.findByImei(imei);
    }
}
