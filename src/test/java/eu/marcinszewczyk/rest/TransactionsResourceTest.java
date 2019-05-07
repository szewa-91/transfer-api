package eu.marcinszewczyk.rest;

import eu.marcinszewczyk.model.Transaction;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionsResourceTest {
    private final static int PORT = 9292;
    private static JettyServer server;

    private static TransactionsService transactionsService = mock(TransactionsService.class);
    private static final Transaction TRANSACTION_1 =
            transaction(1L, "128.34", "123", "432");
    private static final Transaction TRANSACTION_2 =
            transaction(2L, "43.12", "432", "123");

    @BeforeClass
    public static void setUp() {
        ServiceProvider serviceProvider = mock(ServiceProvider.class);
        server = new JettyServer(PORT, serviceProvider);
        when(serviceProvider.getTransactionsService()).thenReturn(transactionsService);
        when(transactionsService.getAllTransactions()).thenReturn(asList(TRANSACTION_1, TRANSACTION_2));
        when(transactionsService.saveTransaction(any())).thenAnswer(
                invocationOnMock -> invocationOnMock.getArgument(0));

        server.start();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        server.stop();
    }

    @Test
    public void shouldReturnTransactions() throws IOException, URISyntaxException {
        ResponseWrapper response = RestTestUtil.get("http://localhost:9292/transactions");
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualToIgnoringWhitespace("[" +
                "{" +
                "    \"id\":1," +
                "    \"payerAccountNumber\":\"123\"," +
                "    \"receiverAccountNumber\":\"432\"," +
                "    \"amount\":128.34," +
                "    \"currencyCode\":null," +
                "    \"status\":null" +
                "  }," +
                "  {" +
                "    \"id\":2," +
                "    \"payerAccountNumber\":\"432\"," +
                "    \"receiverAccountNumber\":\"123\"," +
                "    \"amount\":43.12," +
                "    \"currencyCode\":null," +
                "    \"status\":null" +
                "  }" +
                "]");

    }

    @Test
    public void shouldPost() throws IOException, URISyntaxException {
        String transactionString = "{" +
                "    \"id\":1," +
                "    \"payerAccountNumber\":\"123\"," +
                "    \"receiverAccountNumber\":\"432\"," +
                "    \"amount\":128.34," +
                "    \"currencyCode\":null," +
                "    \"status\":null" +
                "  }";

        ResponseWrapper response = RestTestUtil.post("http://localhost:9292/transactions", transactionString);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualToIgnoringWhitespace(transactionString);
    }

    private static Transaction transaction(long id, String amount, String payerAccountNumber, String receiverAccountNumber) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setPayerAccountNumber(payerAccountNumber);
        transaction.setReceiverAccountNumber(receiverAccountNumber);
        return transaction;
    }
}
