package eu.marcinszewczyk.services;

import eu.marcinszewczyk.db.AccountRepository;
import eu.marcinszewczyk.db.TransferRepository;
import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.model.Transfer;
import eu.marcinszewczyk.model.TransferStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
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

    public Transfer executeTransfer(Transfer transfer) throws SQLException {
        System.out.println("Received transfer: " + transfer);
        validateTransfer(transfer);

        transfer.setStatus(TransferStatus.CREATED);
        transferRepository.save(transfer);

        TransferStatus status = performMovement(transfer);
        return updateWithStatus(transfer, status);
    }

    private TransferStatus performMovement(Transfer transfer) throws SQLException {
        Account payerAccount = getAccount(transfer.getPayerAccountNumber());
        Account receiverAccount = getAccount(transfer.getReceiverAccountNumber());

        if (payerAccount == null) {
            System.out.println("No account found: " + transfer.getPayerAccountNumber()
                    + "Transfer rejected: " + transfer);
            return TransferStatus.REJECTED;
        }
        if (receiverAccount == null) {
            System.out.println("No account found: " + transfer.getReceiverAccountNumber()
                    + "Transfer rejected: " + transfer);
            return TransferStatus.REJECTED;
        }
        BigDecimal amount = transfer.getAmount();
        if (payerAccount.hasAmount(amount)) {
            payerAccount.subtractFromBalance(amount);
            receiverAccount.addToBalance(amount);
            accountRepository.save(payerAccount);
            accountRepository.save(receiverAccount);
            System.out.println("Transfer executed: " + transfer);
            return TransferStatus.COMPLETED;
        } else {
            System.out.println("Transfer not executed, no money on the payer account: " + transfer);
            return TransferStatus.REJECTED;
        }
    }

    private Transfer updateWithStatus(Transfer transfer, TransferStatus rejected) {
        transfer.setStatus(rejected);
        transferRepository.save(transfer);
        return transfer;
    }

    private Account getAccount(String payerAccountNumber) {
        return accountRepository.findById(payerAccountNumber);
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
