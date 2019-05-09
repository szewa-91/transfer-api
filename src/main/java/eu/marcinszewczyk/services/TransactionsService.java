package eu.marcinszewczyk.services;

import eu.marcinszewczyk.model.Transaction;

import java.sql.SQLException;
import java.util.Collection;

public interface TransactionsService {
    Collection<Transaction> getAllTransactions() throws SQLException;
    Transaction getTransaction(Long id) throws SQLException;
    Transaction executeTransaction(Transaction transaction) throws SQLException;
}
