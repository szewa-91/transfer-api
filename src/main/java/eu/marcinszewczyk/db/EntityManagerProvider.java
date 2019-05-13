package eu.marcinszewczyk.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EntityManagerProvider {
    private final ThreadLocal<EntityManager> entityManager ;


    EntityManagerProvider(EntityManagerFactory entityManagerFactory) {
        entityManager = ThreadLocal.withInitial(entityManagerFactory::createEntityManager);
    }

    public EntityManager getEntityManager() {
        return entityManager.get();
    }


}
