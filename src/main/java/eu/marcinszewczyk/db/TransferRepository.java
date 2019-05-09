package eu.marcinszewczyk.db;

import eu.marcinszewczyk.model.Transfer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class TransferRepository {
    private EntityManager entityManager;

    TransferRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Collection<Transfer> findAll() {
        Query query = entityManager.createQuery("SELECT e FROM Transfer e");
        return (Collection<Transfer>) query.getResultList();
    }

    public Transfer findById(Long id) {
        return entityManager.find(Transfer.class, id);
    }

    public void save(Transfer transfer) {
        entityManager.getTransaction().begin();
        entityManager.persist(transfer);
        entityManager.getTransaction().commit();
    }
}
