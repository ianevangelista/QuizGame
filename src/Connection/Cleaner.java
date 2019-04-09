package Connection;

import java.sql.*;

/**
 * The class Cleaner is used when closing statements, resultsets and connections.
 */

public class Cleaner {

    /**
     * The method closes a statement, resultset and connection.
     */
    public static void close(Statement statement, ResultSet resultset, Connection connection){
        try {
            // Closes connection, statement and resultset
            if (resultset != null) resultset.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch(SQLException sqle){
            // Database access error
            System.out.println("Database access error");
            sqle.printStackTrace();
        } catch (Exception e) {
            // If something else goes wrong
            e.printStackTrace();
        }
    }

    /**
     * The method closes a preparedstatement, resultset and connection.
     */

    public static void close(PreparedStatement statement, ResultSet resultset, Connection connection){
        try {
            if (resultset != null) resultset.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch(SQLException sqle){
            // Database access error
            System.out.println("Database access error");
            sqle.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
