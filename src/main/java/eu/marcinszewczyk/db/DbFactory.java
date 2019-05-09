package eu.marcinszewczyk.db;

public interface DbFactory {
    AccountRepository getAccountRepository();
    TransactionRepository getTransactionRepository();
}
