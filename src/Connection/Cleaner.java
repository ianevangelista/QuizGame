package Connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Cleaner {
    public Cleaner(){}

    public void closeStatement(Statement statement){
        try {
            if (statement != null) statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeResult(ResultSet resultset){
        try {
            if (resultset != null) resultset.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(Connection connection){
        try {
            if (connection != null) connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeTwo(Statement statement, Connection connection){
        closeStatement(statement);
        closeConnection(connection);
    }

    public void closeThree(Statement statement, ResultSet resultset, Connection connection){
        closeStatement(statement);
        closeResult(resultset);
        closeConnection(connection);
    }
}
