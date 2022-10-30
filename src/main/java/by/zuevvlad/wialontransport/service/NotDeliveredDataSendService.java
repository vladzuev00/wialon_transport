package by.zuevvlad.wialontransport.service;

import by.zuevvlad.wialontransport.entity.DataEntity;

public interface NotDeliveredDataSendService {
    void addNotDeliveredData(final DataEntity addedNotDeliveredData);
    void sendNotDeliveredData();
}
