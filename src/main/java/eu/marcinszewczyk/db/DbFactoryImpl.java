package eu.marcinszewczyk.db;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.model.Transaction;

import java.io.IOException;
import java.sql.SQLException;

public class DbFactoryImpl implements DbFactory {
    private DbConfig dbConfig;

    public DbFactoryImpl(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public DaoProvider setupDatabase() throws SQLException, IOException {
        try (ConnectionSource connectionSource = openConnection(dbConfig)) {
            if (dbConfig.isShouldCreateSchema()) {
                TableUtils.dropTable(connectionSource, Transaction.class, true);
                TableUtils.dropTable(connectionSource, Account.class, true);
                TableUtils.createTable(connectionSource, Account.class);
                TableUtils.createTable(connectionSource, Transaction.class);
            }
            return new DaoProvider(
                    DaoManager.createDao(connectionSource, Account.class),
                    DaoManager.createDao(connectionSource, Transaction.class));
        }
    }

    private JdbcConnectionSource openConnection(DbConfig dbConfig) throws SQLException {
        return new JdbcConnectionSource(
                dbConfig.getDatabaseUrl(),
                dbConfig.getUsername(),
                dbConfig.getPassword()
        );
    }
}
