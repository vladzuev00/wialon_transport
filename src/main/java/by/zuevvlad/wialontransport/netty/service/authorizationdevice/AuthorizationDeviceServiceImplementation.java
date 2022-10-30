package by.zuevvlad.wialontransport.netty.service.authorizationdevice;

import by.zuevvlad.wialontransport.dao.TrackerRepository;
import by.zuevvlad.wialontransport.entity.TrackerEntity;
import by.zuevvlad.wialontransport.wialonpackage.login.RequestLoginPackage;
import by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage.Status;

import java.util.Optional;

import static by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage.Status.*;

public final class AuthorizationDeviceServiceImplementation implements AuthorizationDeviceService {
    private final TrackerRepository deviceRepository;

    private AuthorizationDeviceServiceImplementation(final TrackerRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public static AuthorizationDeviceService create() {
        return SingletonHolder.AUTHORIZATION_DEVICE_SERVICE;
    }

    @Override
    public Status authorize(final RequestLoginPackage loginPackage) {
        final Optional<TrackerEntity> optionalDevice = this.deviceRepository.findByImei(loginPackage.getImei());
        return optionalDevice
                .map(device -> checkPassword(device, loginPackage.getPassword()))
                .orElse(CONNECTION_FAILURE);
    }

    private static Status checkPassword(final TrackerEntity tracker, final String packagePassword) {
        final String devicePassword = tracker.getPassword();
        return packagePassword.equals(devicePassword) ? SUCCESS_AUTHORIZATION : ERROR_CHECK_PASSWORD;
    }

    private static final class SingletonHolder {
        private static final AuthorizationDeviceService AUTHORIZATION_DEVICE_SERVICE
                = new AuthorizationDeviceServiceImplementation(null);
    }
}
