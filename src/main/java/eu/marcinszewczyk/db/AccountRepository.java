package eu.marcinszewczyk.db;

import eu.marcinszewczyk.model.Account;

import javax.persistence.EntityManager;

public class AccountRepository {
    private EntityManager entityManager;

    public AccountRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Account findById(String payerAccountNumber) {
        return entityManager.find(Account.class, payerAccountNumber);

    }

    public void save(Account payerAccount) {
        entityManager.getTransaction().begin();
        entityManager.persist(payerAccount);
        entityManager.getTransaction().commit();
    }
}
