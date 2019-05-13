package eu.marcinszewczyk.db;

import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.model.Transfer;
import eu.marcinszewczyk.model.TransferStatus;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.math.BigDecimal;

public class AccountRepository {
    private final EntityManagerProvider entityManagerProvider;

    AccountRepository(EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    public Account findById(String payerAccountNumber) {
        return entityManagerProvider.getEntityManager().find(Account.class, payerAccountNumber);
    }

    public Account save(Account payerAccount) {
        EntityManager entityManager = entityManagerProvider.getEntityManager();
        entityManager.getTransaction().begin();
        Account savedAccount = entityManager.merge(payerAccount);
        entityManager.getTransaction().commit();
        return savedAccount;
    }

    public TransferStatus performMovement(Transfer transfer) {
        EntityManager entityManager = entityManagerProvider.getEntityManager();
        entityManager.getTransaction().begin();

        Account payerAccount = entityManager.find(Account.class, transfer.getPayerAccountNumber(), LockModeType.OPTIMISTIC);
        Account receiverAccount = entityManager.find(Account.class, transfer.getReceiverAccountNumber(), LockModeType.OPTIMISTIC);

        if (payerAccount == null) {
            System.out.println("Transfer not executed. No account found: " + transfer.getPayerAccountNumber()
                    + "Transfer rejected: " + transfer);
            entityManager.getTransaction().rollback();
            return TransferStatus.REJECTED;
        }
        if (receiverAccount == null) {
            System.out.println("Transfer not executed. No account found: " + transfer.getReceiverAccountNumber()
                    + "Transfer rejected: " + transfer);
            entityManager.getTransaction().rollback();
            return TransferStatus.REJECTED;
        }

        System.out.println("Payer account: " + payerAccount.getAccountNumber() +
                " has balance: " + payerAccount.getBalance());
        System.out.println("Receiver account: " + receiverAccount.getAccountNumber() +
                " has balance: " + receiverAccount.getBalance());
        BigDecimal amount = transfer.getAmount();
        if (payerAccount.hasAmount(amount)) {
            payerAccount.subtractFromBalance(amount);
            receiverAccount.addToBalance(amount);
            entityManager.persist(payerAccount);
            entityManager.persist(receiverAccount);
            entityManager.getTransaction().commit();
            System.out.println("Payer account: " + payerAccount.getAccountNumber() +
                    " has balance: " + payerAccount.getBalance() + " after transfer.");
            System.out.println("Receiver account: " + receiverAccount.getAccountNumber() +
                    " has balance: " + receiverAccount.getBalance() + " after transfer.");
            System.out.println("Transfer executed: " + transfer);

            return TransferStatus.COMPLETED;
        } else {
            System.out.println("Transfer not executed. No money on the payer account: " + transfer);
            entityManager.getTransaction().rollback();
            return TransferStatus.REJECTED;
        }
    }
}
