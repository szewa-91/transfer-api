package eu.marcinszewczyk.db;

import eu.marcinszewczyk.model.Transfer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

public class TransferRepository {
    private final EntityManagerProvider entityManagerProvider;

    TransferRepository(EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    public Collection<Transfer> findAll() {
        Query query = entityManagerProvider.getEntityManager().createQuery("SELECT e FROM Transfer e");
        return (Collection<Transfer>) query.getResultList();
    }

    public Transfer findById(Long id) {
        return entityManagerProvider.getEntityManager().find(Transfer.class, id);
    }

    public Transfer save(Transfer transfer) {
        EntityManager entityManager = entityManagerProvider.getEntityManager();
        entityManager.getTransaction().begin();
        Transfer savedTransfer = entityManager.merge(transfer);
        entityManager.getTransaction().commit();
        return savedTransfer;
    }
}
