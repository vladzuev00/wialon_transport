package by.zuevvlad.wialontransport.wialonpackage.login;

import by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage.Status;
import org.junit.Test;

import static by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage.Status.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ResponseLoginPackageTest {

    @Test
    public void responseLoginPackageShouldBeSerialized() {
        final ResponseLoginPackage responseLoginPackage = new ResponseLoginPackage(SUCCESS_AUTHORIZATION);
        final String actual = responseLoginPackage.serialize();
        final String expected = "#AL#1\r\n";
        assertEquals(expected, actual);
    }

    @Test
    public void responseLoginPackageStatusShouldBeFoundByValue() {
        final String value = "1";
        final Status actual = findByValue(value);
        assertSame(SUCCESS_AUTHORIZATION, actual);
    }

    @Test
    public void responseLoginPackageStatusShouldBeNotDefinedAfterFindingByValue() {
        final String value = "";
        final Status actual = findByValue(value);
        assertSame(NOT_DEFINED, actual);
    }
}
