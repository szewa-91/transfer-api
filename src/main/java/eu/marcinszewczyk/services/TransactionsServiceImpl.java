package eu.marcinszewczyk.services;

import com.j256.ormlite.dao.Dao;
import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.model.TransactionStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;

public class TransactionsServiceImpl implements TransactionsService {

    private final Dao<Transaction, Long> transactionDao;
    private final Dao<Account, String> accountDao;

    TransactionsServiceImpl(Dao<Transaction, Long> transactionDao, Dao<Account, String> accountDao) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
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
            return null;
        }
    }

    public Transaction executeTransaction(Transaction transaction) {
        try {
            System.out.println("Received transaction: " + transaction);
            validateTransaction(transaction);

            transaction.setStatus(TransactionStatus.CREATED);
            transactionDao.create(transaction);

            Account payerAccount = getAccount(transaction.getPayerAccountNumber());
            Account receiverAccount = getAccount(transaction.getReceiverAccountNumber());

            if (payerAccount == null) {
                System.out.println("No account found: " + transaction.getPayerAccountNumber()
                        + "Transaction rejected: " + transaction);
                return updateWithStatus(transaction, TransactionStatus.REJECTED);
            }
            if (receiverAccount == null) {
                System.out.println("No account found: " + transaction.getReceiverAccountNumber()
                        + "Transaction rejected: " + transaction);
                return updateWithStatus(transaction, TransactionStatus.REJECTED);
            }
            BigDecimal amount = transaction.getAmount();
            if (payerAccount.hasAmount(amount)) {
                payerAccount.subtractFromBalance(amount);
                receiverAccount.addToBalance(amount);
                accountDao.update(payerAccount);
                accountDao.update(receiverAccount);
                System.out.println("Transaction executed: " + transaction);
                return updateWithStatus(transaction, TransactionStatus.COMPLETED);
            } else {
                System.out.println("Transaction not executed, no money on the payer account: " + transaction);
                return updateWithStatus(transaction, TransactionStatus.REJECTED);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            transaction.setStatus(TransactionStatus.REJECTED);
            return transaction;
        }

    }

    private Transaction updateWithStatus(Transaction transaction, TransactionStatus rejected) throws SQLException {
        transaction.setStatus(rejected);
        transactionDao.update(transaction);
        return transaction;
    }

    private Account getAccount(String payerAccountNumber) throws SQLException {
        return accountDao.queryForId(payerAccountNumber);
    }

    private void validateTransaction(Transaction transaction) {
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }
        if (transaction.getPayerAccountNumber() == null || transaction.getPayerAccountNumber().isBlank()) {
            throw new IllegalArgumentException();
        }
        if (transaction.getReceiverAccountNumber() == null || transaction.getReceiverAccountNumber().isBlank()) {
            throw new IllegalArgumentException();
        }
    }
}
