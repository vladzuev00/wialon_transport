package by.zuevvlad.wialontransport.wialonpackage.data;

import by.zuevvlad.wialontransport.wialonpackage.data.ResponseDataPackage.Status;
import org.junit.Test;

import static by.zuevvlad.wialontransport.wialonpackage.data.ResponseDataPackage.Status.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static java.lang.Byte.MIN_VALUE;

public final class ResponseDataPackageTest {

    @Test
    public void responseDataPackageShouldBeSerialized() {
        final ResponseDataPackage responseDataPackage = new ResponseDataPackage(PACKAGE_FIX_SUCCESS);
        final String actual = responseDataPackage.serialize();
        final String expected = "#AD#1\r\n";
        assertEquals(expected, actual);
    }

    @Test
    public void responseDataPackageStatusShouldBeFoundByValue() {
        final byte value = 1;
        final Status actual = findByValue(value);
        assertSame(PACKAGE_FIX_SUCCESS, actual);
    }

    @Test
    public void responseDataPackageStatusShouldBeNotDefinedAfterFindingByNotExistingValue() {
        final Status actual = findByValue(MIN_VALUE);
        assertSame(NOT_DEFINED, actual);
    }
}
