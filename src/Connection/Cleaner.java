package Connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Cleaner {
    public Cleaner(){}

    public void close(Statement statement, ResultSet resultset, Connection connection){
        try {
            if (statement != null) statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultset != null) resultset.close();
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

    public void close(PreparedStatement statement, ResultSet resultset, Connection connection){
        try {
            if (statement != null) statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultset != null) resultset.close();
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
