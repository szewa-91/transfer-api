package eu.marcinszewczyk.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigDecimal;

public class Account {
    @Id
    @Column(nullable = false)
    private String accountNumber;
    @Column(nullable = false)
    private BigDecimal balance;
    @Column(nullable = false)
    private String currencyCode;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean hasAmount(BigDecimal amount) {
        return getBalance().compareTo(amount) >= 0;
    }

    public void subtractFromBalance(BigDecimal amount) {
        setBalance(getBalance().subtract(amount));
    }

    public void addToBalance(BigDecimal amount) {
        setBalance(getBalance().add(amount));
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
