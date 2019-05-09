package eu.marcinszewczyk.util;

import eu.marcinszewczyk.db.DbFactory;
import eu.marcinszewczyk.db.DbFactoryImpl;
import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.model.TransactionStatus;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import java.util.Properties;

public class DbTestUtil {

    public final static int PORT = 9292;
    private final static String DATABASE_URL = "jdbc:h2:~/test";

    public static DbFactory getTestDbFactory() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        //log settings
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.show_sql", "true");
        //driver settings
        properties.put("hibernate.connection.driver_class", "org.h2.Driver");
        properties.put("hibernate.connection.url", "jdbc:h2:~/test");
        properties.put("hibernate.connection.username", "sa");
        properties.put("hibernate.connection.password", "");

        SessionFactory sessionFactory = new Configuration().configure()
                .addProperties(properties)
                .addAnnotatedClass(Transaction.class)
                .addAnnotatedClass(Account.class)
                .addAnnotatedClass(TransactionStatus.class)
                .buildSessionFactory();
        EntityManager entityManager = sessionFactory.createEntityManager();


        return new DbFactoryImpl(entityManager);
    }
}
