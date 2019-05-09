package eu.marcinszewczyk.services;

import eu.marcinszewczyk.db.DbFactory;

import java.util.Map;

public class ServiceProvider {
    private Map<Class, Object> servicesInstances;

    public ServiceProvider(DbFactory dbFactory) {
        servicesInstances = Map.of(
                TransferService.class,
                new TransferServiceImpl(
                        dbFactory.getTransferRepository(),
                        dbFactory.getAccountRepository())
        );
    }

    public TransferService getTransferService() {
        return (TransferService) servicesInstances.get(TransferService.class);
    }
}
