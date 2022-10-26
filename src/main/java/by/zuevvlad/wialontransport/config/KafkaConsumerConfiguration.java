package by.zuevvlad.wialontransport.config;

/*
@Configuration
public class KafkaConsumerConfiguration {

    @Bean("dataConsumer")
    @Autowired
    public KafkaConsumer<Long, Data> createDataConsumer(
            @Qualifier("dataConsumerProperties") final Properties dataConsumerProperties,
            @Value("${kafka.data-topic-name}") final String subscribedTopicRegex) {
        final KafkaConsumer<Long, Data> dataKafkaConsumer = new KafkaConsumer<>(dataConsumerProperties);
        final Pattern subscribedTopicPattern = Pattern.compile(subscribedTopicRegex);
        dataKafkaConsumer.subscribe(subscribedTopicPattern);
        return dataKafkaConsumer;
    }

    @Bean("dataConsumerProperties")
    public Properties createDataConsumerProperties(@Value("${kafka.bootstrap-servers}") final String bootstrapServers,
                                                   @Value("${kafka.consumer.consumer-group-id}") final String consumerGroupId) {
        final Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(GROUP_ID_CONFIG, consumerGroupId);
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, DataDeserializer.class);
        return properties;
    }
}
*/
