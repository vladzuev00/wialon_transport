package by.zuevvlad.wialontransport.wialonpackage.reduceddata;

import by.zuevvlad.wialontransport.builder.entity.DataBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LatitudeBuilder;
import by.zuevvlad.wialontransport.builder.geographiccoordinate.LongitudeBuilder;
import by.zuevvlad.wialontransport.entity.DataEntity;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import static by.zuevvlad.wialontransport.entity.DataEntity.Latitude.Type.NORTH;
import static by.zuevvlad.wialontransport.entity.DataEntity.Longitude.Type.EAST;
import static org.junit.Assert.assertEquals;

public final class RequestReducedDataPackageTest {
    private final Supplier<DataBuilder> dataBuilderSupplier;
    private final Supplier<LatitudeBuilder> latitudeBuilderSupplier;
    private final Supplier<LongitudeBuilder> longitudeBuilderSupplier;

    public RequestReducedDataPackageTest() {
        this.dataBuilderSupplier = DataBuilder::new;
        this.latitudeBuilderSupplier = LatitudeBuilder::new;
        this.longitudeBuilderSupplier = LongitudeBuilder::new;
    }

    @Test
    public void requestReducedDataPackageShouldBeSerialized() {
        final DataEntity data = this.createData();
        final RequestReducedDataPackage requestReducedDataPackage = new RequestReducedDataPackage(data);
        final String actual = requestReducedDataPackage.serialize();
        final String expected = "#SD#121103;113013;2324.25;N;02627.28;E;29;55;30;31\r\n";
        assertEquals(expected, actual);
    }

    private DataEntity createData() {
        final DataBuilder dataBuilder = this.dataBuilderSupplier.get();
        final LatitudeBuilder latitudeBuilder = this.latitudeBuilderSupplier.get();
        final LongitudeBuilder longitudeBuilder = this.longitudeBuilderSupplier.get();
        return dataBuilder
                .catalogId(255)
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
    }
}
