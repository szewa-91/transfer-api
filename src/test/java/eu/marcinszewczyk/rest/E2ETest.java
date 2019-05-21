package eu.marcinszewczyk.rest;

import eu.marcinszewczyk.db.AccountRepository;
import eu.marcinszewczyk.db.DbFactory;
import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.rest.RestTestUtil.ResponseWrapper;
import eu.marcinszewczyk.server.JettyServer;
import eu.marcinszewczyk.services.ServiceProvider;
import eu.marcinszewczyk.util.DbTestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static eu.marcinszewczyk.util.DbTestUtil.PORT;
import static org.assertj.core.api.Assertions.assertThat;

public class E2ETest {
    private static final BigDecimal BALANCE_1 = new BigDecimal("100.00");
    private static final BigDecimal BALANCE_2 = new BigDecimal("50.00");
    private static final Account ACCOUNT_1 = account("1234", BALANCE_1, "USD");
    private static final Account ACCOUNT_2 = account("5678", BALANCE_2, "USD");

    private static JettyServer server;

    @BeforeClass
    public static void setUp() throws IOException, SQLException {
        DbFactory dbFactory = DbTestUtil.getTestDbFactory();

        AccountRepository accountRepository = dbFactory.getAccountRepository();
        accountRepository.save(ACCOUNT_1);
        accountRepository.save(ACCOUNT_2);

        server = new JettyServer(PORT, new ServiceProvider(dbFactory));
        server.start();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        server.stop();
    }

    @Test
    public void shouldPerformTransactionAccounts() throws IOException, URISyntaxException {
        String transfer = "{" +
                "    \"payerAccountNumber\":\"1234\"," +
                "    \"receiverAccountNumber\":\"5678\"," +
                "    \"amount\":50.00," +
                "    \"currencyCode\":\"PLN\"" +
                "  }";
        String expected = "[" +
                "{" +
                "   \"id\":1," +
                "   \"payerAccountNumber\":\"1234\"," +
                "   \"receiverAccountNumber\":\"5678\"," +
                "   \"amount\":50.00," +
                "   \"currencyCode\":\"PLN\"," +
                "   \"status\":\"COMPLETED\"" +
                "}]";

        RestTestUtil.post("http://localhost:9292/transfers", transfer);
        ResponseWrapper response = RestTestUtil.get("http://localhost:9292/transfers/");

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualToIgnoringWhitespace(expected);
    }

    private static Account account(String accountNumber, BigDecimal balance, String currencyCode) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setCurrencyCode(currencyCode);
        return account;
    }

}