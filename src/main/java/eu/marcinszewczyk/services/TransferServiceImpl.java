package eu.marcinszewczyk.services;

import eu.marcinszewczyk.db.AccountRepository;
import eu.marcinszewczyk.db.LockingService;
import eu.marcinszewczyk.db.TransferRepository;
import eu.marcinszewczyk.model.Transfer;
import eu.marcinszewczyk.model.TransferStatus;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.locks.Lock;

public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final LockingService lockingService;

    TransferServiceImpl(TransferRepository transferRepository, AccountRepository accountRepository, LockingService lockingService) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.lockingService = lockingService;
    }

    public Collection<Transfer> getAllTransfers() {
        return transferRepository.findAll();
    }

    public Transfer getTransfer(Long id) {
        return transferRepository.findById(id);
    }

    public Transfer executeTransfer(Transfer transfer) {
        System.out.println("Received transfer: " + transfer);
        Lock payerLock = lockingService.getLock(transfer.getPayerAccountNumber());
        Lock receiverLock = lockingService.getLock(transfer.getReceiverAccountNumber());
        System.out.println("Try to lock " + transfer.getPayerAccountNumber());
        payerLock.lock();
        System.out.println("Locked " + transfer.getPayerAccountNumber());
        System.out.println("Try to lock " + transfer.getReceiverAccountNumber());
        receiverLock.lock();
        System.out.println("Locked " + transfer.getReceiverAccountNumber());

        validateTransfer(transfer);
        Transfer transferAttempt = saveWithStatus(transfer, TransferStatus.CREATED);
        TransferStatus transferStatus = accountRepository.performMovement(transferAttempt);
        Transfer transfer1 = saveWithStatus(transferAttempt, transferStatus);


        System.out.println("Unlocked " + transfer.getPayerAccountNumber());
        payerLock.unlock();
        System.out.println("Unlocked " + transfer.getReceiverAccountNumber());
        receiverLock.unlock();
        return transfer1;
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
