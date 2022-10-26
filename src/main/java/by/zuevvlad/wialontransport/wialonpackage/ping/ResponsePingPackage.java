package by.zuevvlad.wialontransport.wialonpackage.ping;

import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;

public final class ResponsePingPackage implements Package {
    public static final String PACKAGE_DESCRIPTION_PREFIX = "#AP#";
    public static final String PACKAGE_DESCRIPTION_POSTFIX = "\r\n";
    private static final ToStringSerializer<ResponsePingPackage> RESPONSE_PING_PACKAGE_SERIALIZER
            = new ResponsePingPackageSerializer();

    @Override
    public String serialize() {
        return RESPONSE_PING_PACKAGE_SERIALIZER.serialize(this);
    }

    private static final class ResponsePingPackageSerializer implements ToStringSerializer<ResponsePingPackage> {
        @Override
        public String serialize(final ResponsePingPackage serializedPackage) {
            return PACKAGE_DESCRIPTION_PREFIX + PACKAGE_DESCRIPTION_POSTFIX;
        }
    }
}
