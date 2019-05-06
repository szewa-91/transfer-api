package eu.marcinszewczyk.services;

import eu.marcinszewczyk.model.Transaction;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.synchronizedMap;

public class TransactionsServiceImpl implements TransactionsService {

    private Map<Long, Transaction> transactions = synchronizedMap(new HashMap<>());

    TransactionsServiceImpl() {
        transactions.put(1L, transaction(1L, "1234567", "7654321"));
        transactions.put(2L, transaction(2L, "7654321", "1234567"));
        transactions.put(3L, transaction(3L, "1234567", "7654765"));
    }

    public Collection<Transaction> getAllTransactions() {
        return transactions.values();
    }

    public Transaction getTransaction(Long id) {
        return transactions.get(id);
    }

    public Transaction saveTransaction(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    private Transaction transaction(Long id, String payerAccountNumber, String receiverAccountNumber) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setPayerAccountNumber(payerAccountNumber);
        transaction.setPayerAccountNumber(payerAccountNumber);
        transaction.setReceiverAccountNumber(receiverAccountNumber);
        transaction.setAmount(BigDecimal.valueOf(200000.20));
        return transaction;
    }
}
