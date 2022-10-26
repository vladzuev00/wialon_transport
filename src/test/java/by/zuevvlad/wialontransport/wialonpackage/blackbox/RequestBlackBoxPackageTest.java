package by.zuevvlad.wialontransport.wialonpackage.blackbox;

import by.zuevvlad.wialontransport.builder.ParameterBuilder;
import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.entity.ExtendedDataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.entity.Data;
import by.zuevvlad.wialontransport.entity.Data.Latitude;
import by.zuevvlad.wialontransport.entity.Data.Longitude;
import by.zuevvlad.wialontransport.entity.ExtendedData;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.Data.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.Data.Latitude.Type.SOUTH;
import static by.zuevvlad.wialontransport.entity.Data.Longitude.Type.EAST;
import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType.INTEGER;
import static by.zuevvlad.wialontransport.entity.ExtendedData.Parameter.ValueType.STRING;
import static java.util.List.of;
import static org.junit.Assert.assertEquals;

public final class RequestBlackBoxPackageTest {
    private final Supplier<DataBuilder> dataBuilderSupplier;
    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<ExtendedDataBuilder> extendedDataBuilderSupplier;
    private final Supplier<ParameterBuilder> parameterBuilderSupplier;

    public RequestBlackBoxPackageTest() {
        this.dataBuilderSupplier = DataBuilder::new;
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.extendedDataBuilderSupplier = ExtendedDataBuilder::new;
        this.parameterBuilderSupplier = ParameterBuilder::new;
    }

    @Test
    public void requestBlackBoxPackageShouldBeSerialized() {
        final List<Data> data = List.of(this.createData(255), this.createData(256));
        final List<ExtendedData> extendedData = List.of(this.createExtendedData(257), this.createExtendedData(258));
        final RequestBlackBoxPackage requestBlackBoxPackage = new RequestBlackBoxPackage(data, extendedData);
        final String actual = requestBlackBoxPackage.serialize();
        final String expected = "#B#121103;113013;1011.12;S;01314.15;E;16;17;18;19"
                + "|121103;113013;1011.12;S;01314.15;E;16;17;18;19"
                + "|121103;113013;2324.25;N;02627.28;E;29;55;30;31;32.0;33;34;1.0,2.0,3.0;first driver key code;first parameter:1:36,second parameter:3:37"
                + "|121103;113013;2324.25;N;02627.28;E;29;55;30;31;32.0;33;34;1.0,2.0,3.0;first driver key code;first parameter:1:36,second parameter:3:37"
                + "\r\n";
        assertEquals(expected, actual);
    }

    private Data createData(final long id) {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();

        final LocalDateTime dateTime = LocalDateTime.of(2003, 11, 12, 11, 30, 13);
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final int latitudeDegrees = 10;
        final int latitudeMinutes = 11;
        final int latitudeMinuteShare = 12;
        final Latitude latitude = latitudeBuilder
                .catalogDegrees(latitudeDegrees)
                .catalogMinutes(latitudeMinutes)
                .catalogMinuteShare(latitudeMinuteShare)
                .catalogType(SOUTH)
                .build();

        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final int longitudeDegrees = 13;
        final int longitudeMinutes = 14;
        final int longitudeMinuteShare = 15;
        final Longitude longitude = longitudeBuilder
                .catalogDegrees(longitudeDegrees)
                .catalogMinutes(longitudeMinutes)
                .catalogMinuteShare(longitudeMinuteShare)
                .catalogType(EAST)
                .build();

        final int speed = 16;
        final int course = 17;
        final int height = 18;
        final int amountSatellites = 19;

        return dataBuilder
                .catalogId(id)
                .catalogDateTime(dateTime)
                .catalogLatitude(latitude)
                .catalogLongitude(longitude)
                .catalogSpeed(speed)
                .catalogCourse(course)
                .catalogHeight(height)
                .catalogAmountSatellites(amountSatellites)
                .build();
    }

    private ExtendedData createExtendedData(final long id) {
        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();

        return extendedDataBuilder
                .catalogData(dataBuilder
                        .catalogId(id)
                        .catalogDateTime(LocalDateTime.of(2003, 11, 12, 11, 30, 13))
                        .catalogLatitude(latitudeBuilder
                                .catalogDegrees(23)
                                .catalogMinutes(24)
                                .catalogMinuteShare(25)
                                .catalogType(NORTH)
                                .build())
                        .catalogLongitude(longitudeBuilder
                                .catalogDegrees(26)
                                .catalogMinutes(27)
                                .catalogMinuteShare(28)
                                .catalogType(EAST)
                                .build())
                        .catalogSpeed(29)
                        .catalogCourse(55)
                        .catalogHeight(30)
                        .catalogAmountSatellites(31)
                        .build())
                .catalogReductionPrecision(32.)
                .catalogInputs(33)
                .catalogOutputs(34)
                .catalogAnalogInputs(new double[]{1., 2., 3.})
                .catalogDriverKeyCode("first driver key code")
                .catalogParameters(of(
                        parameterBuilder
                                .catalogName("first parameter")
                                .catalogValueType(INTEGER)
                                .catalogValue(36)
                                .build(),
                        parameterBuilder
                                .catalogName("second parameter")
                                .catalogValueType(STRING)
                                .catalogValue("37")
                                .build()
                )).build();
    }
}
