package Connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The class Connection is used when creating a connection to the database.
 */

public class ConnectionPool {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:mysql://mysql.stud.iie.ntnu.no:3306/iaevange");
        config.setUsername("iaevange");
        config.setPassword("53BJMtne");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    /**
     * The static method is used when creating a connection
     * @return the connection.
     */
    public static Connection getConnection() throws SQLException{
        return ds.getConnection();
    }
}
