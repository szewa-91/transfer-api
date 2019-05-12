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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.marcinszewczyk.model.TransferStatus.CREATED;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferServiceIntegrationTest {
    private static final BigDecimal BALANCE_1 = new BigDecimal("100.00");
    private static final BigDecimal BALANCE_2 = new BigDecimal("50.00");
    private static final String ACCOUNT_NUMBER_1 = "1234";
    private static final String ACCOUNT_NUMBER_2 = "5678";
    private static final String NON_EXISTENT_ACCOUNT_NUMBER_1 = "1799";
    private static final String NON_EXISTENT_ACCOUNT_NUMBER_2 = "1800";

    private AccountRepository accountRepository;
    private TransferService transferService;

    @Before
    public void setUp() {
        DbFactory dbFactory = DbTestUtil.getTestDbFactory();
        accountRepository = dbFactory.getAccountRepository();
        TransferRepository transferRepository = dbFactory.getTransferRepository();

        accountRepository.save(account(ACCOUNT_NUMBER_1, BALANCE_1, "USD"));
        accountRepository.save(account(ACCOUNT_NUMBER_2, BALANCE_2, "USD"));

        transferService = new TransferServiceImpl(transferRepository, accountRepository);
    }

    @Test
    public void shouldPerformTransfer() throws SQLException {
        BigDecimal transferAmount = new BigDecimal("70");
        Transfer transfer = transfer(
                account(ACCOUNT_NUMBER_1, BALANCE_1, "USD").getAccountNumber(),
                account(ACCOUNT_NUMBER_2, BALANCE_2, "USD").getAccountNumber(),
                transferAmount);

        Transfer result = transferService.executeTransfer(transfer);

        assertThat(transferService.getAllTransfers()).hasSize(1);
        assertThat(result.getStatus()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(accountRepository.findById(ACCOUNT_NUMBER_1).getBalance())
                .isEqualByComparingTo(BALANCE_1.subtract(transferAmount));
        assertThat(accountRepository.findById(ACCOUNT_NUMBER_2).getBalance())
                .isEqualByComparingTo(BALANCE_2.add(transferAmount));
    }

    @Test
    public void shouldNotPerformTransferIfNotSufficientBalance() throws SQLException {
        Transfer transfer = transfer(
                account(ACCOUNT_NUMBER_1, BALANCE_1, "USD").getAccountNumber(),
                account(ACCOUNT_NUMBER_2, BALANCE_2, "USD").getAccountNumber(),
                new BigDecimal("170"));

        Transfer result = transferService.executeTransfer(transfer);

        assertThat(result.getStatus()).isEqualTo(TransferStatus.REJECTED);
        assertThat(accountRepository.findById(ACCOUNT_NUMBER_1).getBalance())
                .isEqualByComparingTo(BALANCE_1);
        assertThat(accountRepository.findById(ACCOUNT_NUMBER_2).getBalance())
                .isEqualByComparingTo(BALANCE_2);
    }

    @Test
    public void shouldNotPerformTransferIfNoAccounts() throws SQLException {
        Transfer transfer = transfer(
                NON_EXISTENT_ACCOUNT_NUMBER_1,
                NON_EXISTENT_ACCOUNT_NUMBER_2,
                new BigDecimal("1"));

        Transfer result = transferService.executeTransfer(transfer);

        assertThat(result.getStatus()).isEqualTo(TransferStatus.REJECTED);
        assertThat(accountRepository.findById(account(ACCOUNT_NUMBER_1, BALANCE_1, "USD").getAccountNumber()).getBalance())
                .isEqualByComparingTo(BALANCE_1);
        assertThat(accountRepository.findById(account(ACCOUNT_NUMBER_2, BALANCE_2, "USD").getAccountNumber()).getBalance())
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