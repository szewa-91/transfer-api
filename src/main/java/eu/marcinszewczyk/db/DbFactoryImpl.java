package eu.marcinszewczyk.db;

import javax.persistence.EntityManagerFactory;

public class DbFactoryImpl implements DbFactory {
    private final EntityManagerProvider entityManagerProvider;
    private final LockingService lockingService;

    public DbFactoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerProvider = new EntityManagerProvider(entityManagerFactory);
        this.lockingService = new LockingService();
    }

    @Override
    public AccountRepository getAccountRepository() {
        return new AccountRepository(entityManagerProvider);
    }

    @Override
    public TransferRepository getTransferRepository() {
        return new TransferRepository(entityManagerProvider);
    }

    @Override
    public LockingService getLockingService() {
        return lockingService;
    }

}

