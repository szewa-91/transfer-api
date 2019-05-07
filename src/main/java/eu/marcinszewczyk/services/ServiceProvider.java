package eu.marcinszewczyk.services;

import eu.marcinszewczyk.db.DaoProvider;
import eu.marcinszewczyk.db.DbFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class ServiceProvider {
    private Map<Class, Object> servicesInstances;

    public ServiceProvider(DbFactory dbFactory) throws IOException, SQLException {
        DaoProvider daoProvider = dbFactory.getDaos();
        servicesInstances = Map.of(
                TransactionsService.class, new TransactionsServiceImpl(daoProvider.getTransactionDao())
        );
    }

    public TransactionsService getTransactionsService() {
        return (TransactionsService) servicesInstances.get(TransactionsService.class);
    }
}
