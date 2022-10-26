package by.zuevvlad.wialontransport.wialonpackage.blackbox;

import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;

import java.util.Objects;

import static java.lang.String.format;

public final class ResponseBlackBoxPackage implements Package {
    public static final String PACKAGE_DESCRIPTION_PREFIX = "#AB#";
    public static final String PACKAGE_DESCRIPTION_POSTFIX = "\r\n";

    private static final ToStringSerializer<ResponseBlackBoxPackage> RESPONSE_BLACK_BOX_PACKAGE_TO_STRING_SERIALIZER
            = new ResponseBlackBoxPackageToStringSerializer();

    private final int amountFixedMessages;

    public ResponseBlackBoxPackage(final int amountFixedMessages) {
        this.amountFixedMessages = amountFixedMessages;
    }

    public int getAmountFixedMessages() {
        return this.amountFixedMessages;
    }

    @Override
    public String serialize() {
        return RESPONSE_BLACK_BOX_PACKAGE_TO_STRING_SERIALIZER.serialize(this);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (this.getClass() != otherObject.getClass()) {
            return false;
        }
        final ResponseBlackBoxPackage other = (ResponseBlackBoxPackage) otherObject;
        return this.amountFixedMessages == other.amountFixedMessages;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.amountFixedMessages);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[amountFixedMessages = " + this.amountFixedMessages + "]";
    }

    private static final class ResponseBlackBoxPackageToStringSerializer
            implements ToStringSerializer<ResponseBlackBoxPackage> {
        //%d - amount of fixed messages
        private static final String SERIALIZED_RESPONSE_BLACK_BOX_PACKAGE_TEMPLATE = "#AB#%d\r\n";

        @Override
        public String serialize(final ResponseBlackBoxPackage serializedPackage) {
            return format(SERIALIZED_RESPONSE_BLACK_BOX_PACKAGE_TEMPLATE, serializedPackage.amountFixedMessages);
        }
    }
}
