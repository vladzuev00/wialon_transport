package by.zuevvlad.wialontransport.netty.decoder;

import by.zuevvlad.wialontransport.netty.decoder.decodingchain.PackageDecoder;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static io.netty.buffer.Unpooled.wrappedBuffer;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class InboundPackageDecoderTest {

    @Mock
    private PackageDecoder mockedStarterPackageDecoder;

    private InboundPackageDecoder inboundPackageDecoder;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Before
    public void initializeRequestDecoder()
            throws Exception {
        this.inboundPackageDecoder = createRequestDecoder(this.mockedStarterPackageDecoder);
    }

    @Test
    public void singletonShouldBeLazyThreadSafe() {
        final int startedThreadAmount = 50;
        final BlockingQueue<InboundPackageDecoder> createdInboundPackageDecoders = new ArrayBlockingQueue<>(startedThreadAmount);
        rangeClosed(1, startedThreadAmount).forEach(i -> {
            final Thread startedThread = new Thread(() -> {
                try {
                    createdInboundPackageDecoders.put(InboundPackageDecoder.create());
                } catch (final InterruptedException cause) {
                    throw new RuntimeException(cause);
                }
            });
            startedThread.start();
        });
        while (createdInboundPackageDecoders.size() < startedThreadAmount) {
            Thread.yield();
        }
        final long actualAmountOfRequestDecoders = createdInboundPackageDecoders.stream().distinct().count();
        final long expectedAmountOfRequestDecoders = 1;
        assertEquals(expectedAmountOfRequestDecoders, actualAmountOfRequestDecoders);
    }

    @Test
    public void requestShouldBeDecoded() {
        final String request = "request";
        final byte[] requestBytes = request.getBytes(UTF_8);
        final ByteBuf givenByteBuf = wrappedBuffer(requestBytes);

        final Package givenPackage = mock(Package.class);
        when(this.mockedStarterPackageDecoder.decode(anyString())).thenReturn(givenPackage);

        final ChannelHandlerContext givenContext = mock(ChannelHandlerContext.class);
        final List<Object> outObjects = new ArrayList<>();

        this.inboundPackageDecoder.decode(givenContext, givenByteBuf, outObjects);

        assertEquals(1, outObjects.size());
        assertEquals(givenPackage, givenPackage);

        verify(this.mockedStarterPackageDecoder, times(1)).decode(this.stringArgumentCaptor.capture());
        assertEquals(request, this.stringArgumentCaptor.getValue());
    }

    private static InboundPackageDecoder createRequestDecoder(final PackageDecoder starterPackageDecoder)
            throws Exception {
        final Constructor<InboundPackageDecoder> requestDecoderConstructor = InboundPackageDecoder.class
                .getDeclaredConstructor(PackageDecoder.class);
        requestDecoderConstructor.setAccessible(true);
        try {
            return requestDecoderConstructor.newInstance(starterPackageDecoder);
        } finally {
            requestDecoderConstructor.setAccessible(false);
        }
    }
}
