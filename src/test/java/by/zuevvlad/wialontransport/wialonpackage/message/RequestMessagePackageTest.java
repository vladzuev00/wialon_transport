package by.zuevvlad.wialontransport.wialonpackage.message;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class RequestMessagePackageTest {

    @Test
    public void requestMessagePackageShouldBeSerialized() {
        final RequestMessagePackage requestMessagePackage = new RequestMessagePackage("message");
        final String actual = requestMessagePackage.serialize();
        final String expected = "#M#message\r\n";
        assertEquals(expected, actual);
    }
}
