package eu.marcinszewczyk.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.model.Transaction;

public class DatabaseConfig {
    private final static String DATABASE_URL = "jdbc:h2:~/test";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private Dao<Account, String> accountDao;
    private Dao<Transaction, Long> transactionDao;

    private DatabaseConfig(Dao<Account, String> accountDao, Dao<Transaction, Long> transactionDao) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
    }

    public static DatabaseConfig setupDatabase(boolean createSchema) throws Exception {
        try (ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL, USERNAME, PASSWORD)) {
            if (createSchema) {
                TableUtils.dropTable(connectionSource, Transaction.class, true);
                TableUtils.dropTable(connectionSource, Account.class, true);
                TableUtils.createTable(connectionSource, Account.class);
                TableUtils.createTable(connectionSource, Transaction.class);
            }
            return new DatabaseConfig(
                    DaoManager.createDao(connectionSource, Account.class),
                    DaoManager.createDao(connectionSource, Transaction.class));
        }
    }

    public Dao<Account, String> getAccountDao() {
        return accountDao;
    }

    public Dao<Transaction, Long> getTransactionDao() {
        return transactionDao;
    }
}
