package eu.marcinszewczyk.db;

import com.j256.ormlite.dao.Dao;
import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.model.Transaction;

public class DaoProvider {
    private Dao<Account, String> accountDao;
    private Dao<Transaction, Long> transactionDao;

    DaoProvider(Dao<Account, String> accountDao, Dao<Transaction, Long> transactionDao) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
    }

    public Dao<Account, String> getAccountDao() {
        return accountDao;
    }

    public Dao<Transaction, Long> getTransactionDao() {
        return transactionDao;
    }
}
