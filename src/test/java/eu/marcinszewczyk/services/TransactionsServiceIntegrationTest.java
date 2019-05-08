package eu.marcinszewczyk.services;

import com.j256.ormlite.dao.Dao;
import eu.marcinszewczyk.db.DbFactory;
import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.model.TransactionStatus;
import eu.marcinszewczyk.util.DbTestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionsServiceIntegrationTest {
    private static final BigDecimal BALANCE_1 = new BigDecimal("100.00");
    private static final BigDecimal BALANCE_2 = new BigDecimal("50.00");
    private static final Account ACCOUNT_1 = account("1234", BALANCE_1, "USD");
    private static final Account ACCOUNT_2 = account("5678", BALANCE_2, "USD");

    private Dao<Transaction, Long> transactionDao;
    private Dao<Account, String> accountDao;
    private TransactionsService transactionsService;

    @Before
    public void setUp() throws IOException, SQLException {
        DbFactory dbFactory = DbTestUtil.getTestDbFactory();

        transactionDao = dbFactory.setupDatabase().getTransactionDao();
        accountDao = dbFactory.setupDatabase().getAccountDao();

        accountDao.create(ACCOUNT_1);
        accountDao.create(ACCOUNT_2);

        transactionsService = new TransactionsServiceImpl(transactionDao, accountDao);
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
        assertThat(accountDao.queryForId(ACCOUNT_1.getAccountNumber()).getBalance())
                .isEqualByComparingTo(BALANCE_1.subtract(transferAmount));
        assertThat(accountDao.queryForId(ACCOUNT_2.getAccountNumber()).getBalance())
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
        assertThat(accountDao.queryForId(ACCOUNT_1.getAccountNumber()).getBalance())
                .isEqualByComparingTo(BALANCE_1);
        assertThat(accountDao.queryForId(ACCOUNT_2.getAccountNumber()).getBalance())
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