package eu.marcinszewczyk.services;

import eu.marcinszewczyk.model.Transfer;

import java.sql.SQLException;
import java.util.Collection;

public interface TransferService {
    Collection<Transfer> getAllTransfers();

    Transfer getTransfer(Long id);

    Transfer executeTransfer(Transfer transfer);
}
