package eu.marcinszewczyk.services;

import java.util.Map;

public class ServiceProvider {
    private static Map<Class, Object> servicesInstances = Map.of(
            TransactionsService.class, new TransactionsServiceImpl()
    );

    public static TransactionsService getTransactionsService() {
        return (TransactionsService) servicesInstances.get(TransactionsService.class);
    }
}
