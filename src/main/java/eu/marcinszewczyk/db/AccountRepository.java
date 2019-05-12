package eu.marcinszewczyk.db;

import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.model.Transfer;
import eu.marcinszewczyk.model.TransferStatus;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.math.BigDecimal;

public class AccountRepository {
    private EntityManager entityManager;

    public AccountRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Account findById(String payerAccountNumber) {
        return entityManager.find(Account.class, payerAccountNumber);
    }

    public Account save(Account payerAccount) {
        entityManager.getTransaction().begin();
        Account savedAccount = entityManager.merge(payerAccount);
        entityManager.getTransaction().commit();
        return savedAccount;
    }

    public TransferStatus performMovement(Transfer transfer) {
        entityManager.getTransaction().begin();

        Account payerAccount = entityManager.find(Account.class, transfer.getPayerAccountNumber(), LockModeType.OPTIMISTIC);
        Account receiverAccount = entityManager.find(Account.class, transfer.getReceiverAccountNumber(), LockModeType.OPTIMISTIC);

        if (payerAccount == null) {
            System.out.println("No account found: " + transfer.getPayerAccountNumber()
                    + "Transfer rejected: " + transfer);
            entityManager.getTransaction().rollback();
            return TransferStatus.REJECTED;
        }
        if (receiverAccount == null) {
            System.out.println("No account found: " + transfer.getReceiverAccountNumber()
                    + "Transfer rejected: " + transfer);
            entityManager.getTransaction().rollback();
            return TransferStatus.REJECTED;
        }
        BigDecimal amount = transfer.getAmount();
        if (payerAccount.hasAmount(amount)) {
            payerAccount.subtractFromBalance(amount);
            receiverAccount.addToBalance(amount);
            entityManager.persist(payerAccount);
            entityManager.persist(receiverAccount);
            System.out.println("Transfer executed: " + transfer);
            entityManager.getTransaction().commit();
            return TransferStatus.COMPLETED;
        } else {
            System.out.println("Transfer not executed, no money on the payer account: " + transfer);
            entityManager.getTransaction().rollback();
            return TransferStatus.REJECTED;
        }
    }
}
