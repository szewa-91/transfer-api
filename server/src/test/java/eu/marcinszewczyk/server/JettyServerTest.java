package eu.marcinszewczyk.server;

import eu.marcinszewczyk.model.Transaction;
import eu.marcinszewczyk.model.TransactionDirection;
import eu.marcinszewczyk.services.ServiceProvider;
import eu.marcinszewczyk.services.TransactionsService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JettyServerTest {
    private static JettyServer server;

    private static TransactionsService transactionsService = mock(TransactionsService.class);
    private static final Transaction TRANSACTION_1 =
            transaction(1L, "128.34", TransactionDirection.RECEIVE, "123");
    private static final Transaction TRANSACTION_2 =
            transaction(2L, "43.12", TransactionDirection.PAY, "432");

    @BeforeClass
    public static void setUp() throws Exception {
        ServiceProvider serviceProvider = mock(ServiceProvider.class);
        server = new JettyServer(serviceProvider);
        when(serviceProvider.getTransactionsService()).thenReturn(transactionsService);
        when(transactionsService.getAllTransactions()).thenReturn(asList(TRANSACTION_1, TRANSACTION_2));

        new Thread(() -> server.start()).run();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        server.stop();
    }

    @Test
    public void shouldReturnTransactions() throws IOException, URISyntaxException {
        String url = "/transactions";
        HttpURLConnection http = (HttpURLConnection) new URI("http://localhost:9090").resolve(url).toURL().openConnection();
        http.connect();
        BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String body = br.lines().collect(Collectors.joining());
        assertThat(http.getResponseCode()).isEqualTo(200);
        assertThat(body).isEqualToIgnoringWhitespace("[" +
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

    private static Transaction transaction(long id, String amount, TransactionDirection direction, String accountNumber) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setDirection(direction);
        transaction.setAccountNumber(accountNumber);
        return transaction;
    }
}