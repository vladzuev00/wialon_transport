package by.zuevvlad.wialontransport.service.implementation;

/*
@Component
public final class NotDeliveredDataSendServiceImplementation implements NotDeliveredDataSendService {
    private final DataDAO dataDAO;
    private final DataSender dataSender;

    public NotDeliveredDataSendServiceImplementation(final DataDAO dataDAO, final DataSender dataSender) {
        this.dataDAO = dataDAO;
        this.dataSender = dataSender;
    }


    @Override
    public void addNotDeliveredData(final Data addedNotDeliveredData) {
        this.dataDAO.save(addedNotDeliveredData);
    }

    @Override
    @Scheduled(fixedDelay = 1000 * 5)
    public void sendNotDeliveredData() {
        final List<Data> notDeliveredData = this.dataDAO.findAll();
        notDeliveredData.forEach(sentData -> this.dataSender.sendData(sentData, (metadata, exception) -> {
            if (exception == null) {
                this.dataDAO.delete(sentData);
            }
        }));
    }
}
*/
