/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.database.h2;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import org.almuallim.service.database.Database;
import org.almuallim.service.helpers.Application;
import org.h2.jdbcx.JdbcConnectionPool;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Naveed
 */
//@ServiceProviders(value = {
//    ,
//    @ServiceProvider(service = SearchProvider.class)
//})
@ServiceProvider(service = Database.class)
public class H2Database implements Database {

    private static final String DATABASE_NAME = "almuallim";
    private static JdbcConnectionPool connectionPool;
    private Map<String, String> properties;

    @Override
    public Connection getConnection() throws SQLException {
        if (connectionPool == null) {
            connectionPool = JdbcConnectionPool.create(String.format("jdbc:h2:%s%s", getDatabasePath(), DATABASE_NAME), DATABASE_NAME, DATABASE_NAME);
        }
        return connectionPool.getConnection();
    }

    private String getDatabasePath() {
        return Application.getHome() + File.separator + "data" + File.separator;
    }

    @Override
    public void close() throws Exception {
        if (connectionPool != null) {
            connectionPool.dispose();
        }
    }
}
