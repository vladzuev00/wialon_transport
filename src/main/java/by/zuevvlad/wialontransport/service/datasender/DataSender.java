package by.zuevvlad.wialontransport.service.datasender;

import by.zuevvlad.wialontransport.entity.DataEntity;
import org.apache.kafka.clients.producer.Callback;

public interface DataSender {
    void sendData(final DataEntity sentData, final Callback callback);
}
