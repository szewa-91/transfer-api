package eu.marcinszewczyk.util;

import eu.marcinszewczyk.db.DbConfig;
import eu.marcinszewczyk.db.DbFactory;
import eu.marcinszewczyk.db.DbFactoryImpl;

public class DbTestUtil {
    public final static int PORT = 9292;
    private final static String DATABASE_URL = "jdbc:h2:~/test";
    private final static String USERNAME = "sa";
    private final static String PASSWORD = "";
    private final static boolean SHOULD_CREATE_SCHEMA = true;

    public static DbFactory getTestDbFactory() {
        DbConfig dbConfig = new DbConfig(DATABASE_URL, USERNAME, PASSWORD, SHOULD_CREATE_SCHEMA);
        return new DbFactoryImpl(dbConfig);
    }
}
