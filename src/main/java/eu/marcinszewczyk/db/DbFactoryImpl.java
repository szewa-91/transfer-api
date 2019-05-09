package eu.marcinszewczyk.db;

import javax.persistence.EntityManager;

public class DbFactoryImpl implements DbFactory {
    private EntityManager entityManager;

    public DbFactoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public AccountRepository getAccountRepository() {
        return new AccountRepository(entityManager);
    }

    @Override
    public TransactionRepository getTransactionRepository() {
        return new TransactionRepository(entityManager);
    }
}
