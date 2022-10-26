package by.zuevvlad.wialontransport.wialonpackage.ping;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class ResponsePingPackageTest {
    @Test
    public void responsePingPackageShouldBeSerialized() {
        final ResponsePingPackage responsePingPackage = new ResponsePingPackage();
        final String actual = responsePingPackage.serialize();
        final String expected = "#AP#\r\n";
        assertEquals(expected, actual);
    }
}
