package eu.marcinszewczyk.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.math.BigDecimal;

@Entity

public class Account {
    @Id
    @Column(nullable = false)
    private String accountNumber;
    @Column(nullable = false)
    private BigDecimal balance;
    @Column(nullable = false)
    private String currencyCode;

    @Version
    @Column(name = "version")
    private int version;

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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
