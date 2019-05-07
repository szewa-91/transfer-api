package eu.marcinszewczyk.services;

import com.j256.ormlite.dao.Dao;
import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.model.TransactionStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.synchronizedMap;

public class TransactionsServiceImpl implements TransactionsService {

    private final Dao<Transaction, Long> transactionDao;

    private Map<Long, Transaction> transactions = synchronizedMap(new HashMap<>());

    TransactionsServiceImpl(Dao<Transaction, Long> transactionDao) {
        this.transactionDao = transactionDao;
    }

    public Collection<Transaction> getAllTransactions() {
        try {
            return transactionDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Transaction getTransaction(Long id) {
        try {
            return transactionDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Transaction saveTransaction(Transaction transaction) {
        try {
            transactionDao.create(transaction);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaction;
    }
}
