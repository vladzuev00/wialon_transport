package by.zuevvlad.wialontransport.service.datasender;

import by.zuevvlad.wialontransport.entity.Data;
import org.apache.kafka.clients.producer.Callback;

public interface DataSender {
    void sendData(final Data sentData, final Callback callback);
}
