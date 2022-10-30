package by.zuevvlad.wialontransport.service.datasender;

import by.zuevvlad.wialontransport.entity.DataEntity;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class DataSenderImplementation implements DataSender {
    private final String topicName;
    private final KafkaProducer<Long, DataEntity> dataProducer;

    public DataSenderImplementation(@Value("${kafka.data-topic-name}") final String topicName,
                                    final KafkaProducer<Long, DataEntity> dataProducer) {
        this.topicName = topicName;
        this.dataProducer = dataProducer;
    }

    @Override
    public void sendData(final DataEntity sentData, final Callback callback) {
        final ProducerRecord<Long, DataEntity> producerRecord = new ProducerRecord<>(this.topicName, sentData.getId(),
                sentData);
        this.dataProducer.send(producerRecord, callback);
    }
}
