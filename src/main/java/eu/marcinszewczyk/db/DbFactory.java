package eu.marcinszewczyk.db;

import java.io.IOException;
import java.sql.SQLException;

public interface DbFactory {
    DaoProvider getDaos() throws SQLException, IOException;
}
