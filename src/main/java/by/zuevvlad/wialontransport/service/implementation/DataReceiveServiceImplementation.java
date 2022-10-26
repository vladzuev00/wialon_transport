package by.zuevvlad.wialontransport.service.implementation;

import by.zuevvlad.wialontransport.entity.Data;
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
    private final KafkaConsumer<Long, Data> dataKafkaConsumer;

    public DataReceiveServiceImplementation(final KafkaConsumer<Long, Data> dataKafkaConsumer) {
        this.dataKafkaConsumer = dataKafkaConsumer;
    }

    @Override
    public List<Data> receiveData() {
        final List<Data> receivedData = new ArrayList<>();
        final ConsumerRecords<Long, Data> consumerRecords = this.dataKafkaConsumer.poll(Duration.ZERO);
        for (final ConsumerRecord<Long, Data> consumerRecord : consumerRecords) {
            receivedData.add(consumerRecord.value());
        }
        return receivedData;
    }
}
