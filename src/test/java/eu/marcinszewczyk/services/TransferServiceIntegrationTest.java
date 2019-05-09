package eu.marcinszewczyk.services;

import eu.marcinszewczyk.db.AccountRepository;
import eu.marcinszewczyk.db.DbFactory;
import eu.marcinszewczyk.db.TransferRepository;
import eu.marcinszewczyk.model.Account;
import eu.marcinszewczyk.model.Transfer;
import eu.marcinszewczyk.model.TransferStatus;
import eu.marcinszewczyk.util.DbTestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferServiceIntegrationTest {
    private static final BigDecimal BALANCE_1 = new BigDecimal("100.00");
    private static final BigDecimal BALANCE_2 = new BigDecimal("50.00");
    private static final Account ACCOUNT_1 = account("1234", BALANCE_1, "USD");
    private static final Account ACCOUNT_2 = account("5678", BALANCE_2, "USD");

    private AccountRepository accountRepository;
    private TransferService transferService;

    @Before
    public void setUp() {
        DbFactory dbFactory = DbTestUtil.getTestDbFactory();
        accountRepository = dbFactory.getAccountRepository();
        TransferRepository transferRepository = dbFactory.getTransferRepository();

        accountRepository.save(ACCOUNT_1);
        accountRepository.save(ACCOUNT_2);

        transferService = new TransferServiceImpl(transferRepository, accountRepository);
    }

    @Test
    public void shouldPerformTransfer() throws SQLException {
        BigDecimal transferAmount = new BigDecimal("70");
        Transfer transfer = transfer(
                ACCOUNT_1.getAccountNumber(),
                ACCOUNT_2.getAccountNumber(),
                transferAmount);

        Transfer result = transferService.executeTransfer(transfer);

        assertThat(result.getStatus()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(accountRepository.findById(ACCOUNT_1.getAccountNumber()).getBalance())
                .isEqualByComparingTo(BALANCE_1.subtract(transferAmount));
        assertThat(accountRepository.findById(ACCOUNT_2.getAccountNumber()).getBalance())
                .isEqualByComparingTo(BALANCE_2.add(transferAmount));
    }

    @Test
    public void shouldNotPerformTransfer() throws SQLException {
        Transfer transfer = transfer(
                ACCOUNT_1.getAccountNumber(),
                ACCOUNT_2.getAccountNumber(),
                new BigDecimal("170"));

        Transfer result = transferService.executeTransfer(transfer);

        assertThat(result.getStatus()).isEqualTo(TransferStatus.REJECTED);
        assertThat(accountRepository.findById(ACCOUNT_1.getAccountNumber()).getBalance())
                .isEqualByComparingTo(BALANCE_1);
        assertThat(accountRepository.findById(ACCOUNT_2.getAccountNumber()).getBalance())
                .isEqualByComparingTo(BALANCE_2);
    }

    private static Transfer transfer(String payerAccountNumber, String receiverAccountNumber, BigDecimal amount) {
        Transfer transfer = new Transfer();
        transfer.setPayerAccountNumber(payerAccountNumber);
        transfer.setPayerAccountNumber(payerAccountNumber);
        transfer.setReceiverAccountNumber(receiverAccountNumber);
        transfer.setAmount(amount);
        transfer.setCurrencyCode("PLN");
        transfer.setStatus(TransferStatus.CREATED);
        return transfer;
    }

    private static Account account(String accountNumber, BigDecimal balance, String currencyCode) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setCurrencyCode(currencyCode);
        return account;
    }
}