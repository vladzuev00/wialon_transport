package by.zuevvlad.wialontransport.service.implementation;

import by.zuevvlad.wialontransport.entity.DataEntity;
import by.zuevvlad.wialontransport.service.DataSendService;
import by.zuevvlad.wialontransport.service.NotDeliveredDataSendService;
import by.zuevvlad.wialontransport.service.datasender.DataSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class DataSendServiceImplementation implements DataSendService {
    private final DataSender dataSender;
    private final NotDeliveredDataSendService notDeliveredDataSendService;

    @Autowired
    public DataSendServiceImplementation(final DataSender dataSender,
                                         final NotDeliveredDataSendService notDeliveredDataSendService) {
        this.dataSender = dataSender;
        this.notDeliveredDataSendService = notDeliveredDataSendService;
    }

    @Override
    public void sendData(final DataEntity sentData) {
        this.dataSender.sendData(sentData, (metadata, exception) -> {
            if (exception != null) {
                this.notDeliveredDataSendService.addNotDeliveredData(sentData);
            }
        });
    }
}
