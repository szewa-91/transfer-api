package eu.marcinszewczyk.db;

import eu.marcinszewczyk.model.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class TransactionRepository {
    private EntityManager entityManager;

    public TransactionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Collection<Transaction> findAll() {
        Query query = entityManager.createQuery("SELECT e FROM Transaction e");
        return (Collection<Transaction>) query.getResultList();
    }

    public Transaction findById(Long id) {
        return entityManager.find(Transaction.class, id);
    }

    public void save(Transaction transaction) {
        entityManager.getTransaction().begin();
        entityManager.persist(transaction);
        entityManager.getTransaction().commit();
//        return mergedTransaction;
    }
}
