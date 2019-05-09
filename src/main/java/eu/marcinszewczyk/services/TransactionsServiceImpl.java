package eu.marcinszewczyk.services;

import eu.marcinszewczyk.db.AccountRepository;
import eu.marcinszewczyk.db.TransactionRepository;
import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.model.TransactionStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;

public class TransactionsServiceImpl implements TransactionsService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionsServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public Collection<Transaction> getAllTransactions() throws SQLException {
        return transactionRepository.findAll();
    }

    public Transaction getTransaction(Long id) throws SQLException {
        return transactionRepository.findById(id);
    }

    public Transaction executeTransaction(Transaction transaction) throws SQLException {
        System.out.println("Received transaction: " + transaction);
        validateTransaction(transaction);

        transaction.setStatus(TransactionStatus.CREATED);
        transactionRepository.save(transaction);

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
            accountRepository.save(payerAccount);
            accountRepository.save(receiverAccount);
            System.out.println("Transaction executed: " + transaction);
            return updateWithStatus(transaction, TransactionStatus.COMPLETED);
        } else {
            System.out.println("Transaction not executed, no money on the payer account: " + transaction);
            return updateWithStatus(transaction, TransactionStatus.REJECTED);
        }
    }

    private Transaction updateWithStatus(Transaction transaction, TransactionStatus rejected) throws SQLException {
        transaction.setStatus(rejected);
        transactionRepository.save(transaction);
        return transaction;
    }

    private Account getAccount(String payerAccountNumber) throws SQLException {
        return accountRepository.findById(payerAccountNumber);
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
