package eu.marcinszewczyk;

import eu.marcinszewczyk.db.DbFactory;
import eu.marcinszewczyk.db.DbFactoryImpl;
import eu.marcinszewczyk.server.JettyServer;
import eu.marcinszewczyk.services.ServiceProvider;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManagerFactory;

public class App {
    private final static int PORT = 9090;

    public static void main(String... args) {
        SessionFactory sessionFactory = new Configuration().configure()
                .buildSessionFactory();
        EntityManagerFactory entityManager = sessionFactory.openSession().getEntityManagerFactory();

        DbFactory dbFactory = new DbFactoryImpl(entityManager);


        ServiceProvider serviceProvider = new ServiceProvider(dbFactory);
        new JettyServer(PORT, serviceProvider).start();
    }
}
