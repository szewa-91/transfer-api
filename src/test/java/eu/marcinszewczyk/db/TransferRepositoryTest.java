package eu.marcinszewczyk.db;

import eu.marcinszewczyk.model.Transfer;
import eu.marcinszewczyk.model.TransferStatus;
import eu.marcinszewczyk.util.DbTestUtil;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.marcinszewczyk.model.TransferStatus.COMPLETED;
import static eu.marcinszewczyk.model.TransferStatus.CREATED;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferRepositoryTest {
    private static final String PAYER_ACCOUNT_NUMBER = "1231231";
    private static final String RECEIVER_ACCOUNT_NUMBER = "321321321";
    private static final String AMOUNT_STRING = "33.52";
    private TransferRepository transferRepository;

    @Before
    public void setUp() {
        DbFactory dbFactory = DbTestUtil.getTestDbFactory();
        transferRepository = dbFactory.getTransferRepository();

        transferRepository.save(transfer("1234567", "7654321", "21.20"));
        transferRepository.save(transfer("7654321", "1234567", "14.20"));
        transferRepository.save(transfer("1234567", "7654765", "200000.20"));
    }

    @Test
    public void shouldGetAll() {
        Collection<Transfer> transfers = transferRepository.findAll();

        assertThat(transfers).hasSize(3);
    }

    @Test
    public void shouldGetById() {
        Transfer transfer = transferRepository.findById(1L);

        assertThat(transfer).extracting(
                Transfer::getId,
                Transfer::getPayerAccountNumber,
                Transfer::getReceiverAccountNumber,
                Transfer::getAmount
        ).containsExactly(
                1L, "1234567", "7654321", new BigDecimal("21.20")
        );
    }

    @Test
    public void shouldHandleParallelTransferSave() {
        List<Transfer> transfers = Stream.of(
                transfer(PAYER_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER, "1.0"),
                transfer(PAYER_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER, "2.0"),
                transfer(PAYER_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER, "3.0"),
                transfer(PAYER_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER, "4.0")
        )
                .parallel().map(transfer -> transferRepository.save(transfer))
                .collect(Collectors.toList());
        assertThat(transfers).extracting(Transfer::getStatus).containsExactly(
                CREATED, CREATED, CREATED, CREATED
        );

    }

    private static Transfer transfer(String payerAccountNumber, String receiverAccountNumber, String amount) {
        Transfer transfer = new Transfer();
        transfer.setPayerAccountNumber(payerAccountNumber);
        transfer.setPayerAccountNumber(payerAccountNumber);
        transfer.setReceiverAccountNumber(receiverAccountNumber);
        transfer.setAmount(new BigDecimal(amount));
        transfer.setCurrencyCode("PLN");
        transfer.setStatus(CREATED);
        return transfer;
    }
}
