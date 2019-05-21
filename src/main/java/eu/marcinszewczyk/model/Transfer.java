package eu.marcinszewczyk.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Transfer {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String payerAccountNumber;
    @Column(nullable = false)
    private String receiverAccountNumber;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String currencyCode;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;
//    @Column(nullable = false)
//    private LocalDate valueDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPayerAccountNumber() {
        return payerAccountNumber;
    }

    public void setPayerAccountNumber(String payerAccountNumber) {
        this.payerAccountNumber = payerAccountNumber;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public TransferStatus getStatus() {
        return status;
    }

//    public LocalDate getValueDate() {
//        return valueDate;
//    }
//
//    public void setValueDate(LocalDate valueDate) {
//        this.valueDate = valueDate;
//    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", payerAccountNumber='" + payerAccountNumber + '\'' +
                ", receiverAccountNumber='" + receiverAccountNumber + '\'' +
                ", amount=" + amount +
                ", currencyCode='" + currencyCode + '\'' +
                ", status=" + status +
//                ", valueDate=" + valueDate +
                '}';
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

}