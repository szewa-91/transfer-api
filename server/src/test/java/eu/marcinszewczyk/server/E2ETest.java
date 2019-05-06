package eu.marcinszewczyk.server;

import eu.marcinszewczyk.rest.RestTestUtil;
import eu.marcinszewczyk.rest.RestTestUtil.ResponseWrapper;
import eu.marcinszewczyk.services.ServiceProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class E2ETest {
    private static JettyServer server;

    @BeforeClass
    public static void setUp() {
        server = new JettyServer(new ServiceProvider());
        server.start();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        server.stop();
    }

    @Test
    public void shouldSuccessfullyPostAndGetEntity() throws IOException, URISyntaxException {
        String transactionString = "{" +
                "    \"id\":621," +
                "    \"payerAccountNumber\":\"123\"," +
                "    \"receiverAccountNumber\":\"432\"," +
                "    \"amount\":128.34," +
                "    \"currencyCode\":null," +
                "    \"issueDate\":null," +
                "    \"status\":null" +
                "  }";

        RestTestUtil.post("http://localhost:9090/transactions", transactionString);
        ResponseWrapper response = RestTestUtil.get("http://localhost:9090/transactions/621");

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualToIgnoringWhitespace(transactionString);
    }

}