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
        try {
            transactionDao.create(transaction("1234567", "7654321"));
            transactionDao.create(transaction("7654321", "1234567"));
            transactionDao.create(transaction("1234567", "7654765"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private Transaction transaction(String payerAccountNumber, String receiverAccountNumber) {
        Transaction transaction = new Transaction();
        transaction.setPayerAccountNumber(payerAccountNumber);
        transaction.setPayerAccountNumber(payerAccountNumber);
        transaction.setReceiverAccountNumber(receiverAccountNumber);
        transaction.setAmount(BigDecimal.valueOf(200000.20));
        transaction.setCurrencyCode("PLN");
        transaction.setStatus(TransactionStatus.CREATED);
        return transaction;
    }
}
