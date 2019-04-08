package Connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * The class Cleaner is used when closing statements, resultsets and connections.
 */

public class Cleaner {

    /**
     * The method closes a statement, resultset and connection.
     */
    public static void close(Statement statement, ResultSet resultset, Connection connection){
        try {
            if (resultset != null) resultset.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * The method closes a preparedstatement, resultset and connection.
     */

    public static void close(PreparedStatement statement, ResultSet resultset, Connection connection){
        try {
            if (resultset != null) resultset.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
