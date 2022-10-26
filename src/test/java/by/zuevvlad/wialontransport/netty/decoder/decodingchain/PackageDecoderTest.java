package by.zuevvlad.wialontransport.netty.decoder.decodingchain;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.deserializer.wialonpackage.PackageDeserializer;
import by.zuevvlad.wialontransport.netty.decoder.decodingchain.exception.NoSuitablePackageDecoderException;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PackageDecoderTest {
    private static final String PACKAGE_PREFIX = "PREFIX";
    private static final String FIELD_NAME_PACKAGE_PREFIX = "packagePrefix";
    private static final String FIELD_NAME_NEXT_PACKAGE_DECODER = "nextPackageDecoder";

    @Mock
    private PackageDecoder mockedNextPackageDecoder;

    @Mock
    private PackageDeserializer mockedPackageDeserializer;

    private PackageDecoder packageDecoder;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Before
    public void initializePackageDecoder() {
        this.packageDecoder = new PackageDecoder(this.mockedNextPackageDecoder, PACKAGE_PREFIX,
                this.mockedPackageDeserializer) {
        };
    }

    @Test
    public void packageShouldBeDecodedByDecoder() {
        final String givenDecoded = "PREFIX and ...";

        final Package expected = () -> {
            throw new UnsupportedOperationException();
        };
        when(this.mockedPackageDeserializer.deserialize(anyString())).thenReturn(expected);

        final Package actual = this.packageDecoder.decode(givenDecoded);
        assertSame(expected, actual);

        verify(this.mockedPackageDeserializer, times(1)).deserialize(this.stringArgumentCaptor.capture());
        verify(this.mockedNextPackageDecoder, times(0)).decode(anyString());

        assertSame(givenDecoded, this.stringArgumentCaptor.getValue());
    }

    @Test
    public void packageShouldBeDecodedByNextHandlerBecauseOfPackagePrefixIsNull()
            throws Exception {
        this.setNullAsPackagePrefixInPackageDecoder();
        final String givenDecoded = "PREFIX and ...";

        final Package expected = () -> {
            throw new UnsupportedOperationException();
        };
        when(this.mockedNextPackageDecoder.decode(anyString())).thenReturn(expected);

        final Package actual = this.packageDecoder.decode(givenDecoded);
        assertSame(expected, actual);

        verify(this.mockedPackageDeserializer, times(0)).deserialize(anyString());
        verify(this.mockedNextPackageDecoder, times(1)).decode(this.stringArgumentCaptor.capture());

        assertSame(givenDecoded, this.stringArgumentCaptor.getValue());
    }

    @Test
    public void packageShouldBeDecodedByNextHandlerBecauseOfNotMatchingPrefix() {
        final String givenDecoded = "SECOND_PREFIX and ...";

        final Package expected = () -> {
            throw new UnsupportedOperationException();
        };
        when(this.mockedNextPackageDecoder.decode(anyString())).thenReturn(expected);

        final Package actual = this.packageDecoder.decode(givenDecoded);
        assertSame(expected, actual);

        verify(this.mockedPackageDeserializer, times(0)).deserialize(anyString());
        verify(this.mockedNextPackageDecoder, times(1)).decode(this.stringArgumentCaptor.capture());

        assertSame(givenDecoded, this.stringArgumentCaptor.getValue());
    }

    @Test(expected = NoSuitablePackageDecoderException.class)
    public void packageShouldNotBeDecodedBecauseOfNoSuitableDecoder()
            throws Exception {
        this.setNullAsNextPackageDecoderInPackageDecoder();

        final String givenDecoded = "SECOND_PREFIX and ...";

        this.packageDecoder.decode(givenDecoded);
    }

    private void setNullAsPackagePrefixInPackageDecoder()
            throws Exception {
        final Field packagePrefixField = PackageDecoder.class.getDeclaredField(FIELD_NAME_PACKAGE_PREFIX);
        packagePrefixField.setAccessible(true);
        try {
            packagePrefixField.set(this.packageDecoder, null);
        } finally {
            packagePrefixField.setAccessible(false);
        }
    }

    private void setNullAsNextPackageDecoderInPackageDecoder()
            throws Exception {
        final Field nextPackageDecoderField = PackageDecoder.class.getDeclaredField(FIELD_NAME_NEXT_PACKAGE_DECODER);
        nextPackageDecoderField.setAccessible(true);
        try {
            nextPackageDecoderField.set(this.packageDecoder, null);
        } finally {
            nextPackageDecoderField.setAccessible(false);
        }
    }
}
