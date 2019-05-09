package eu.marcinszewczyk.db;

import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.model.TransactionStatus;
import eu.marcinszewczyk.util.DbTestUtil;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionRepositoryTest {
    private static final String PAYER_ACCOUNT_NUMBER = "1231231";
    private static final String RECEIVER_ACCOUNT_NUMBER = "321321321";
    private static final String AMOUNT_STRING = "33.52";
    private TransactionRepository transactionRepository;

    @Before
    public void setUp() {
        DbFactory dbFactory = DbTestUtil.getTestDbFactory();
        transactionRepository = dbFactory.getTransactionRepository();

        transactionRepository.save(transaction("1234567", "7654321", "21.20"));
        transactionRepository.save(transaction("7654321", "1234567", "14.20"));
        transactionRepository.save(transaction("1234567", "7654765", "200000.20"));
    }

    @Test
    public void shouldGetAll() throws SQLException {
        Collection<Transaction> transactions = transactionRepository.findAll();

        assertThat(transactions).hasSize(3);
    }

    @Test
    public void shouldGetById() throws SQLException {
        Transaction transaction = transactionRepository.findById(1L);

        assertThat(transaction).extracting(
                Transaction::getId,
                Transaction::getPayerAccountNumber,
                Transaction::getReceiverAccountNumber,
                Transaction::getAmount
        ).containsExactly(
                1L, "1234567", "7654321", new BigDecimal("21.20")
        );
    }

    @Test
    public void shouldSaveWithNextId() throws SQLException {
        Transaction transaction = transaction(PAYER_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER, AMOUNT_STRING);
        transactionRepository.save(transaction);

        long expectedId = 4L;
//        Transaction transaction = transactionRepository.findById(expectedId);

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
