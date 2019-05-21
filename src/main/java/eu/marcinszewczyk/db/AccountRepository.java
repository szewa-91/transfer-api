package eu.marcinszewczyk.db;

import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.model.Transfer;
import eu.marcinszewczyk.model.TransferStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Account savedAccount = entityManager.merge(payerAccount);
        transaction.commit();
        return savedAccount;
    }

    public TransferStatus performMovement(Transfer transfer) {
        EntityManager entityManager = entityManagerProvider.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            Account payerAccount = entityManager.find(Account.class, transfer.getPayerAccountNumber());
            if (payerAccount == null) {
                System.out.println("Transfer not executed. No account found: " + transfer.getPayerAccountNumber()
                        + "Transfer rejected: " + transfer);
                transaction.rollback();
                return TransferStatus.REJECTED;
            }

            Account receiverAccount = entityManager.find(Account.class, transfer.getReceiverAccountNumber());
            if (receiverAccount == null) {
                System.out.println("Transfer not executed. No account found: " + transfer.getReceiverAccountNumber()
                        + "Transfer rejected: " + transfer);
                transaction.rollback();
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
                transaction.commit();

                System.out.println("Payer account: " + payerAccount.getAccountNumber() +
                        " has balance: " + payerAccount.getBalance() + " after transfer.");
                System.out.println("Receiver account: " + receiverAccount.getAccountNumber() +
                        " has balance: " + receiverAccount.getBalance() + " after transfer.");
                System.out.println("Transfer executed: " + transfer);
                return TransferStatus.COMPLETED;
            } else {
                System.out.println("Transfer not executed. No money on the payer account: " + transfer);
                transaction.rollback();
                return TransferStatus.REJECTED;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            transaction.rollback();
            return TransferStatus.REJECTED;
        }
    }
}
