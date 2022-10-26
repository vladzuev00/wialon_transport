package by.zuevvlad.wialontransport.wialonpackage.login;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class RequestLoginPackageTest {

    @Test
    public void requestLoginPackageShouldBeSerialized() {
        final RequestLoginPackage requestLoginPackage = new RequestLoginPackage("imei", "password");
        final String actual = requestLoginPackage.serialize();
        final String expected = "#L#imei;password\r\n";
        assertEquals(expected, actual);
    }
}
