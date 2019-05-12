package eu.marcinszewczyk.services;

import eu.marcinszewczyk.db.AccountRepository;
import eu.marcinszewczyk.db.TransferRepository;
import eu.marcinszewczyk.model.Transfer;
import eu.marcinszewczyk.model.TransferStatus;

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
        transfer = saveWithStatus(transfer, TransferStatus.CREATED);
        TransferStatus transferStatus = accountRepository.performMovement(transfer);
        return saveWithStatus(transfer, transferStatus);
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
