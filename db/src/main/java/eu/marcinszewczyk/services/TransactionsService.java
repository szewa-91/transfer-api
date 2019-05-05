package eu.marcinszewczyk.services;

import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.model.TransactionDirection;

import java.math.BigDecimal;

public class TransactionsService {
    public Transaction getTransaction(Long id) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAccountNumber("1234567");
        transaction.setDirection(TransactionDirection.PAY);
        transaction.setAmount(BigDecimal.valueOf(200000.20));
        return transaction;
    }
}
