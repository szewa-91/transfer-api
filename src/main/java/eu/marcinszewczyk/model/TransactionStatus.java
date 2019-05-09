package eu.marcinszewczyk.model;

import javax.persistence.Entity;

public enum TransactionStatus {
    CREATED, ISSUED, COMPLETED, REJECTED
}
