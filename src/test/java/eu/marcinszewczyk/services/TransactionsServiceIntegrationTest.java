package eu.marcinszewczyk.services;

import eu.marcinszewczyk.db.AccountRepository;
import eu.marcinszewczyk.db.DbFactory;
import eu.marcinszewczyk.db.DbFactoryImpl;
import eu.marcinszewczyk.db.TransactionRepository;
import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.model.TransactionStatus;
import eu.marcinszewczyk.util.DbTestUtil;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionsServiceIntegrationTest {
    private static final BigDecimal BALANCE_1 = new BigDecimal("100.00");
    private static final BigDecimal BALANCE_2 = new BigDecimal("50.00");
    private static final Account ACCOUNT_1 = account("1234", BALANCE_1, "USD");
    private static final Account ACCOUNT_2 = account("5678", BALANCE_2, "USD");

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private TransactionsService transactionsService;

    @Before
    public void setUp() throws IOException, SQLException {
        DbFactory dbFactory = DbTestUtil.getTestDbFactory();
        accountRepository = dbFactory.getAccountRepository();
        transactionRepository = dbFactory.getTransactionRepository();

        accountRepository.save(ACCOUNT_1);
        accountRepository.save(ACCOUNT_2);

        transactionsService = new TransactionsServiceImpl(transactionRepository, accountRepository);
    }

    @Test
    public void shouldPerformTransaction() throws SQLException {
        BigDecimal transferAmount = new BigDecimal("70");
        Transaction transaction = transaction(
                ACCOUNT_1.getAccountNumber(),
                ACCOUNT_2.getAccountNumber(),
                transferAmount);

        Transaction result = transactionsService.executeTransaction(transaction);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(accountRepository.findById(ACCOUNT_1.getAccountNumber()).getBalance())
                .isEqualByComparingTo(BALANCE_1.subtract(transferAmount));
        assertThat(accountRepository.findById(ACCOUNT_2.getAccountNumber()).getBalance())
                .isEqualByComparingTo(BALANCE_2.add(transferAmount));
    }

    @Test
    public void shouldNotPerformTransaction() throws SQLException {
        Transaction transaction = transaction(
                ACCOUNT_1.getAccountNumber(),
                ACCOUNT_2.getAccountNumber(),
                new BigDecimal("170"));

        Transaction result = transactionsService.executeTransaction(transaction);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.REJECTED);
        assertThat(accountRepository.findById(ACCOUNT_1.getAccountNumber()).getBalance())
                .isEqualByComparingTo(BALANCE_1);
        assertThat(accountRepository.findById(ACCOUNT_2.getAccountNumber()).getBalance())
                .isEqualByComparingTo(BALANCE_2);
    }

    private static Transaction transaction(String payerAccountNumber, String receiverAccountNumber, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setPayerAccountNumber(payerAccountNumber);
        transaction.setPayerAccountNumber(payerAccountNumber);
        transaction.setReceiverAccountNumber(receiverAccountNumber);
        transaction.setAmount(amount);
        transaction.setCurrencyCode("PLN");
        transaction.setStatus(TransactionStatus.CREATED);
        return transaction;
    }

    private static Account account(String accountNumber, BigDecimal balance, String currencyCode) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setCurrencyCode(currencyCode);
        return account;
    }
}