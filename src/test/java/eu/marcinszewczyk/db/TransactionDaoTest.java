package eu.marcinszewczyk.db;

import com.j256.ormlite.dao.Dao;
import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.model.TransactionStatus;
import eu.marcinszewczyk.util.DbTestUtil;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionDaoTest {
    private static final String PAYER_ACCOUNT_NUMBER = "1231231";
    private static final String RECEIVER_ACCOUNT_NUMBER = "321321321";
    private static final String AMOUNT_STRING = "33.52";
    private static final Transaction EXISTING_TRANSACTION_1 = transaction("1234567", "7654321", "21.20");
    private static final Transaction EXISTING_TRANSACTION_2 = transaction("7654321", "1234567", "14.20");
    private static final Transaction EXISTING_TRANSACTION_3 = transaction("1234567", "7654765", "200000.20");
    private Dao<Transaction, Long> transactionDao;

    @Before
    public void setUp() throws Exception {
        transactionDao = DbTestUtil.getTestDbFactory().getDaos().getTransactionDao();
        transactionDao.create(EXISTING_TRANSACTION_1);
        transactionDao.create(EXISTING_TRANSACTION_2);
        transactionDao.create(EXISTING_TRANSACTION_3);
    }

    @Test
    public void shouldGetAll() throws SQLException {
        List<Transaction> transactions = transactionDao.queryForAll();

        assertThat(transactions).hasSize(3);
    }

    @Test
    public void shouldGetById() throws SQLException {
        Transaction transaction = transactionDao.queryForId(1L);

        assertThat(transaction).isEqualToComparingFieldByField(EXISTING_TRANSACTION_1);
    }

    @Test
    public void shouldSaveWithNextId() throws SQLException {
        transactionDao.create(transaction(PAYER_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER, AMOUNT_STRING));

        long expectedId = 4L;
        Transaction transaction = transactionDao.queryForId(expectedId);

        assertThat(transaction).extracting(
                Transaction::getId,
                Transaction::getPayerAccountNumber,
                Transaction::getReceiverAccountNumber,
                Transaction::getAmount
        ).containsExactly(
                expectedId, PAYER_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER, new BigDecimal(AMOUNT_STRING)
        );
    }

    private static Transaction transaction(String payerAccountNumber, String receiverAccountNumber, String amount) {
        Transaction transaction = new Transaction();
        transaction.setPayerAccountNumber(payerAccountNumber);
        transaction.setPayerAccountNumber(payerAccountNumber);
        transaction.setReceiverAccountNumber(receiverAccountNumber);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setCurrencyCode("PLN");
        transaction.setStatus(TransactionStatus.CREATED);
        return transaction;
    }
}
