package by.zuevvlad.wialontransport.wialonpackage.blackbox;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class ResponseBlackBoxPackageTest {

    @Test
    public void responseBlackBoxPackageShouldBeSerialized() {
        final ResponseBlackBoxPackage responseBlackBoxPackage = new ResponseBlackBoxPackage(10);
        final String actual = responseBlackBoxPackage.serialize();
        final String expected = "#AB#10\r\n";
        assertEquals(expected, actual);
    }
}
