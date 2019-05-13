package eu.marcinszewczyk.db;

import javax.persistence.EntityManagerFactory;

public class DbFactoryImpl implements DbFactory {
    private final EntityManagerProvider entityManagerProvider;

    public DbFactoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerProvider = new EntityManagerProvider(entityManagerFactory);
    }

    @Override
    public AccountRepository getAccountRepository() {
        return new AccountRepository(entityManagerProvider);
    }

    @Override
    public TransferRepository getTransferRepository() {
        return new TransferRepository(entityManagerProvider);
    }
}

