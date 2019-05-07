package eu.marcinszewczyk.rest;

import eu.marcinszewczyk.db.DbFactory;
import eu.marcinszewczyk.rest.RestTestUtil.ResponseWrapper;
import eu.marcinszewczyk.server.JettyServer;
import eu.marcinszewczyk.services.ServiceProvider;
import eu.marcinszewczyk.util.DbTestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static eu.marcinszewczyk.util.DbTestUtil.PORT;
import static org.assertj.core.api.Assertions.assertThat;

public class E2ETest {

    private static JettyServer server;

    @BeforeClass
    public static void setUp() throws IOException, SQLException {
        DbFactory dbFactory = DbTestUtil.getTestDbFactory();

        server = new JettyServer(PORT, new ServiceProvider(dbFactory));
        server.start();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        server.stop();
    }

    @Test
    public void shouldSuccessfullyPostAndGetEntity() throws IOException, URISyntaxException {
        String transaction = "{" +
                "    \"payerAccountNumber\":\"123\"," +
                "    \"receiverAccountNumber\":\"432\"," +
                "    \"amount\":128.34," +
                "    \"currencyCode\":\"PLN\"," +
                "    \"status\":\"CREATED\"" +
                "  }";
        String expected = "[" +
                "{" +
                "   \"id\":1," +
                "   \"payerAccountNumber\":\"123\"," +
                "   \"receiverAccountNumber\":\"432\"," +
                "   \"amount\":128.34," +
                "   \"currencyCode\":\"PLN\"," +
                "   \"status\":\"CREATED\"" +
                "}]";

        RestTestUtil.post("http://localhost:9292/transactions", transaction);
        ResponseWrapper response = RestTestUtil.get("http://localhost:9292/transactions/");

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualToIgnoringWhitespace(expected);
    }

}