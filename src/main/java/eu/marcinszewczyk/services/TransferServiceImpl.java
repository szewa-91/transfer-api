package eu.marcinszewczyk.services;

import eu.marcinszewczyk.db.AccountRepository;
import eu.marcinszewczyk.db.TransferRepository;
import eu.marcinszewczyk.model.Transfer;
import eu.marcinszewczyk.model.TransferStatus;

import javax.persistence.OptimisticLockException;
import java.math.BigDecimal;
import java.util.Collection;

public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    TransferServiceImpl(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    public Collection<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }

    public Transfer getTransfer(Long id) {
        return transferRepository.findById(id);
    }

    public Transfer executeTransfer(Transfer transfer) {
        System.out.println("Received transfer: " + transfer);
        validateTransfer(transfer);
        for (int retryAttempts = 3; ; retryAttempts--) {
            Transfer transferAttempt = saveWithStatus(transfer, TransferStatus.CREATED);
            try {
                TransferStatus transferStatus = accountRepository.performMovement(transferAttempt);
                return saveWithStatus(transferAttempt, transferStatus);
            } catch (OptimisticLockException e) {
                e.printStackTrace();
                if (retryAttempts == 0) {
                    System.out.println("Transfer not executed. Retry attempts used.");
                    return saveWithStatus(transferAttempt, TransferStatus.REJECTED);
                }
            }
            System.out.println(String.format("Retrying attempt with transfer (%d retries left), " +
                    "transaction: %s", retryAttempts, transferAttempt));
            saveWithStatus(transferAttempt, TransferStatus.REJECTED);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Transfer saveWithStatus(Transfer transfer, TransferStatus rejected) {
        transfer.setStatus(rejected);
        return transferRepository.save(transfer);
    }

    private void validateTransfer(Transfer transfer) {
        if (transfer.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }
        if (transfer.getPayerAccountNumber() == null || transfer.getPayerAccountNumber().isBlank()) {
            throw new IllegalArgumentException();
        }
        if (transfer.getReceiverAccountNumber() == null || transfer.getReceiverAccountNumber().isBlank()) {
            throw new IllegalArgumentException();
        }
    }
}
