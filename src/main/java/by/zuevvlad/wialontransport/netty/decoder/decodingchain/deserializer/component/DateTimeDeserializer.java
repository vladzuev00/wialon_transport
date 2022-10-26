package by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.Deserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.component.exception.NotValidInboundSerializedDateTimeException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

public final class DateTimeDeserializer implements Deserializer<LocalDateTime> {
    private static final String SERIALIZED_NOT_DEFINED_DATE_TIME = "NA;NA";
    private static final String FORMAT_OF_DATE_TIME = "ddMMyy;HHmmss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern(FORMAT_OF_DATE_TIME);

    private DateTimeDeserializer() {

    }

    public static Deserializer<LocalDateTime> create() {
        return SingletonHolder.DATE_TIME_DESERIALIZER;
    }

    @Override
    public LocalDateTime deserialize(final String deserialized) {
        try {
            return !deserialized.equals(SERIALIZED_NOT_DEFINED_DATE_TIME)
                    ? parse(deserialized, DATE_TIME_FORMATTER)
                    : now();
        } catch (final DateTimeParseException cause) {
            throw new NotValidInboundSerializedDateTimeException(cause);
        }
    }

    private static final class SingletonHolder {
        private static final Deserializer<LocalDateTime> DATE_TIME_DESERIALIZER = new DateTimeDeserializer();
    }
}
