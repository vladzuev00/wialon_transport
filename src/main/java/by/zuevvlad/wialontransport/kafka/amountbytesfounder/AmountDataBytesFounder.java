package by.zuevvlad.wialontransport.kafka.amountbytesfounder;

import by.zuevvlad.wialontransport.entity.Data;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class AmountDataBytesFounder implements AmountObjectBytesFounder<Data> {
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static final byte[] DATE_TIME_PATTERN_BYTES = DATE_TIME_PATTERN.getBytes(UTF_8);
    static final int AMOUNT_OF_BYTES =
            Long.BYTES                                            //Entity::id
                    + DATE_TIME_PATTERN_BYTES.length              //Data::dateTime
                    + Integer.BYTES                               //Data::Latitude::degrees
                    + Integer.BYTES                               //Data::Latitude::minutes
                    + Integer.BYTES                               //Data::Latitude::minuteShare
                    + Character.BYTES                             //Data::Latitude::Type::value
                    + Integer.BYTES                               //Data::Longitude::degrees
                    + Integer.BYTES                               //Data::Longitude::minutes
                    + Integer.BYTES                               //Data::Longitude::minuteShare
                    + Character.BYTES                             //Data::Longitude::Type::value
                    + Integer.BYTES                               //Data::speed
                    + Integer.BYTES                               //Data::course
                    + Integer.BYTES                               //Data::height
                    + Integer.BYTES;                              //Data::amountOfSatellites

    public static AmountObjectBytesFounder<Data> create() {
        return SingletonHolder.AMOUNT_DATA_BYTES_FOUNDER;
    }

    private AmountDataBytesFounder() {

    }

    @Override
    public int find(final Data data) {
        return AMOUNT_OF_BYTES;  //is fixed
    }

    private static final class SingletonHolder {
        private static final AmountObjectBytesFounder<Data> AMOUNT_DATA_BYTES_FOUNDER = new AmountDataBytesFounder();
    }
}
