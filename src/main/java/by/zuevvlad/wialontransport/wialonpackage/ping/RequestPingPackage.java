package by.zuevvlad.wialontransport.wialonpackage.ping;

import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;

public final class RequestPingPackage implements Package {
    public static final String PACKAGE_DESCRIPTION_PREFIX = "#P#";
    public static final String PACKAGE_DESCRIPTION_POSTFIX = "\r\n";
    private static final ToStringSerializer<RequestPingPackage> REQUEST_PING_PACKAGE_SERIALIZER
            = new RequestPingPackageSerializer();

    @Override
    public String serialize() {
        return REQUEST_PING_PACKAGE_SERIALIZER.serialize(this);
    }

    private static final class RequestPingPackageSerializer implements ToStringSerializer<RequestPingPackage> {
        @Override
        public String serialize(final RequestPingPackage serializedPackage) {
            return PACKAGE_DESCRIPTION_PREFIX + PACKAGE_DESCRIPTION_POSTFIX;
        }
    }
}
