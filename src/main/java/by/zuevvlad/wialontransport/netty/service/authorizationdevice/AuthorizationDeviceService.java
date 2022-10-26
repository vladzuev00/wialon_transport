package by.zuevvlad.wialontransport.netty.service.authorizationdevice;

import by.zuevvlad.wialontransport.wialonpackage.login.RequestLoginPackage;
import by.zuevvlad.wialontransport.wialonpackage.login.ResponseLoginPackage.Status;

@FunctionalInterface
public interface AuthorizationDeviceService {
    Status authorize(final RequestLoginPackage loginPackage);
}
