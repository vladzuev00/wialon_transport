package by.zuevvlad.wialontransport.service;

import by.zuevvlad.wialontransport.entity.Data;

public interface NotDeliveredDataSendService {
    void addNotDeliveredData(final Data addedNotDeliveredData);
    void sendNotDeliveredData();
}
