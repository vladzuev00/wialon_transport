package by.zuevvlad.wialontransport.service.extendeddata;

//public final class ExtendedDataServiceImplementation implements ExtendedDataService {
//    private final EntityRepository<ExtendedData> extendedDataRepository;
//
//    public static ExtendedDataService create() {
//        return SingletonHolder.DATA_SERVICE;
//    }
//
//    private ExtendedDataServiceImplementation(final EntityRepository<ExtendedData> extendedDataRepository) {
//        this.extendedDataRepository = extendedDataRepository;
//    }
//
//    @Override
//    public Status save(final ExtendedData savedData) {
//        try {
//            this.extendedDataRepository.insert(savedData);
//            return PACKAGE_FIX_SUCCESS;
//        } catch (final InsertingEntityException exception) {
//            return ERROR_PACKAGE_STRUCTURE;
//        }
//    }
//
//    private static final class SingletonHolder {
//        private static final ExtendedDataService DATA_SERVICE = new ExtendedDataServiceImplementation(
//                TempExtendedDataRepository.create());
//    }
//}
