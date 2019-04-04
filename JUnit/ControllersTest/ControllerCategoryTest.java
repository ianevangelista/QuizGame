package Controllers;


import Connection.Cleaner;
import Connection.ConnectionPool;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;


public class ControllerCategoryTest {

    ControllerCategory cc = new ControllerCategory();
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;

    @Test
    public void chooseCategory1Test() {
        ActionEvent event = new ActionEvent();
        cc.initialize();
        boolean ans = cc.chooseCategory1(event);
        assertTrue(ans);
    }

    @Test
    public void chooseCategory2Test() {
        ActionEvent event = new ActionEvent();
        cc.initialize();
        boolean ans = cc.chooseCategory2(event);
        assertTrue(ans);
    }

    @Test
    public void chooseCategory3Test() {
        ActionEvent event = new ActionEvent();
        cc.initialize();
        boolean ans = cc.chooseCategory3(event);
        assertTrue(ans);
    }

    @Test
    public void updateCategoryTest() {
        int category = 1;
        int gameId = 1;
        String sql = "INSERT INTO Game(gameId) VALUES (" + gameId + ");";
        String sqlCategory = "SELECT categoryId FROM Game WHERE gameId=" + gameId + ";";
        String sqlDelete = "DELETE FROM Game WHERE gameId=" + gameId + ";";
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            // Adds questions from the category to the game
            boolean resultat = cc.updateCategory(category, gameId);
            rs = statement.executeQuery(sqlCategory);
            if(rs.next()) {
                if(rs.getInt(1) ==category) {
                    statement.executeUpdate(sqlDelete);
                    assertTrue(resultat);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            Cleaner.close(statement, null, connection);
        }

    }

    @Test
    public void initialize() {
        boolean ans = cc.initialize();
        assertTrue(ans);
    }
}