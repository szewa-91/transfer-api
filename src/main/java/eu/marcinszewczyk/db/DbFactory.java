package eu.marcinszewczyk.db;

public interface DbFactory {
    AccountRepository getAccountRepository();

    TransferRepository getTransferRepository();

    LockingService getLockingService();
}
