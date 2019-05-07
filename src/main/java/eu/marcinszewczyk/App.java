package eu.marcinszewczyk;

import eu.marcinszewczyk.db.DbConfig;
import eu.marcinszewczyk.db.DbFactory;
import eu.marcinszewczyk.db.DbFactoryImpl;
import eu.marcinszewczyk.server.JettyServer;
import eu.marcinszewczyk.services.ServiceProvider;

public class App {
    private final static int PORT = 9090;
    private final static String DATABASE_URL = "jdbc:h2:~/transfers";
    private final static String USERNAME = "sa";
    private final static String PASSWORD = "";
    private final static boolean SHOULD_CREATE_SCHEMA = true;

    public static void main(String... args) throws Exception {
        DbConfig dbConfig = new DbConfig(DATABASE_URL, USERNAME, PASSWORD, SHOULD_CREATE_SCHEMA);
        DbFactory dbFactory= new DbFactoryImpl(dbConfig);
        ServiceProvider serviceProvider = new ServiceProvider(dbFactory);
        new JettyServer(PORT, serviceProvider).start();
    }
}
