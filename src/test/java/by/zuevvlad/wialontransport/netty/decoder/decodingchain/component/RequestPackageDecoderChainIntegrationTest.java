package by.zuevvlad.wialontransport.netty.decoder.decodingchain.component;

import by.zuevvlad.wialontransport.builder.ParameterBuilder;
import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.entity.ExtendedDataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.entity.Data;
import by.zuevvlad.wialontransport.entity.ExtendedData;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.PackageDecoder;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.exception.NoSuitablePackageDecoderException;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.blackbox.RequestBlackBoxPackage;
import by.zuevvlad.wialontransport.wialonpackage.data.RequestDataPackage;
import by.zuevvlad.wialontransport.wialonpackage.login.RequestLoginPackage;
import by.zuevvlad.wialontransport.wialonpackage.message.RequestMessagePackage;
import by.zuevvlad.wialontransport.wialonpackage.ping.RequestPingPackage;
import by.zuevvlad.wialontransport.wialonpackage.reduceddata.RequestReducedDataPackage;
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
import static org.junit.Assert.assertTrue;

public final class RequestPackageDecoderChainIntegrationTest {
    private final PackageDecoder starterRequestPackageDecoder;
    private final Supplier<DataBuilder> dataBuilderSupplier;
    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;
    private final Supplier<ExtendedDataBuilder> extendedDataBuilderSupplier;
    private final Supplier<ParameterBuilder> parameterBuilderSupplier;

    public RequestPackageDecoderChainIntegrationTest() {
        this.starterRequestPackageDecoder = StarterInboundPackageDecoder.create();
        this.dataBuilderSupplier = DataBuilder::new;
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
        this.extendedDataBuilderSupplier = ExtendedDataBuilder::new;
        this.parameterBuilderSupplier = ParameterBuilder::new;
    }

    @Test
    public void requestLoginPackageShouldBeDecoded() {
        final String givenDecoded = "#L#imei;password\r\n";

        final Package actual = this.starterRequestPackageDecoder.decode(givenDecoded);

        final RequestLoginPackage expected = new RequestLoginPackage("imei", "password");

        assertEquals(expected, actual);
    }

    @Test
    public void requestReducedDataPackageShouldBeDecoded() {
        final String givenDecoded = "#SD#121103;113013;2324.25;N;02627.28;E;29;55;30;31\r\n";

        final Package actual = this.starterRequestPackageDecoder.decode(givenDecoded);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final Data data = dataBuilder
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
                .build();
        final RequestReducedDataPackage expected = new RequestReducedDataPackage(data);

        assertEquals(expected, actual);
    }

    @Test
    public void requestDataPackageShouldBeDecoded() {
        final String givenDecoded = "#D#121103;113013;2324.25;N;02627.28;E;29;55;30;31;32.0;33;34;1.0,2.0,3.0;"
                + "first driver key code;first parameter:1:36,second parameter:3:37\r\n";

        final Package actual = this.starterRequestPackageDecoder.decode(givenDecoded);

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();

        final ExtendedData extendedData = extendedDataBuilder
                .catalogData(dataBuilder
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
        final RequestDataPackage expected = new RequestDataPackage(extendedData);

        assertEquals(expected, actual);
    }

    @Test
    public void requestPingPackageShouldBeDecoded() {
        final String givenDecoded = "#P#\r\n";

        final Package decodedPackage = this.starterRequestPackageDecoder.decode(givenDecoded);

        assertTrue(decodedPackage instanceof RequestPingPackage);
    }

    @Test
    public void requestBlackBoxPackageShouldBeDecoded() {
        final String givenDecoded = "#B#121103;113013;1011.12;S;01314.15;E;16;17;18;19"
                + "|121103;113013;1011.12;S;01314.15;E;16;17;18;19"
                + "|121103;113013;2324.25;N;02627.28;E;29;55;30;31;32.0;33;34;1.0,2.0,3.0;first driver key code;first parameter:1:36,second parameter:3:37"
                + "|121103;113013;2324.25;N;02627.28;E;29;55;30;31;32.0;33;34;1.0,2.0,3.0;first driver key code;first parameter:1:36,second parameter:3:37"
                + "\r\n";

        final Package actual = this.starterRequestPackageDecoder.decode(givenDecoded);

        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();

        final Supplier<Data> dataSupplier = () -> dataBuilder
                .catalogDateTime(LocalDateTime.of(2003, 11, 12, 11, 30, 13))
                .catalogLatitude(latitudeBuilder
                        .catalogDegrees(10)
                        .catalogMinutes(11)
                        .catalogMinuteShare(12)
                        .catalogType(SOUTH)
                        .build())
                .catalogLongitude(longitudeBuilder
                        .catalogDegrees(13)
                        .catalogMinutes(14)
                        .catalogMinuteShare(15)
                        .catalogType(EAST)
                        .build())
                .catalogSpeed(16)
                .catalogCourse(17)
                .catalogHeight(18)
                .catalogAmountSatellites(19)
                .build();

        final ExtendedDataBuilder extendedDataBuilder = this.extendedDataBuilderSupplier.get();
        final ParameterBuilder parameterBuilder = this.parameterBuilderSupplier.get();
        final Supplier<ExtendedData> extendedDataSupplier = () -> extendedDataBuilder
                .catalogData(dataBuilder
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
                .catalogReductionPrecision(32.0)
                .catalogInputs(33)
                .catalogOutputs(34)
                .catalogAnalogInputs(new double[]{1., 2., 3.})
                .catalogDriverKeyCode("first driver key code")
                .catalogParameters(List.of(
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
                ))
                .build();

        final List<Data> data = List.of(dataSupplier.get(), dataSupplier.get());
        final List<ExtendedData> extendedData = List.of(extendedDataSupplier.get(), extendedDataSupplier.get());
        final RequestBlackBoxPackage expected = new RequestBlackBoxPackage(data, extendedData);

        assertEquals(expected, actual);
    }

    @Test
    public void requestMessagePackageShouldBeDecoded() {
        final String givenDecoded = "#M#message\r\n";

        final Package actual = this.starterRequestPackageDecoder.decode(givenDecoded);

        final RequestMessagePackage expected = new RequestMessagePackage("message");

        assertEquals(expected, actual);
    }

    @Test(expected = NoSuitablePackageDecoderException.class)
    public void requestPackageShouldNotBeDecodedBecauseOfNoSuitableDecoder() {
        final String givenDecoded = "#NOTEXIST#123\r\n";
        this.starterRequestPackageDecoder.decode(givenDecoded);
    }
}
