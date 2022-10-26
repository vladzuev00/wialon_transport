package by.zuevvlad.wialontransport.wialonpackage.message;

import by.zuevvlad.wialontransport.wialonpackage.message.ResponseMessagePackage.Status;
import org.junit.Test;

import static by.zuevvlad.wialontransport.wialonpackage.message.ResponseMessagePackage.Status.*;
import static java.lang.Byte.MIN_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public final class ResponseMessagePackageTest {

    @Test
    public void responseMessagePackageShouldBeSerialized() {
        final ResponseMessagePackage responseMessagePackage = new ResponseMessagePackage(SUCCESS);
        final String actual = responseMessagePackage.serialize();
        final String expected = "#AM#1\r\n";
        assertEquals(expected, actual);
    }

    @Test
    public void statusShouldBeFoundByValue() {
        final byte statusValue = 1;
        final Status actual = findByValue(statusValue);
        assertSame(SUCCESS, actual);
    }

    @Test
    public void statusFoundByValueShouldBeNotDefined() {
        final Status actual = findByValue(MIN_VALUE);
        assertSame(NOT_DEFINED, actual);
    }
}
