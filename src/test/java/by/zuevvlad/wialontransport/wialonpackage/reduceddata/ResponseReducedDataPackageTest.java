package by.zuevvlad.wialontransport.wialonpackage.reduceddata;

import by.zuevvlad.wialontransport.wialonpackage.reduceddata.ResponseReducedDataPackage.Status;
import org.junit.Test;

import static by.zuevvlad.wialontransport.wialonpackage.reduceddata.ResponseReducedDataPackage.Status.*;
import static java.lang.Byte.MIN_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public final class ResponseReducedDataPackageTest {

    @Test
    public void responseReducedDataPackageShouldBeSerialized() {
        final ResponseReducedDataPackage responseReducedDataPackage = new ResponseReducedDataPackage(
                PACKAGE_FIX_SUCCESS);
        final String actual = responseReducedDataPackage.serialize();
        final String expected = "#ASD#1\r\n";
        assertEquals(expected, actual);
    }

    @Test
    public void statusShouldBeFoundByValue() {
        final Status actual = findByValue((byte) 1);
        assertSame(PACKAGE_FIX_SUCCESS, actual);
    }

    @Test
    public void statusFoundByValueShouldBeNotDefined() {
        final Status actual = findByValue(MIN_VALUE);
        assertSame(NOT_DEFINED, actual);
    }
}
