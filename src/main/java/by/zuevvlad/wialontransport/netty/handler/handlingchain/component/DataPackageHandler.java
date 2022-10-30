package by.zuevvlad.wialontransport.netty.handler.handlingchain.component;

import by.zuevvlad.wialontransport.entity.ExtendedDataEntity;
import by.zuevvlad.wialontransport.netty.handler.handlingchain.PackageHandler;
import by.zuevvlad.wialontransport.service.crud.CRUDEntityService;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.data.RequestDataPackage;
import by.zuevvlad.wialontransport.wialonpackage.data.ResponseDataPackage;
import io.netty.channel.ChannelHandlerContext;

import static by.zuevvlad.wialontransport.wialonpackage.data.ResponseDataPackage.Status.PACKAGE_FIX_SUCCESS;

public final class DataPackageHandler extends PackageHandler {
    private final CRUDEntityService<ExtendedDataEntity> extendedDataService;

//    public static PackageHandler create() {
//        return SingletonHolder.PACKAGE_HANDLER;
//    }

    private DataPackageHandler(final PackageHandler nextHandler,
                               final CRUDEntityService<ExtendedDataEntity> extendedDataService) {
        super(RequestDataPackage.class, nextHandler);
        this.extendedDataService = extendedDataService;
    }

    @Override
    protected void handleIndependently(final Package handledPackage, final ChannelHandlerContext context) {
        final RequestDataPackage requestDataPackage = (RequestDataPackage) handledPackage;
        this.extendedDataService.save(requestDataPackage.getExtendedData());
        final ResponseDataPackage responseDataPackage = new ResponseDataPackage(PACKAGE_FIX_SUCCESS);   //TODO: остальные статусы должны обрабатываться ловя исключения при парсинге
        context.writeAndFlush(responseDataPackage);
    }

//    private static final class SingletonHolder {
//        private static final PackageHandler PACKAGE_HANDLER = new DataPackageHandler();
//    }
}
