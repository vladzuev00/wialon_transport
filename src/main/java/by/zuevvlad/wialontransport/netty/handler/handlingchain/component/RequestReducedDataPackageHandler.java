package by.zuevvlad.wialontransport.netty.handler.handlingchain.component;

import by.zuevvlad.wialontransport.entity.Data;
import by.zuevvlad.wialontransport.netty.handler.handlingchain.PackageHandler;
import by.zuevvlad.wialontransport.service.crud.CRUDEntityService;
import by.zuevvlad.wialontransport.service.crud.DataService;
import by.zuevvlad.wialontransport.wialonpackage.Package;
import by.zuevvlad.wialontransport.wialonpackage.data.RequestDataPackage;
import by.zuevvlad.wialontransport.wialonpackage.reduceddata.RequestReducedDataPackage;
import by.zuevvlad.wialontransport.wialonpackage.reduceddata.ResponseReducedDataPackage;
import io.netty.channel.ChannelHandlerContext;

import static by.zuevvlad.wialontransport.wialonpackage.reduceddata.ResponseReducedDataPackage.Status.PACKAGE_FIX_SUCCESS;

public final class RequestReducedDataPackageHandler extends PackageHandler {
    private final CRUDEntityService<Data> dataService;

    private RequestReducedDataPackageHandler(final PackageHandler nextHandler, final CRUDEntityService<Data> dataService) {
        super(RequestDataPackage.class, nextHandler);
        this.dataService = dataService;
    }

    public static PackageHandler create() {
        return SingletonHolder.PACKAGE_ANSWERER;
    }

    @Override
    protected void handleIndependently(final Package handledPackage, final ChannelHandlerContext context) {
        final RequestReducedDataPackage requestReducedDataPackage = (RequestReducedDataPackage) handledPackage;
        this.dataService.save(requestReducedDataPackage.getData());
        final ResponseReducedDataPackage responseReducedDataPackage
                = new ResponseReducedDataPackage(PACKAGE_FIX_SUCCESS);  //TODO: остальные статусы должны обрабатываться ловя исключения при парсинге
        context.writeAndFlush(responseReducedDataPackage);
    }

    private static final class SingletonHolder {
        private static final PackageHandler PACKAGE_ANSWERER = new RequestReducedDataPackageHandler(
                DataPackageHandler.create(), DataService.create());
    }
}
