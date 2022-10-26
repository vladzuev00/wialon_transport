package by.zuevvlad.wialontransport.dao.dbconnectionpool;

import java.sql.Connection;
import java.util.Optional;

public interface DataBaseConnectionPool extends AutoCloseable {
    int findAmountOfAvailableConnections();
    Optional<Connection> findAvailableConnection();
    void returnConnectionToPool(final Connection returnedConnection);
}
