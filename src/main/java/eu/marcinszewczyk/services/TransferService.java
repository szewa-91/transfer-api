package eu.marcinszewczyk.services;

import eu.marcinszewczyk.model.Transfer;

import java.sql.SQLException;
import java.util.Collection;

public interface TransferService {
    Collection<Transfer> getAllTransfers() throws SQLException;

    Transfer getTransfer(Long id) throws SQLException;

    Transfer executeTransfer(Transfer transfer) throws SQLException;
}
