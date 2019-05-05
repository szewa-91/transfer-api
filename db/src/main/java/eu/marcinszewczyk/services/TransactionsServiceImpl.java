package eu.marcinszewczyk.services;

import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.model.TransactionDirection;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.synchronizedMap;

public class TransactionsServiceImpl implements TransactionsService {
    private Map<Long, Transaction> transactions = synchronizedMap(new HashMap<>());
    {
        transactions.put(1L, transaction(1L));
        transactions.put(2L, transaction(2L));
        transactions.put(3L, transaction(3L));
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

    private Transaction transaction(Long id) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAccountNumber("1234567");
        transaction.setDirection(TransactionDirection.PAY);
        transaction.setAmount(BigDecimal.valueOf(200000.20));
        return transaction;
    }
}
