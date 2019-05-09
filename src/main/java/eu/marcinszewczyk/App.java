package eu.marcinszewczyk;

import eu.marcinszewczyk.db.DbFactory;
import eu.marcinszewczyk.db.DbFactoryImpl;
import eu.marcinszewczyk.server.JettyServer;
import eu.marcinszewczyk.services.ServiceProvider;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;

public class App {
    private final static int PORT = 9090;

    public static void main(String... args) throws Exception {
        SessionFactory sessionFactory = new Configuration().configure()
                .buildSessionFactory();
        EntityManager entityManager = sessionFactory.createEntityManager();

        DbFactory dbFactory = new DbFactoryImpl(entityManager);


        ServiceProvider serviceProvider = new ServiceProvider(dbFactory);
        new JettyServer(PORT, serviceProvider).start();
    }
}
