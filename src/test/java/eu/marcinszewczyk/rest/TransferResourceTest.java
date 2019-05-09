package eu.marcinszewczyk.rest;

import eu.marcinszewczyk.model.Transfer;
import eu.marcinszewczyk.rest.RestTestUtil.ResponseWrapper;
import eu.marcinszewczyk.server.JettyServer;
import eu.marcinszewczyk.services.ServiceProvider;
import eu.marcinszewczyk.services.TransferService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransferResourceTest {
    private final static int PORT = 9292;
    private static JettyServer server;

    private static TransferService transferService = mock(TransferService.class);
    private static final Transfer TRANSFER_1 =
            transfer(1L, "128.34", "123", "432");
    private static final Transfer TRANSFER_2 =
            transfer(2L, "43.12", "432", "123");

    @BeforeClass
    public static void setUp() throws SQLException {
        ServiceProvider serviceProvider = mock(ServiceProvider.class);
        server = new JettyServer(PORT, serviceProvider);
        when(serviceProvider.getTransferService()).thenReturn(transferService);
        when(transferService.getAllTransfers()).thenReturn(asList(TRANSFER_1, TRANSFER_2));
        when(transferService.executeTransfer(any())).thenAnswer(
                invocationOnMock -> invocationOnMock.getArgument(0));

        server.start();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        server.stop();
    }

    @Test
    public void shouldReturnTransfer() throws IOException, URISyntaxException {
        ResponseWrapper response = RestTestUtil.get("http://localhost:9292/transfers");
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
        String transferString = "{" +
                "    \"id\":1," +
                "    \"payerAccountNumber\":\"123\"," +
                "    \"receiverAccountNumber\":\"432\"," +
                "    \"amount\":128.34," +
                "    \"currencyCode\":null," +
                "    \"status\":null" +
                "  }";

        ResponseWrapper response = RestTestUtil.post("http://localhost:9292/transfers", transferString);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualToIgnoringWhitespace(transferString);
    }

    private static Transfer transfer(long id, String amount, String payerAccountNumber, String receiverAccountNumber) {
        Transfer transfer = new Transfer();
        transfer.setId(id);
        transfer.setAmount(new BigDecimal(amount));
        transfer.setPayerAccountNumber(payerAccountNumber);
        transfer.setReceiverAccountNumber(receiverAccountNumber);
        return transfer;
    }
}
