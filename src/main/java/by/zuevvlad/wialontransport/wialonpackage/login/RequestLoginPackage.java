package by.zuevvlad.wialontransport.wialonpackage.login;

import by.zuevvlad.wialontransport.netty.tostringserializer.ToStringSerializer;
import by.zuevvlad.wialontransport.wialonpackage.Package;

import java.util.Objects;

import static java.lang.String.format;

public final class RequestLoginPackage implements Package {
    public static final String PACKAGE_DESCRIPTION_PREFIX = "#L#";
    public static final String PACKAGE_DESCRIPTION_POSTFIX = "\r\n";
    public static final String DELIMITER_IMEI_AND_PASSWORD = ";";

    private static final ToStringSerializer<RequestLoginPackage> REQUEST_LOGIN_PACKAGE_TO_STRING_SERIALIZER
            = new RequestLoginPackageToStringSerializer();

    private final String imei;
    private final String password;

    public RequestLoginPackage(final String imei, final String password) {
        this.imei = imei;
        this.password = password;
    }

    public String getImei() {
        return this.imei;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public String serialize() {
        return REQUEST_LOGIN_PACKAGE_TO_STRING_SERIALIZER.serialize(this);
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
        final RequestLoginPackage other = (RequestLoginPackage) otherObject;
        return Objects.equals(this.imei, other.imei)
                && Objects.equals(this.password, other.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.imei, this.password);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[imei = " + this.imei + ", password = " + this.password + "]";
    }

    private static final class RequestLoginPackageToStringSerializer
            implements ToStringSerializer<RequestLoginPackage> {
        /*
             First %s - imei
             Second %s - password
         */
        private static final String SERIALIZED_REQUEST_LOGIN_PACKAGE_TEMPLATE = "#L#%s;%s\r\n";

        @Override
        public String serialize(final RequestLoginPackage serializedPackage) {
            return format(SERIALIZED_REQUEST_LOGIN_PACKAGE_TEMPLATE, serializedPackage.getImei(),
                    serializedPackage.getPassword());
        }
    }
}
