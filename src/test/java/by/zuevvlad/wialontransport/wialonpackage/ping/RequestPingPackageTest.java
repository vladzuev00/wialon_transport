package by.zuevvlad.wialontransport.wialonpackage.ping;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public final class RequestPingPackageTest {
    @Test
    public void requestPingPackageShouldBeSerialized() {
        final RequestPingPackage requestPingPackage = new RequestPingPackage();
        final String actual = requestPingPackage.serialize();
        final String expected = "#P#\r\n";
        assertEquals(expected, actual);
    }
}
