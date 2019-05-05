package eu.marcinszewczyk.services;

import eu.marcinszewczyk.model.Transaction;

import java.util.Collection;

public interface TransactionsService {
    Collection<Transaction> getAllTransactions();
    Transaction getTransaction(Long id);
    Transaction saveTransaction(Transaction transaction);
}
