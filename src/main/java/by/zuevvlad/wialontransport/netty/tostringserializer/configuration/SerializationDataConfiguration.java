package by.zuevvlad.wialontransport.netty.tostringserializer.configuration;

//TODO: перести в проперти файл
public enum SerializationDataConfiguration {
    DATE_FORMAT("ddMMyy"), TIME_FORMAT("HHmmss"), SERIALIZED_NOT_DEFINED_VALUE("NA"),
    SERIALIZED_NOT_DEFINED_GEOGRAPHIC_COORDINATE("NA;NA"), COMPONENTS_DELIMITER(";");

    private final String value;

    SerializationDataConfiguration(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
