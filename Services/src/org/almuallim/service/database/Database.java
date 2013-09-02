package org.almuallim.service.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Naveed Quadri
 */
public interface Database extends AutoCloseable {

    /**
     * gets the connection from database.
     * @return a new connection to database
     * @throws SQLException 
     */
    public Connection getConnection() throws SQLException;
}
