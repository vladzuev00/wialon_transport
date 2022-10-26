package by.zuevvlad.wialontransport.config;

/*
@Configuration
public class KafkaProducerConfiguration {

    @Bean("dataProducer")
    @Autowired
    public KafkaProducer<Long, Data> createDataProducer(
            @Qualifier("dataProducerProperties") final Properties properties) {
        return new KafkaProducer<>(properties);
    }

    @Bean("dataProducerProperties")
    public Properties createDataProducerProperties(@Value("${kafka.bootstrap-servers}") final String bootstrapServers,
                                                   @Value("${kafka.producer.delivery.timeout.ms}") final int deliveryTimeoutMs) {
        final Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, DataSerializer.class);
        //An upper bound on the time to report success or failure after a call to send() returns.
        properties.put(DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs);
        return properties;
    }
}
*/
