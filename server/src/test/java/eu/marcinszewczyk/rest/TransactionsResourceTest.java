package eu.marcinszewczyk.rest;

import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.model.TransactionDirection;
import eu.marcinszewczyk.rest.RestTestUtil.ResponseWrapper;
import eu.marcinszewczyk.server.JettyServer;
import eu.marcinszewczyk.services.ServiceProvider;
import eu.marcinszewczyk.services.TransactionsService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionsResourceTest {
    private static JettyServer server;

    private static TransactionsService transactionsService = mock(TransactionsService.class);
    private static final Transaction TRANSACTION_1 =
            transaction(1L, "128.34", TransactionDirection.RECEIVE, "123");
    private static final Transaction TRANSACTION_2 =
            transaction(2L, "43.12", TransactionDirection.PAY, "432");

    @BeforeClass
    public static void setUp() {
        ServiceProvider serviceProvider = mock(ServiceProvider.class);
        server = new JettyServer(serviceProvider);
        when(serviceProvider.getTransactionsService()).thenReturn(transactionsService);
        when(transactionsService.getAllTransactions()).thenReturn(asList(TRANSACTION_1, TRANSACTION_2));

        server.start();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        server.stop();
    }

    @Test
    public void shouldReturnTransactions() throws IOException, URISyntaxException {
        ResponseWrapper response = RestTestUtil.get("http://localhost:9090/transactions");
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualToIgnoringWhitespace("[" +
                "{" +
                "    \"id\":1," +
                "    \"accountNumber\":\"123\"," +
                "    \"direction\":\"RECEIVE\"," +
                "    \"amount\":128.34" +
                "  }," +
                "  {" +
                "    \"id\":2," +
                "    \"accountNumber\":\"432\"," +
                "    \"direction\":\"PAY\"," +
                "    \"amount\":43.12" +
                "  }" +
                "]");

    }

    @Test
    public void shouldPost() throws IOException, URISyntaxException {
        String transactionString = "{" +
                "    \"id\":1," +
                "    \"accountNumber\":\"123\"," +
                "    \"direction\":\"RECEIVE\"," +
                "    \"amount\":128.34" +
                "  }";

        ResponseWrapper response = RestTestUtil.post("http://localhost:9090/transactions", transactionString);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualToIgnoringWhitespace(transactionString);
    }

    private static Transaction transaction(long id, String amount, TransactionDirection direction, String accountNumber) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setDirection(direction);
        transaction.setAccountNumber(accountNumber);
        return transaction;
    }
}