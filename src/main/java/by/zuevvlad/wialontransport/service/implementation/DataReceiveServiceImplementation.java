package by.zuevvlad.wialontransport.service.implementation;

import by.zuevvlad.wialontransport.entity.DataEntity;
import by.zuevvlad.wialontransport.service.DataReceiveService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public final class DataReceiveServiceImplementation implements DataReceiveService {
    private final KafkaConsumer<Long, DataEntity> dataKafkaConsumer;

    public DataReceiveServiceImplementation(final KafkaConsumer<Long, DataEntity> dataKafkaConsumer) {
        this.dataKafkaConsumer = dataKafkaConsumer;
    }

    @Override
    public List<DataEntity> receiveData() {
        final List<DataEntity> receivedData = new ArrayList<>();
        final ConsumerRecords<Long, DataEntity> consumerRecords = this.dataKafkaConsumer.poll(Duration.ZERO);
        for (final ConsumerRecord<Long, DataEntity> consumerRecord : consumerRecords) {
            receivedData.add(consumerRecord.value());
        }
        return receivedData;
    }
}
