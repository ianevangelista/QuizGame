package Controllers;

import Connection.ConnectionPool;
import javafx.event.ActionEvent;
import org.junit.Test;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public class ControllerRefreshTest {

    Connection connection = null;
    Statement statement = null;
    ControllerRefresh cr = new ControllerRefresh();
    ControllerHome ch = new ControllerHome();

    @Test
    public void startTest() {
        String username = "juni";
        String p2 = "ian";
        int gameid = 118;
        String sqlInsertGame = "INSERT INTO Game (gameId, player1, player2, p1Points, p2Points) VALUES (" + gameid + ", '" + username + "', '" + p2 + "', 0, 0);";
        String sqlUpdate = "UPDATE Player SET gameId = " + gameid + " WHERE username = " + username + " AND username = " + p2 + ";";
        String sqlDeleteGameId = "UPDATE Player SET gameId = NULL WHERE username = " + username + " AND username = " + p2 + ";";
        String sqlDeleteGame = "DELETE Game WHERE gameId = " + gameid + ";";

        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sqlInsertGame);
            statement.executeUpdate(sqlUpdate);
            boolean result = cr.start(username);
            statement.executeUpdate(sqlDeleteGameId);
            statement.executeUpdate(sqlDeleteGame);

            assertTrue(result);

        }
        catch (SQLException e){
            e.printStackTrace();
            assertFalse(true);
        }
    }

    @Test
    public void refreshTest() {
        String username = "juni";
        int gameid = 118;
        String sqlInsertGame = "INSERT INTO Game (gameId, player1, player2, categoryId) VALUES (" + gameid + ", '" + username + "', 'ian', 1);";
        String sqlUpdate = "UPDATE Player SET gameId = " + gameid + " WHERE username = '" + username + "';";
        String sqlDelete = "UPDATE Player SET gameId = null WHERE username = '" + username + "';";
        String sqlDeleteGame = "DELETE Game WHERE gameId = " + gameid + ";";
        ActionEvent event = new ActionEvent();
        try{
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sqlInsertGame);
            statement.executeUpdate(sqlUpdate);
            int result = cr.refresh(event, username);
            int expect = 0;
            statement.executeUpdate(sqlDelete);
            statement.executeUpdate(sqlDeleteGame);
            assertEquals(result, expect);
        }
        catch (SQLException e){
            e.printStackTrace();
            assertFalse(true);
        }
    }

    @Test
    public void decline() {
        ch.setUserName("juni");
        String username =
        String sqlInsert = "INSERT INTO Game(player1, player2, p1Points, p2Points) VALUES('"+ player1 + "', '" + player2 + "', 0, 0);";

    }

    @Test
    public void sceneHome() {
    }
}